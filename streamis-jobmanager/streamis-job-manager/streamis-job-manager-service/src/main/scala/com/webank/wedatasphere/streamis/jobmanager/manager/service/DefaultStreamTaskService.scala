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

import java.util
import java.util.concurrent.{Executors, Future, ScheduledExecutorService, TimeUnit}
import java.util.{Calendar, Map, function}
import com.webank.wedatasphere.streamis.jobmanager.launcher.conf.JobConfKeyConstants
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf.JobConf
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.exception.{JobErrorException, JobExecuteErrorException, JobFetchErrorException, JobPauseErrorException, JobTaskErrorException}
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobLaunchManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.{JobGenericState, JobState}
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.{JobInfo, LaunchJob}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LogRequestPayload
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client.{AbstractJobClient, EngineConnJobClient}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.manager.SimpleFlinkJobLaunchManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state.{FlinkCheckpoint, FlinkSavepoint}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.jobInfo.EngineConnJobInfo
import com.webank.wedatasphere.streamis.jobmanager.manager.SpringContextHolder
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.{StreamJobMapper, StreamTaskMapper}
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo._
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.{StreamJob, StreamJobMode, StreamTask}
import com.webank.wedatasphere.streamis.jobmanager.manager.scheduler.FutureScheduler
import com.webank.wedatasphere.streamis.jobmanager.manager.scheduler.events.AbstractStreamisSchedulerEvent.StreamisEventInfo
import com.webank.wedatasphere.streamis.jobmanager.manager.scheduler.events.StreamisPhaseInSchedulerEvent
import com.webank.wedatasphere.streamis.jobmanager.manager.scheduler.events.StreamisPhaseInSchedulerEvent.ScheduleCommand
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity.RealtimeLogEntity
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.exception.TransformFailedErrorException
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.{StreamisTransformJobBuilder, TaskMetricsParser, Transform}
import com.webank.wedatasphere.streamis.jobmanager.manager.util.DateUtils
import com.webank.wedatasphere.streamis.jobmanager.manager.utils.StreamTaskUtils
import com.webank.wedatasphere.streamis.errorcode.handler.StreamisErrorCodeHandler
import com.webank.wedatasphere.streamis.jobmanager.launcher.dao.StreamJobConfMapper

import javax.annotation.Resource
import org.apache.commons.lang.StringUtils
import org.apache.linkis.common.utils.{Logging, Utils}
import org.apache.linkis.httpclient.dws.DWSHttpClient
import org.apache.linkis.scheduler.queue
import org.apache.linkis.scheduler.queue.{Job, SchedulerEvent}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConverters._
import scala.util.control.Breaks.{break, breakable}


@Service
class DefaultStreamTaskService extends StreamTaskService with Logging{

  @Autowired private var streamTaskMapper:StreamTaskMapper=_
  @Autowired private var streamJobMapper:StreamJobMapper=_
  @Autowired private var streamisTransformJobBuilders: Array[StreamisTransformJobBuilder] = _
  @Autowired private var taskMetricsParser: Array[TaskMetricsParser] = _

  @Resource
  private var jobLaunchManager: JobLaunchManager[_ <: JobInfo] = _

  @Resource
  private var streamJobConfMapper: StreamJobConfMapper = _

  @Resource
  private var streamTaskService: StreamTaskService = _
  /**
   * Scheduler
   */
  @Resource
  private var scheduler: FutureScheduler = _

  @Autowired
  private var instanceService: InstanceService = _

  private val errorCodeHandler = StreamisErrorCodeHandler.getInstance()

  /**
   *
   * @param Id
   * @return
   */
  override def getTaskById(Id: Long): StreamTask = {
    this.streamTaskMapper.getTaskById(Id)
  }

