/*
 * Copyright 2021 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.streamis.jobmanager.manager.service

import com.webank.wedatasphere.streamis.jobmanager.launcher.JobLauncherAutoConfiguration
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobLaunchManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobState
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.{JobInfo, LaunchJob}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LogRequestPayload
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.FlinkJobInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.manager.FlinkJobLaunchManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state.Savepoint
import com.webank.wedatasphere.streamis.jobmanager.manager.SpringContextHolder
import com.webank.wedatasphere.streamis.jobmanager.manager.conf.JobConf
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.{StreamJobMapper, StreamTaskMapper}
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamTask
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.{JobProgressVo, StreamTaskListVo}
import com.webank.wedatasphere.streamis.jobmanager.manager.exception.{JobErrorException, JobExecuteErrorException, JobFetchErrorException, JobPauseErrorException, JobTaskErrorException}
import com.webank.wedatasphere.streamis.jobmanager.manager.scheduler.FutureScheduler
import com.webank.wedatasphere.streamis.jobmanager.manager.scheduler.events.StreamisPhaseInSchedulerEvent
import com.webank.wedatasphere.streamis.jobmanager.manager.scheduler.events.StreamisPhaseInSchedulerEvent.ScheduleCommand
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.exception.TransformFailedErrorException
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.{StreamisTransformJobBuilder, Transform}
import com.webank.wedatasphere.streamis.jobmanager.manager.util.DateUtils
import org.apache.commons.lang.StringUtils
import org.apache.ibatis.mapping.ResultMapping
import org.apache.linkis.common.utils.Logging
import org.apache.linkis.httpclient.dws.DWSHttpClient
import org.apache.linkis.scheduler.queue
import org.apache.linkis.scheduler.queue.{SchedulerEvent, SchedulerEventState}
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util
import java.util.function
import java.util.Date
import java.util.concurrent.Future
import javax.annotation.Resource
import scala.collection.JavaConverters._


@Service
class DefaultStreamTaskService extends StreamTaskService with Logging{

  @Autowired private var streamTaskMapper:StreamTaskMapper=_
  @Autowired private var streamJobMapper:StreamJobMapper=_
  @Autowired private var streamisTransformJobBuilders: Array[StreamisTransformJobBuilder] = _

  @Resource
  private var jobLaunchManager: JobLaunchManager[_ <: JobInfo] = _

  /**
   * Scheduler
   */
  @Resource
  private var scheduler: FutureScheduler = _


  /**
   * Sync to execute job(task)
   * 1) create a new task
   * 2) launch the new task
   *
   * @param jobId    job id
   * @param taskId   task id
   * @param execUser user name
   * @param restore  restore from job state
   */
  override def execute(jobId: Long, taskId: Long, execUser: String, restore: Boolean): Unit = {
    val result: Future[String] = asyncExecute(jobId, taskId, execUser, restore)
    val errorMessage = result.get()
    if (StringUtils.isNotBlank(errorMessage)){
      throw new JobExecuteErrorException(-1, s"Fail to execute StreamJob, message output: $errorMessage");
    }
  }


  override def asyncExecute(jobId: Long, taskId: Long, execUser: String, restore: Boolean): Future[String] = {
    execute(jobId, taskId, execUser, restore, new function.Function[SchedulerEvent, String] {
      override def apply(event: SchedulerEvent): String = {
        event match {
          case job: StreamisPhaseInSchedulerEvent =>
            job.getJobInfo.getOutput
          case _ => null
        }
      }
    })
  }

  def execute[T](jobId: Long, taskId: Long, execUser: String, restore: Boolean = false, returnMapping: function.Function[SchedulerEvent, T]): Future[T] = {
    val self = SpringContextHolder.getBean(classOf[DefaultStreamTaskService])
    val event = new StreamisPhaseInSchedulerEvent(if (jobId > 0) "job-" + jobId else "task-" + taskId, new ScheduleCommand {

      override def onPrepare(context: StreamisPhaseInSchedulerEvent.StateContext, scheduleJob: queue.JobInfo): Unit =  {
        var currentJobId = jobId
        if (currentJobId <= 0 ){
          // Should find the jobId
          val oldStreamTask = streamTaskMapper.getTaskById(taskId)
          if (Option(oldStreamTask).isEmpty){
            throw new JobTaskErrorException(-1, s"Cannot find the StreamTask in id: $taskId")
          }
          currentJobId = oldStreamTask.getJobId
        }
        // Assign the status STARTING default
        val streamTask = self.createTask(currentJobId, JobConf.FLINK_JOB_STATUS_STARTING.getValue, execUser)
        context.addVar("newTaskId", streamTask.getId);
      }

      override def schedule(context: StreamisPhaseInSchedulerEvent.StateContext, jobInfo: queue.JobInfo): Unit = {
         Option(context.getVar[Long]("newTaskId", classOf[Long])) match {
           case Some(newTaskId) =>
             var jobState = null
             // Means to fetch the job state from task to restore
             if (taskId > 0 && restore){
                // TODO fetch the job stage strategy
//                jobLaunchManager.getJobStateManager.getJobState(classOf[])
             }
             // Launch entrance
             launch(taskId, execUser, jobState)
           case _ => // TODO cannot find the new task id
         }

      }

      override def onErrorHandle(context: StreamisPhaseInSchedulerEvent.StateContext, scheduleJob: queue.JobInfo, t: Throwable): Unit = {
        // Change the task status
        Option(context.getVar[Long]("newTaskId", classOf[Long])) match {
          case Some(newTaskId) =>
            info(s"Error in scheduling StreamTask [$newTaskId], now try to persist the status and message output", t)
            val finalTask = new StreamTask()
            finalTask.setStatus(JobConf.FLINK_JOB_STATUS_FAILED.getValue)
            // Output message equals error message, you can use t.getMessage()
            finalTask.setErrDesc(scheduleJob.getOutput)
            if (streamTaskMapper.updateTaskInStatus(finalTask, JobConf.FLINK_JOB_STATUS_STARTING.getValue) > 0) {
              info(s"Transient the StreamTask [$newTaskId]'status from STARTING to FAILED and flush the output message.")
            }
        }
      }
    })
    scheduler.submit(event, returnMapping)
  }


  /**
   * Sync to pause job(task)
   *
   * @param jobId    job id
   * @param taskId   task id
   * @param operator user name
   */
  override def pause(jobId: Long, taskId: Long, operator: String): Unit = {

  }

  def pause[T](jobId: Long, taskId: Long, )
  def pause(jobId: Long, userName: String): Unit ={
    val job = streamJobMapper.queryAndLockJobById(jobId)
    if(job == null) throw new JobPauseErrorException(30355, s"Job-$jobId is not exists.")
    info(s"Try to stop StreamJob-${job.getName}.")
    val jobVersions = streamJobMapper.getJobVersions(jobId)
    if(jobVersions == null || jobVersions.isEmpty)
      throw new JobExecuteErrorException(30350, s"StreamJob-${job.getName} does not exists any JobVersion.")
    val oldTasks = streamTaskMapper.getTasksByJobIdAndJobVersionId(job.getId, jobVersions.get(0).getId)
    if(oldTasks == null || oldTasks.isEmpty || JobConf.isCompleted(oldTasks.get(0).getStatus))
      throw new JobPauseErrorException(30355, s"StreamJob-${job.getName} has not bean started.")
    val oldTask = oldTasks.get(0)
    info(s"Try to stop StreamJob-${job.getName} with task(taskId: ${oldTask.getId}, linkisJobId: ${oldTask.getLinkisJobId}).")
    DefaultStreamTaskService.loadStreamTask(oldTask)
    jobLaunchManager.stop(oldTask.getLinkisJobId)
    info(s"StreamJob-${job.getName} has stopped with task(taskId: ${oldTask.getId}, linkisJobId: ${oldTask.getLinkisJobId}).")
    val taskModel = new StreamTask()
    taskModel.setId(oldTask.getId)
    taskModel.setLastUpdateTime(new Date)
    taskModel.setStatus(JobConf.FLINK_JOB_STATUS_STOPPED.getValue)
    streamTaskMapper.updateTask(taskModel)
  }

  /**
   * Query execute history(查询运行历史)
   * @param jobId
   * @param version
   * @return
   */
  def queryHistory(jobId: Long, version: String): util.List[StreamTaskListVo] ={
    if(StringUtils.isEmpty(version)) throw new JobFetchErrorException(30355, "version cannot be empty.")
    val job = streamJobMapper.getJobById(jobId)
    if(job == null) throw new JobFetchErrorException(30355, s"Unknown job $jobId.")
    val jobVersion = streamJobMapper.getJobVersionById(jobId, version)
    if(jobVersion == null) return new util.ArrayList[StreamTaskListVo]
    val tasks = streamTaskMapper.getByJobVersionId(jobVersion.getId, version)
    if(tasks == null || tasks.isEmpty) return new util.ArrayList[StreamTaskListVo]
    val list = new util.ArrayList[StreamTaskListVo]
    tasks.asScala.foreach{ f =>
      val svo = new StreamTaskListVo()
      svo.setTaskId(f.getId)
      svo.setStatus(JobConf.getStatusString(f.getStatus))
      svo.setCreator(f.getSubmitUser)
      svo.setVersion(version)
      svo.setJobName(job.getName)
      svo.setStartTime(DateUtils.formatDate(f.getStartTime))
      svo.setEndTime(DateUtils.formatDate(f.getLastUpdateTime))
      svo.setJobVersionId(f.getJobVersionId)
      //获取最新版本的代码信息
      svo.setVersionContent(jobVersion.getJobContent)
      svo.setRunTime(DateUtils.intervals(f.getStartTime, f.getLastUpdateTime))
      svo.setStopCause(sub(f.getErrDesc))
      list.add(svo)
    }
    list
  }

  def getRealtimeLog(jobId: Long, operator: String, requestPayload: LogRequestPayload): util.Map[String, Any] = {
    val returnMap = new util.HashMap[String, Any]
    returnMap.put("logPath", "undefined")
    returnMap.put("logs", util.Arrays.asList("No log content is available, perhaps the task has not been scheduled"))
    val streamTask = streamTaskMapper.getTaskById(jobId)
    if (null != streamTask && StringUtils.isNotBlank(streamTask.getLinkisJobId)) {
      jobLaunchManager match {
        case jobManager: FlinkJobLaunchManager =>
          DefaultStreamTaskService.loadStreamTask(streamTask)
          val logIterator = jobManager.fetchLogs(streamTask.getLinkisJobId, requestPayload)
          returnMap.put("logPath", logIterator.getLogPath)
          returnMap.put("logs", logIterator.getLogs)
          logIterator.close()
      }
    }
    returnMap
  }


  /**
   * job status(每次任务状态)
   * @param jobId
   * @return
   */
  def getByJobStatus(jobId:Long, version: String): JobProgressVo ={
    val jobVersion = streamJobMapper.getJobVersionById(jobId, version)
    if(jobVersion == null) return new JobProgressVo
    val tasks = streamTaskMapper.getTasksByJobIdAndJobVersionId(jobVersion.getJobId, jobVersion.getId)
    if(tasks == null || tasks.isEmpty) return new JobProgressVo
    val task = tasks.get(0)
    val jobProgressVO = new JobProgressVo()
    jobProgressVO.setTaskId(task.getId)
    jobProgressVO.setProgress(task.getStatus)
    jobProgressVO
  }

  def getTask(jobId:Long, version: String): FlinkJobInfo ={
    val str = streamTaskMapper.getTask(jobId, version)
    if (StringUtils.isBlank(str)) {
      return new FlinkJobInfo
    }
    DWSHttpClient.jacksonJson.readValue(str,classOf[FlinkJobInfo])
  }

  /**
   * Create new task use the latest job version
   *
   * @param jobId   job id
   * @param status  init status
   * @param creator creator
   */
  @Transactional(rollbackFor = Array(classOf[Exception]))
  override def createTask(jobId: Long, status: Int, creator: String): StreamTask = {
     info(s"Start to query and lock the stream job in [$jobId] before creating StreamTask")
     Option(streamJobMapper.queryAndLockJobById(jobId)) match {
       case None => throw new JobTaskErrorException(-1, s"Unable to create StreamTask, the stream job [$jobId] is not exists.")
       case Some(job) =>
          // Then to fetch latest job version
          Option(streamJobMapper.getLatestJobVersion(jobId)) match {
            case None => throw new JobTaskErrorException(-1, s"No versions can be found for job [id: ${job.getId}, name: ${job.getName}]")
            case Some(jobVersion) =>
              info(s"Fetch the latest version: ${jobVersion.getVersion} for job [id: ${job.getId}, name: ${job.getName}]")
              // Get the latest task by job version id
              val latestTask = streamTaskMapper.getLatestByJobVersionId(jobVersion.getId, jobVersion.getVersion)
              if (null == latestTask || JobConf.isCompleted(latestTask.getStatus)){
                 val streamTask = new StreamTask(jobId, jobVersion.getId, jobVersion.getVersion, creator)
                 streamTask.setStatus(status)
                 streamTaskMapper.insertTask(streamTask)
                 streamTask
              } else {
                  throw new JobTaskErrorException(-1, s"Unable to create new task, StreamTask [${latestTask.getId}] is still " +
                    s"not completed for job [id: ${job.getId}, name: ${job.getName}]")
              }
          }
     }
  }

  /**
   * Just launch task by task id
   *
   * @param taskId task id
   */
  override def launch(taskId: Long, execUser: String): Unit = {
      launch(taskId, execUser, null)
  }

  /**
   * Launch with job state
   * @param taskId task id
   * @param execUser executor
   * @param state state
   */
  def launch(taskId: Long, execUser: String, state: JobState):Long = {
    // First to query the task information
    val streamTask = this.streamTaskMapper.getTaskById(taskId)
    if (null == streamTask){
      throw new JobExecuteErrorException(-1, s"Not found the StreamTask [$taskId] to execute, please examined the system runtime status!")
    }
    // Second to query the related job information
    val streamJob = streamJobMapper.getJobById(streamTask.getJobId)
    if (null == streamJob){
      throw new JobExecuteErrorException(-1, s"Not found the related job info in [${streamTask.getJobId}], has been dropped it ?")
    }
    info(s"Start to find the transform builder to process the StreamJob [${streamJob.getName}]")
    val transformJob = streamisTransformJobBuilders.find(_.canBuild(streamJob)).map(_.build(streamJob))
      .getOrElse(throw new TransformFailedErrorException(30408, s"Cannot find a TransformJobBuilder to build StreamJob ${streamJob.getName}."))
    // To avoid the permission problem, use the creator to submit job
    // Use {projectName}.{jobName} as the launch job name
    var launchJob = LaunchJob.builder().setJobName(s"${streamJob.getProjectName}.${streamJob.getName}").setSubmitUser(streamJob.getCreateBy).build()
    launchJob = Transform.getTransforms.foldLeft(launchJob)((job, transform) => transform.transform(transformJob, job))
    info(s"StreamJob [${streamJob.getName}] has transformed with launchJob $launchJob.")
    //TODO getLinkisJobManager should use jobManagerType to instance in future, since not only `simpleFlink` mode is supported in future.
    val linkisJobId = jobLaunchManager.launch(launchJob, state)
    streamTask.setLinkisJobId(linkisJobId)
    info(s"StreamJob [${streamJob.getName}] has launched with id $linkisJobId.")
    DefaultStreamTaskService.updateStreamTaskStatus(streamTask, streamJob.getName)
    streamTaskMapper.updateTask(streamTask)
    streamTask.getId
  }


  /**
   * Sub function
   * @param str str
   * @return
   */
  private def sub(str:String):String = {
    if (StringUtils.isBlank(str) || str.length <= 100){
      str
    }else {
      if (str.contains("message")){
        val subStr = str.substring(str.indexOf("message") - 1)
        if (subStr.length <= 100){
          subStr + "..."
        }else {
          subStr.substring(0,100) + "..."
        }
      }else {
        str.substring(0,100) + "..."
      }
    }
  }
}

object DefaultStreamTaskService {

  def loadStreamTask(task: StreamTask): JobInfo = {
    val jobLaunchManager = JobLaunchManager.getJobManager(JobLauncherAutoConfiguration.DEFAULT_JOB_LAUNCH_MANGER)
    Option(jobLaunchManager.getJobInfo(task.getLinkisJobId)).getOrElse {
      // connect the task
      jobLaunchManager.connect(task.getLinkisJobId, task.getLinkisJobInfo)
    }
  }


  def updateStreamTaskStatus(task: StreamTask, jobName: String)(implicit logger: Logger): Unit = {
    val jobInfo = loadStreamTask(task)
    logger.info(s"StreamJob [$jobName] is ${jobInfo.getStatus} with $jobInfo.")
    task.setLastUpdateTime(new Date)
    task.setStatus(JobConf.linkisStatusToStreamisStatus(jobInfo.getStatus))
    if(JobConf.isCompleted(task.getStatus) && StringUtils.isNotEmpty(jobInfo.getCompletedMsg))
      task.setErrDesc(jobInfo.getCompletedMsg)
    task.setLinkisJobInfo(DWSHttpClient.jacksonJson.writeValueAsString(jobInfo))
  }
}