  override def getTaskStatusById(jobId: Long): JobStatusVo = {
    val streamTask = this.streamTaskMapper.getLatestByJobId(jobId)
    val statusVo = new JobStatusVo()
    statusVo.setStatusCode(streamTask.getStatus)
    statusVo.setStatus(JobConf.getStatusString(streamTask.getStatus))
    statusVo.setJobId(streamTask.getJobId)
    statusVo.setMessage(streamTask.getErrDesc)
    statusVo
  }
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
      throw new JobExecuteErrorException(-1, s"Fail to execute StreamJob(Task), message output: $errorMessage");
    }
  }

  override def execute(jobId: Long, taskId: Long, execUser: String): Unit = {
    val actualJobId = if(jobId <= 0) getTaskInfo(taskId)._1 else jobId
    val restore = this.streamJobConfMapper.getRawConfValue(actualJobId, JobConfKeyConstants.START_AUTO_RESTORE_SWITCH.getValue) match {
      case "ON" => true
      case _ =>  false
    }
    execute(actualJobId, 0, execUser, restore)
  }

  override def asyncExecute(jobId: Long, taskId: Long, execUser: String, restore: Boolean): Future[String] = {
    execute(jobId, taskId, execUser, restore, new function.Function[SchedulerEvent, String] {
      override def apply(event: SchedulerEvent): String = {
        event match {
          case job: Job =>
            job.getJobInfo.getOutput
          case _ => null
        }
      }
    })._2
  }

  override def asyncExecute(jobId: Long, taskId: Long, execUser: String): Future[String] = {
    val actualJobId = if(jobId <= 0) getTaskInfo(taskId)._1 else jobId
    val restore = this.streamJobConfMapper.getRawConfValue(actualJobId, JobConfKeyConstants.START_AUTO_RESTORE_SWITCH.getValue) match {
      case "ON" => true
      case _ =>  false
    }
    asyncExecute(actualJobId, 0, execUser, restore)
  }

  override def bulkExecute(jobIds: util.List[Long], taskIds: util.List[Long], execUser: String): util.List[ExecResultVo] = {
    bulkExecute(jobIds, taskIds, execUser, (jobId, taskId) => {
      val actualJobId = if(jobId <= 0) getTaskInfo(taskId)._1 else jobId
      this.streamJobConfMapper.getRawConfValue(actualJobId, JobConfKeyConstants.START_AUTO_RESTORE_SWITCH.getValue) match {
        case "ON" => true
        case _ =>  false
      }
    })
  }
  /**
   * Bulk executing
   *
   * @param jobIds   jobIds
   * @param taskIds  taskIds
   * @param execUser execUser
   * @param restore  restore from job state
   */
  override def bulkExecute(jobIds: util.List[Long], taskIds: util.List[Long], execUser: String, restore: Boolean): util.List[ExecResultVo] = {
    bulkExecute(jobIds, taskIds, execUser, (_, _) => restore)
  }

  def bulkExecute(jobIds: util.List[Long], taskIds: util.List[Long], execUser: String, isRestore: (Long, Long) => Boolean): util.List[ExecResultVo] = {
    val result: util.List[ExecResultVo] = new util.ArrayList[ExecResultVo]()
    val counter = (jobIds.size(), taskIds.size())
    val iterateNum: Int = math.max(counter._1, counter._2)
    for (i <- 0 until iterateNum){
      val jobId = if (i < counter._1) jobIds.get(i) else 0L
      val taskId = if (i < counter._2) taskIds.get(i) else 0L
      val event = execute(jobId, taskId, execUser, isRestore(jobId, taskId),
        new function.Function[SchedulerEvent, String]{
          override def apply(event: SchedulerEvent): String = {
            event match {
              case job: Job =>
                job.getJobInfo.getOutput
              case _ => null
            }
          }
        })._1
      // Convert scheduler event to execution result
      val resultVo: ExecResultVo = new ExecResultVo(jobId, taskId)
      event match {
        case job: queue.Job =>
          queueJobInfoIntoResult(job.getJobInfo, resultVo)
      }
      result.add(resultVo)
    }
    result
  }
  def execute[T](jobId: Long, taskId: Long, execUser: String, restore: Boolean, returnMapping: function.Function[SchedulerEvent, T]): (SchedulerEvent, Future[T]) = {
    val self = SpringContextHolder.getBean(classOf[StreamTaskService])
    var finalJobId = jobId
    val event = new StreamisPhaseInSchedulerEvent(if (jobId > 0) "executeJob-" + jobId else "executeTask-" + taskId, new ScheduleCommand {

      override def onPrepare(context: StreamisPhaseInSchedulerEvent.StateContext, scheduleJob: queue.JobInfo): Unit =  {
        if (finalJobId <= 0 ){
          finalJobId = getTaskInfo(taskId)._1
        }
        // Assign the status STARTING default
        val streamTask = self.createTask(finalJobId, JobConf.FLINK_JOB_STATUS_STARTING.getValue, execUser)
        context.addVar("newTaskId", streamTask.getId);
      }

      override def schedule(context: StreamisPhaseInSchedulerEvent.StateContext, jobInfo: queue.JobInfo): util.Map[String, AnyRef] = {
        val newTaskId = context.getVar("newTaskId")
        if (null != newTaskId){
          val restoreTaskId = taskId
          val jobState: JobState = if (restoreTaskId <= 0){
            getStateInfo(streamTaskMapper.getLatestLaunchedById(jobId))
          } else getStateInfo(restoreTaskId)
          if (null != jobState){
            // If jobState.setToRestore(true) means that using the job state to restore stream task
            jobState.setToRestore(restore)
          }
          // Launch entrance
          launch(newTaskId.asInstanceOf[Long], execUser, jobState)
        } else {
          // cannot find the new task id
        }
        null
      }

      override def onErrorHandle(context: StreamisPhaseInSchedulerEvent.StateContext, scheduleJob: queue.JobInfo, t: Throwable): Unit = {
        // Change the task status
        val newTaskId = context.getVar("newTaskId")
        if (null != newTaskId) {
          info(s"Error to launch StreamTask [$newTaskId], now try to persist the status and message output", t)
          val finalTask = new StreamTask()
          finalTask.setId(newTaskId.asInstanceOf[Long])
          finalTask.setStatus(JobConf.FLINK_JOB_STATUS_FAILED.getValue)
          // Output message equals error message, you can use t.getMessage()
          finalTask.setErrDesc(scheduleJob.getOutput)
          finalTask.setServerInstance(instanceService.getThisServiceInstance)
          if (streamTaskMapper.updateTaskInStatus(finalTask, JobConf.FLINK_JOB_STATUS_STARTING.getValue) > 0) {
            info(s"Transient the StreamTask [$newTaskId]'status from STARTING to FAILED and flush the output message.")
          }
        }
      }
    })
    (event, scheduler.submit(event, returnMapping))
  }


  /**
   * Sync to pause job(task)
   *
   * @param jobId    job id
   * @param taskId   task id
   * @param operator user name
   */
  override def pause(jobId: Long, taskId: Long, operator: String, snapshot: Boolean): PauseResultVo = {
    val result: Future[PauseResultVo] = asyncPause(jobId, taskId, operator, snapshot)
    val pauseResult = result.get()
    if (StringUtils.isNotBlank(pauseResult.getMessage)){
      throw new JobExecuteErrorException(-1, s"Fail to pause StreamJob(Task), message output: ${pauseResult.getMessage}");
    }
    pauseResult
  }


  override def asyncPause(jobId: Long, taskId: Long, operator: String, snapshot: Boolean): Future[PauseResultVo] = {
    pause(jobId, taskId, operator, snapshot, new function.Function[SchedulerEvent, PauseResultVo] {
      override def apply(event: SchedulerEvent): PauseResultVo = {
        val resultVo: PauseResultVo = new PauseResultVo(jobId, taskId)
        event match {
          case job: queue.Job =>
            val jobInfo = job.getJobInfo
            queueJobInfoIntoResult(jobInfo, resultVo)
            jobInfo match {
              case eventInfo: StreamisEventInfo =>
                resultVo.setSnapshotPath(String.valueOf(eventInfo
                  .getResultSet.asScala.getOrElse("snapshotPath", "")))
            }
          case _ =>
        }
        resultVo
      }
    })._2
  }

  /**
   * Bulk pausing
   *
   * @param jobIds   jobIds
   * @param taskIds  taskIds
   * @param operator operator
   * @param snapshot snapshot
   * @return
   */
  override def bulkPause(jobIds: util.List[Long], taskIds: util.List[Long], operator: String, snapshot: Boolean): util.List[PauseResultVo] = {
    val result: util.List[Future[PauseResultVo]] = new util.ArrayList[Future[PauseResultVo]]()
    val counter = (jobIds.size(), taskIds.size())
    val iterateNum: Int = math.max(counter._1, counter._2)
    for (i <- 0 until iterateNum) {
      val jobId = if (i < counter._1) jobIds.get(i) else 0L
      val taskId = if (i < counter._2) taskIds.get(i) else 0L
      result.add(asyncPause(jobId, taskId, operator, snapshot))
    }
    result.asScala.map(_.get()).asJava
  }

  def pause[T](jobId: Long, taskId: Long, operator: String, snapshot: Boolean, returnMapping: function.Function[SchedulerEvent, T]): (SchedulerEvent, Future[T]) = {
    val self = SpringContextHolder.getBean(classOf[StreamTaskService])
    var finalJobId = jobId
    val event = new StreamisPhaseInSchedulerEvent(if (jobId > 0) "pauseJob-" + jobId else "pauseTask-" + taskId, new ScheduleCommand {

      override def onPrepare(context: StreamisPhaseInSchedulerEvent.StateContext, scheduleJob: queue.JobInfo): Unit = {
        if (finalJobId < 0){
          finalJobId = getTaskInfo(taskId)._1
        }
        // Assign the status STOPPING default
        val pauseTaskId = self.transitionTaskStatus(jobId, taskId, JobConf.FLINK_JOB_STATUS_STOPPING.getValue)
        if (pauseTaskId > 0) context.addVar("pauseTaskId", pauseTaskId)
      }

      override def onErrorHandle(context: StreamisPhaseInSchedulerEvent.StateContext, scheduleJob: queue.JobInfo, t: Throwable): Unit = {
        val pauseTaskId = context.getVar("pauseTaskId")
        if (null != pauseTaskId) {
          info(s"Error to pause StreamTask [$pauseTaskId], now try to restore the status", t)
          val finalTask = new StreamTask()
          finalTask.setId(pauseTaskId.asInstanceOf[Long])
          finalTask.setStatus(JobConf.FLINK_JOB_STATUS_RUNNING.getValue)
          finalTask.setServerInstance(instanceService.getThisServiceInstance)
          // Not need to store the output message
          if (streamTaskMapper.updateTaskInStatus(finalTask, JobConf.FLINK_JOB_STATUS_STOPPING.getValue) > 0) {
            info(s"Restore the StreamTask [$pauseTaskId]'status from STOPPING return to RUNNING.")
          }
        }
      }

      override def schedule(context: StreamisPhaseInSchedulerEvent.StateContext, jobInfo: queue.JobInfo): util.Map[String, AnyRef] = {
        val pauseTaskId = context.getVar("pauseTaskId")
        val resultSet = new util.HashMap[String, AnyRef]()
        if (null != pauseTaskId){
          val streamTask = streamTaskMapper.getTaskById(pauseTaskId.asInstanceOf[Long])
          if (null == streamTask){
            throw new JobPauseErrorException(-1, s"Not found the StreamTask [$pauseTaskId] to pause, please examined the system runtime status!")
          }
          if (StringUtils.isBlank(streamTask.getLinkisJobId)){
            throw new JobPauseErrorException(-1, s"Unable to pause the StreamTask [$pauseTaskId}], the linkis job id is null")
          }
          val streamJob = streamJobMapper.getJobById(finalJobId)
          logger.info(s"Try to stop StreamJob [${streamJob.getName} with task(taskId: ${streamTask.getId}, linkisJobId: ${streamTask.getLinkisJobId}).")
          val jobClient = getJobLaunchManager(streamTask).connect(streamTask.getLinkisJobId, streamTask.getLinkisJobInfo)
          val status = JobConf.linkisStatusToStreamisStatus(jobClient.getJobInfo(true).getStatus)
          if (!JobConf.isCompleted(status)) {
            val jobStateInfo = Utils.tryCatch(jobClient.stop(snapshot)){
              case e: Exception =>
                val pauseError =  new JobPauseErrorException(-1, s"Fail to stop the StreamJob [${streamJob.getName}] " +
                  s"with task(taskId: ${streamTask.getId}, linkisJobId: ${streamTask.getLinkisJobId}), reason: ${e.getMessage}.")
                pauseError.initCause(e)
                throw pauseError
              case pauseE: JobPauseErrorException =>
                throw pauseE
            }
            Option(jobStateInfo).foreach(stateInfo => resultSet.put("snapshotPath", stateInfo.getLocation))
          }
          streamTask.setLastUpdateTime(Calendar.getInstance.getTime)
          streamTask.setStatus(JobConf.FLINK_JOB_STATUS_STOPPED.getValue)
          streamTaskMapper.updateTask(streamTask)
        }
        resultSet
      }
    })
    (event, this.scheduler.submit(event, returnMapping))
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

  def getRealtimeLog(jobId: Long, taskId: Long, operator: String, requestPayload: LogRequestPayload): RealtimeLogEntity = {
    val realtimeLogEntity = new RealtimeLogEntity
    realtimeLogEntity.setLogPath("undefined")
    realtimeLogEntity.setLogs(util.Arrays.asList("No log content is available. Perhaps the task has not been scheduled"))
    realtimeLogEntity.setEndLine(1);
    val streamTask = if(taskId > 0) streamTaskMapper.getTaskById(taskId)
    else streamTaskMapper.getLatestByJobId(jobId)
    if (null != streamTask && StringUtils.isNotBlank(streamTask.getLinkisJobId)) {
      Utils.tryCatch {
        val jobClient = getJobLaunchManager(streamTask).connect(streamTask.getLinkisJobId, streamTask.getLinkisJobInfo)
        jobClient match {
          //todo other clients
          case client: EngineConnJobClient =>
            requestPayload.setLogHistory(JobConf.isCompleted(streamTask.getStatus))
            val logIterator = client.fetchLogs(requestPayload)
            realtimeLogEntity.setLogPath(logIterator.getLogPath)
            realtimeLogEntity.setLogs(logIterator.getLogs)
            realtimeLogEntity.setEndLine(logIterator.getEndLine);
            logIterator.close()
            jobClient.getJobInfo match {
              case linkisInfo: EngineConnJobInfo =>
                if (StringUtils.isBlank(linkisInfo.getLogDirSuffix) && StringUtils.isNotBlank(logIterator.getLogDirSuffix)){
                  Utils.tryAndWarn {
                    // Update the linkis job info and store into database
                    linkisInfo.setLogDirSuffix(logIterator.getLogDirSuffix)
                    streamTask.setLinkisJobInfo(DWSHttpClient.jacksonJson.writeValueAsString(linkisInfo));
                    streamTaskMapper.updateTask(streamTask)
                  }
                }
              case _ =>
            }
          case o =>
            logger.error(s"Invalid client: ${o}")
        }
      }{ case e: Exception =>
        // Just warn the exception
        warn(s"Unable to fetch runtime log for StreamTask " +
          s"[id: ${streamTask.getId}, jobId: ${streamTask.getJobId}, linkis_id: ${streamTask.getLinkisJobId}]", e)
      }
    }
    realtimeLogEntity
  }

  /**
   * Do snapshot
   *
   * @param jobId    job id
   * @param taskId   task id
   * @param operator operator
   */
  override def snapshot(jobId: Long, taskId: Long, operator: String): String = {
    val streamTask = if (taskId > 0) streamTaskMapper.getTaskById(taskId)
    else streamTaskMapper.getLatestByJobId(jobId)
    if (null != streamTask && StringUtils.isNotBlank(streamTask.getLinkisJobId)){
      val jobClient = getJobLaunchManager(streamTask).connect(streamTask.getLinkisJobId, streamTask.getLinkisJobInfo)
      return jobClient match {
        case flinkJobClient: AbstractJobClient =>
          Option(flinkJobClient.triggerSavepoint()) match {
            case Some(savepoint) =>
              savepoint.getLocation.toString
          }
      }
    }
    null
  }
  /**
   * @param jobId
   * @return
   */
  def getProgress(jobId:Long, version: String): JobProgressVo ={
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

  /**
   * Fetch the status list by job id list
   *
   * @param jobIds job ids
   */
  override def getStatusList(jobIds: util.List[Long]): util.List[JobStatusVo] = {
    val streamTask: util.List[StreamTask] = this.streamTaskMapper.getStatusInfoByJobIds(jobIds.asScala.map(id => {
      id.asInstanceOf[java.lang.Long]
    }).asJava)
    streamTask.asScala.map(task => {
      val statusVo = new JobStatusVo()
      statusVo.setStatusCode(task.getStatus)
      statusVo.setStatus(JobConf.getStatusString(task.getStatus))
      statusVo.setJobId(task.getJobId)
      statusVo.setMessage(task.getErrDesc)
      statusVo
    }).asJava
  }

  def getTaskJobInfo(jobId:Long, version: String): EngineConnJobInfo ={
    val str = streamTaskMapper.getTask(jobId, version)
    if (StringUtils.isBlank(str)) {
      return new EngineConnJobInfo
    }
    DWSHttpClient.jacksonJson.readValue(str,classOf[EngineConnJobInfo])
  }


  /**
   * Update the task status
   *
   * @param jobId  job id
   * @param status status code
   * @return task id of latest task
   */
  @Transactional(rollbackFor = Array(classOf[Exception]))
  override def transitionTaskStatus(jobId: Long, taskId: Long, status: Int): Long = {
    trace(s"Query and lock the StreamJob in [$jobId] before updating status of StreamTask")
    Option(streamJobMapper.queryJobById(jobId)) match {
      case None => throw new JobTaskErrorException(-1, s"Unable to update status of StreamTask, the StreamJob [$jobId] is not exists.")
      case Some(job) =>
        val streamTask = if(taskId > 0) streamTaskMapper.getTaskById(taskId)
        else streamTaskMapper.getLatestByJobId(jobId)
        if (null == streamTask){
          throw new JobTaskErrorException(-1, s"Unable to find any StreamTask for job [id: ${job.getId}, name: ${job.getName}]")
        }
        if (JobConf.isCompleted(streamTask.getStatus)){
          warn(s"StreamTask [${streamTask.getId}] has been completed for for " +
            s"job [id: ${job.getId}, name: ${job.getName}]")
          // Just return 0
          0
        }else {
          streamTask.setStatus(status)
          streamTask.setLastUpdateTime(Calendar.getInstance.getTime)
          streamTaskMapper.updateTask(streamTask)
          streamTask.getId
        }
    }
  }


  override def getLatestTaskByJobId(jobId: Long): StreamTask = streamTaskMapper.getLatestByJobId(jobId)

  /**
   * Create new task use the latest job version
   *
   * @param jobId   job id
   * @param status  init status
   * @param creator creator
   */
  @Transactional(rollbackFor = Array(classOf[Exception]))
  override def createTask(jobId: Long, status: Int, creator: String): StreamTask = {
    logger.trace(s"Query and lock the StreamJob in [$jobId] before creating StreamTask")
    Option(streamJobMapper.queryJobById(jobId)) match {
      case None => throw new JobTaskErrorException(-1, s"Unable to create StreamTask, the StreamJob [$jobId] is not exists.")
      case Some(job) =>
        // Then to fetch latest job version
        Option(streamJobMapper.getLatestJobVersion(jobId)) match {
          case None => throw new JobTaskErrorException(-1, s"No versions can be found for job [id: ${job.getId}, name: ${job.getName}]")
          case Some(jobVersion) =>
            var noticeMessage = s"Fetch the latest version: ${jobVersion.getVersion} for job [id: ${job.getId}, name: ${job.getName}]"
            if (!jobVersion.getVersion.equals(job.getCurrentVersion)){
              noticeMessage += s", last version used for task is ${job.getCurrentVersion}"
              // Update job current version
              job.setCurrentVersion(jobVersion.getVersion)
              streamJobMapper.updateJob(job)
            }
            logger.info(noticeMessage)
            // Get the latest task by job id
            val latestTask = streamTaskMapper.getLatestByJobId(jobId)
            if (null == latestTask || JobConf.isCompleted(latestTask.getStatus)){
              val streamTask = new StreamTask(jobId, jobVersion.getId, jobVersion.getVersion, creator)
              streamTask.setStatus(status)
              streamTask.setServerInstance(instanceService.getThisServiceInstance)
              logger.info(s"Produce a new StreamTask [jobId: $jobId, version: ${jobVersion.getVersion}, creator: $creator, status: ${streamTask.getStatus}]")
              streamTaskMapper.insertTask(streamTask)
              streamTask
            } else {
              throw new JobTaskErrorException(-1, s"Unable to create new task, StreamTask [${latestTask.getId}] is still " +
                s"not completed for job [id: ${job.getId}, name: ${job.getName}]")
            }
        }
    }
  }

  override def updateTask(streamTask: StreamTask): Unit = streamTaskMapper.updateTask(streamTask)

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
    //todo 在前置的streamJob创建时设置
    info(s"Start to find the transform builder to process the StreamJob [${streamJob.getName}]")
    val transformJob = streamisTransformJobBuilders.find(_.canBuild(streamJob)).map(_.build(streamJob))
      .getOrElse(throw new TransformFailedErrorException(30408, s"Cannot find a TransformJobBuilder to build StreamJob ${streamJob.getName}."))
    // To avoid the permission problem, use the creator to submit job
    // Use {projectName}.{jobName} as the launch job name
    var launchJob = LaunchJob.builder().setJobName(s"${streamJob.getProjectName}.${streamJob.getName}.${taskId}").setSubmitUser(streamJob.getCreateBy).build()
    launchJob = Transform.getTransforms.foldLeft(launchJob)((job, transform) => transform.transform(transformJob, job))
    info(s"StreamJob [${streamJob.getName}] has transformed with launchJob $launchJob, now to launch it.")
    //TODO getLinkisJobManager should use jobManagerType to instance in future, since not only `simpleFlink` mode is supported in future.
    val jobClient = getJobLaunchManager(streamTask).launch(launchJob, state)
    // Refresh and store the information from JobClient
    Utils.tryCatch {
      // Refresh the job info(If the job shutdown immediately)
      val jobInfo = jobClient.getJobInfo(true)
      info(s"StreamJob [${streamJob.getName}] has launched with linkis_id ${jobInfo.getId}. now to examine its status")
      streamTask.setLinkisJobId(jobInfo.getId)
      StreamTaskUtils.refreshInfo(streamTask, jobInfo)
      // First to store the launched task info
      streamTaskMapper.updateTask(streamTask)
      info(s"StreamJob [${streamJob.getName}] is ${jobInfo.getStatus} with $jobInfo.")
      if (JobConf.FLINK_JOB_STATUS_FAILED.getValue == streamTask.getStatus){
        val result: Future[_] = streamTaskService.errorCodeMatching(streamJob.getId,streamTask)
        throw new JobExecuteErrorException(-1, s"(提交流式应用状态失败, 请检查日志), errorDesc: ${streamTask.getErrDesc}")
      }
      // Drop the temporary configuration
      Utils.tryQuietly(streamJobConfMapper.deleteTemporaryConfValue(streamTask.getJobId), {
        case e: Exception =>
          warn(s"Fail to delete the temporary configuration for job [${streamTask.getJobId}], task [${streamTask.getId}]", e)
      })
    }{case e: Exception =>
      val message = s"Error occurred when to refresh and store the info of StreamJob [${streamJob.getName}] with JobClient. ${e.getMessage}"
      warn(s"$message, stop and destroy the Client connection.")
      // Stop the JobClient directly
      Utils.tryAndWarn(jobClient.stop())
      val errExcept =  new JobExecuteErrorException(-1, s"$message, message: ${e.getMessage}")
      errExcept.initCause(e)
      throw errExcept
    }
    streamTask.getId
  }


  /**
   * @param taskId taskId
   * @return
   */
  private def getTaskInfo(taskId: Long): (Long, StreamTask) = {
    val oldStreamTask = streamTaskMapper.getTaskById(taskId)
    if (Option(oldStreamTask).isEmpty){
      throw new JobTaskErrorException(-1, s"Cannot find the StreamTask in id: $taskId")
    }
    (oldStreamTask.getJobId, oldStreamTask)
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

  /**
   * Convert the queue job info into schedule result
   * @param jobInfo job info
   * @param scheduleResult schedule result
   */
  private def queueJobInfoIntoResult(jobInfo: queue.JobInfo, scheduleResult: ScheduleResultVo): Unit = {
    scheduleResult.setScheduleId(jobInfo.getId)
    scheduleResult.setScheduleState(jobInfo.getState)
    scheduleResult.setProgress(jobInfo.getProgress)
    // TODO Set metric info
    scheduleResult.setMessage(jobInfo.getOutput)
  }

  override def getStateInfo(taskId: Long): JobState = {
    getStateInfo(this.streamTaskMapper.getTaskById(taskId))
  }

  override def getStateInfo(streamTask: StreamTask): JobState = {
    Option(streamTask) match {
      case Some(task) =>
        if (StringUtils.isNotBlank(task.getLinkisJobId)) {
          val stateList: util.List[JobState] = new util.ArrayList[JobState]()
          // Connect to get the JobInfo
          getJobLaunchManager(task) match {
            case jobLaunchManager: SimpleFlinkJobLaunchManager =>
              // Only support to fetch state information for Flink stream task
              logger.info(s"Try to fetch and choose the state information from [${task.getId}].")
              val jobClient = jobLaunchManager.connect(task.getLinkisJobId, task.getLinkisJobInfo)
              val jobInfo = jobClient.getJobInfo
              // Get the JobStateManager
              val jobStateManager = jobLaunchManager.getJobStateManager
              val highAvailablePolicy = this.streamJobConfMapper.getRawConfValue(task.getJobId, JobConf.HIGHAVAILABLE_POLICY_KEY.getValue)
              // First to fetch the latest Savepoint information
              Option(jobStateManager.getJobState[FlinkSavepoint](classOf[FlinkSavepoint], jobInfo,highAvailablePolicy)).foreach(savepoint => stateList.add(savepoint))
              // Determinate if need the checkpoint information
              this.streamJobConfMapper.getRawConfValue(task.getJobId, JobConfKeyConstants.CHECKPOINT_SWITCH.getValue) match {
                case "ON" =>
                  // Then to fetch the latest Checkpoint information
                  Option(jobStateManager.getJobState[FlinkCheckpoint](classOf[FlinkCheckpoint], jobInfo,highAvailablePolicy)).foreach(checkpoint => stateList.add(checkpoint))
                case _ =>
              }
              // At last fetch the state information from storage
              Option(jobInfo.getJobStates).foreach(stateInfoList => {
                stateInfoList.foreach(stateInfo => {
                  val jobState = new JobGenericState(stateInfo.getLocation)
                  jobState.setToRestore(stateInfo.isRestore)
                  jobState.setTimestamp(stateInfo.getTimestamp)
                  stateList.add(jobState)
                })
              })
            case _ =>
          }
          if (!stateList.isEmpty){
            // Choose the newest job state
            val finalState = stateList.asScala.maxBy(_.getTimestamp)
            // For candidate, set the restore flag as false
            finalState.setToRestore(false)
            logger.info(s"Final choose the JobState: [${finalState.getLocation}] as the candidate for restoring the StreamJob")
            return finalState
          }
        } else {
          // get jobInfo from linkis
          throw new JobFetchErrorException(30030, s"task ${task.getId} got null linkisjobId.")
        }
        null
      case _ => null
    }
  }

  override def getJobDetailsVO(streamJob: StreamJob, version: String): JobDetailsVo = {
    val flinkJobInfo = getTaskJobInfo(streamJob.getId, version)
    val jobStateInfos = flinkJobInfo.getJobStates
    var manageMod = StreamJobMode.ENGINE_CONN
    if (JobConf.isCompleted(streamJob.getStatus)) {
      // should read param
      val value = streamJobConfMapper.getRawConfValue(streamJob.getId, JobConfKeyConstants.MANAGE_MODE_KEY.getValue)
      manageMod = Option(Utils.tryAndWarn(StreamJobMode.valueOf(value))).getOrElse(StreamJobMode.ENGINE_CONN)
    } else {
      manageMod = Option(streamJobMapper.getJobVersionById(streamJob.getId, version)) match {
        case Some(jobVersion) =>
          Option(Utils.tryQuietly(StreamJobMode
            .valueOf(jobVersion.getManageMode))).getOrElse(StreamJobMode.ENGINE_CONN)
        case _ => StreamJobMode.ENGINE_CONN
      }
    }

    val metricsStr = if (JobConf.SUPPORTED_MANAGEMENT_JOB_TYPES.getValue.contains(streamJob.getJobType)) null
    else if(jobStateInfos == null || jobStateInfos.length == 0) null
    else jobStateInfos(0).getLocation
    taskMetricsParser.find(_.canParse(streamJob)).map(_.parse(metricsStr)).filter { jobDetailsVO =>
      jobDetailsVO.setLinkisJobInfo(flinkJobInfo)
      jobDetailsVO.setManageMode(manageMod.getClientType)
      true
    }.getOrElse(throw new JobFetchErrorException(30030, s"Cannot find a TaskMetricsParser to parse job details."))
  }

  /**
   * Fetch the suitable job launch manager
   * @param streamTask stream task
   * @return
   */
  private def getJobLaunchManager(streamTask: StreamTask): JobLaunchManager[_ <: JobInfo] = {
    Option(streamTask.getJobType) match {
      case Some(jobType) =>
        var launchType = jobType
        if (launchType.indexOf(".") > 0){
          launchType = launchType.substring(0, launchType.indexOf("."))
        }
        val manager = JobLaunchManager.getJobManager(launchType.toLowerCase)
        if (null == manager){
          throw new JobErrorException(-1, s"Cannot find the suitable job launch manager for jobType: [${jobType}]")
        }
        manager
      case _ => this.jobLaunchManager
    }
  }

  override def queryErrorCode(jobId: Long): StreamTask = {
    streamTaskMapper.getLatestByJobId(jobId)
  }

  override def  errorCodeMatching(jobId: Long, streamTask: StreamTask): Future[_] = {
    var errorMsg =""
    val taskId =streamTask.getId
    val user =streamTask.getSubmitUser
      Utils.defaultScheduler.submit(new Runnable {
      override def run(): Unit = {
        Utils.tryCatch{
          breakable(
            for(i<-0 to 10) {
              val logs = getLog(jobId, taskId, user, "yarn",i*100)
              errorMsg =exceptionAnalyze(errorMsg,logs)
              if(errorMsg.nonEmpty){
                break()
              }
            })
        }{
          case e: Exception =>
            logger.error("errorCodeMatching failed. ", e)
        }
        if (errorMsg.isEmpty){
          Utils.tryCatch{
            breakable(
              for(i<-0 to 10) {
                val logs = getLog(jobId, taskId, user, "client",i*100)
                errorMsg =exceptionAnalyze(errorMsg,logs)
                if(errorMsg.nonEmpty){
                  break()
                }
              })
            if (errorMsg.isEmpty){
              errorMsg =JobConf.DEFAULT_ERROR_MSG.getHotValue()
            }
            val streamTask = new StreamTask()
            streamTask.setId(taskId);
            streamTask.setErrDesc(errorMsg)
            streamTaskService.updateTask(streamTask)
          }{
            case e: Exception =>
              logger.error("errorCodeMatching failed. ", e)
          }
        }
        val streamTask = new StreamTask()
        streamTask.setId(taskId);
        streamTask.setErrDesc(errorMsg)
        streamTaskService.updateTask(streamTask)
      }
    })
  }

  def getLog(jobId : Long,taskId: Long, username: String,logType: String, fromLine: Int): String = {
    val payload = new LogRequestPayload
    payload.setLogType(logType)
    payload.setFromLine(fromLine+1)
    payload.setPageSize(100)
    val realtimeLog = streamTaskService.getRealtimeLog(jobId, if (null != taskId) taskId else 0L, username, payload)
    val logs = realtimeLog.getLogs
    val logString =logs.asScala.mkString("\n")
    logString
  }

  def exceptionAnalyze(errorMsg: String, log: String): String = {
    if (null != log) {
      val errorCodes = errorCodeHandler.handle(log)
      if (errorCodes != null && errorCodes.size() > 0) {
        errorCodes.asScala.map(e => e.getErrorDesc).mkString(",")
      } else {
        errorMsg
      }
    } else {
      errorMsg
    }

  }
}
