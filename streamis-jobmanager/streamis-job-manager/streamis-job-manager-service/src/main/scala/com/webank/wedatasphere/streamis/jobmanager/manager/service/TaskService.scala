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
import java.util.Date

import org.apache.linkis.common.utils.Logging
import org.apache.linkis.httpclient.dws.DWSHttpClient
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.LinkisJobManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.{LaunchJob, LogRequestPayload}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.manager.FlinkJobManager
import com.webank.wedatasphere.streamis.jobmanager.manager.conf.JobConf
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.{StreamJobMapper, StreamTaskMapper}
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamTask
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.{JobProgressVO, StreamTaskListVO}
import com.webank.wedatasphere.streamis.jobmanager.manager.exception.{JobExecuteFailedErrorException, JobFetchFailedErrorException, JobStopFailedErrorException}
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.exception.TransformFailedErrorException
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.{StreamisTransformJobBuilder, Transform}
import com.webank.wedatasphere.streamis.jobmanager.manager.util.DateUtils
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConverters._


@Service
class TaskService extends Logging{

  @Autowired private var streamTaskMapper:StreamTaskMapper=_
  @Autowired private var streamJobMapper:StreamJobMapper=_
  @Autowired private var streamisTransformJobBuilders: Array[StreamisTransformJobBuilder] = _


  /**
   * Execute Job(执行job)
   * @param jobId
   */
    @Transactional(rollbackFor = Array(classOf[Exception]))
  def executeJob(jobId: Long, userName: String): Unit = {
    val job = streamJobMapper.getJobById(jobId)
    if(job == null)
      throw new JobExecuteFailedErrorException(30350, s"StreamJob-$jobId is not exists.")
    info(s"Try to start StreamJob-${job.getName}.")
    val streamJobVersions = streamJobMapper.getJobVersions(jobId)
    if(streamJobVersions == null || streamJobVersions.isEmpty)
      throw new JobExecuteFailedErrorException(30350, s"StreamJob-${job.getName} does not exists any JobVersion.")
    val jobVersion = streamJobVersions.get(0)
    val tasks = streamTaskMapper.getTasksByJobIdAndJobVersionId(jobVersion.getJobId, jobVersion.getId)
    val task = if(tasks == null || tasks.size() < 1 || !JobConf.isRunning(tasks.asScala.head.getStatus)){
      saveTask(job.getId, jobVersion.getId, userName, new Date(),jobVersion.getVersion)
    } else {
      throw new JobExecuteFailedErrorException(30350, s"StreamJob-${job.getName} is in ${JobConf.getStatusString(tasks.get(0).getStatus)}, please stop it in first.")
    }
    info(s"A new task(taskId: ${task.getId}, jobVersion: ${jobVersion.getVersion}) is created for StreamJob-${job.getName}.")
    val transformJob = streamisTransformJobBuilders.find(_.canBuild(job)).map(_.build(job))
      .getOrElse(throw new TransformFailedErrorException(30408, s"Cannot find a TransformJobBuilder to build StreamJob ${job.getName}."))
    var launchJob = LaunchJob.builder().setSubmitUser(userName).build()
    launchJob = Transform.getTransforms.foldLeft(launchJob)((job, transform) => transform.transform(transformJob, job))
    info(s"StreamJob-${job.getName} has transformed with launchJob $launchJob.")
    //TODO getLinkisJobManager should use jobManagerType to instance in future, since not only `simpleFlink` mode is supported in future.
    val id = LinkisJobManager.getLinkisJobManager.launch(launchJob)
      task.setLinkisJobId(id)
    info(s"StreamJob-${job.getName} has launched with id $id.")
    TaskService.updateStreamTaskStatus(task, job.getName, userName)
    streamTaskMapper.updateTask(task)
  }

  def stopJob(jobId: Long, userName: String): Unit ={
    val job = streamJobMapper.getJobById(jobId)
    if(job == null) throw new JobStopFailedErrorException(30355, s"Job-$jobId is not exists.")
    info(s"Try to stop StreamJob-${job.getName}.")
    val jobVersions = streamJobMapper.getJobVersions(jobId)
    if(jobVersions == null || jobVersions.isEmpty)
      throw new JobExecuteFailedErrorException(30350, s"StreamJob-${job.getName} does not exists any JobVersion.")
    val oldTasks = streamTaskMapper.getTasksByJobIdAndJobVersionId(job.getId, jobVersions.get(0).getId)
    if(oldTasks == null || oldTasks.isEmpty || JobConf.isCompleted(oldTasks.get(0).getStatus))
      throw new JobStopFailedErrorException(30355, s"StreamJob-${job.getName} has not bean started.")
    val oldTask = oldTasks.get(0)
    info(s"Try to stop StreamJob-${job.getName} with task(taskId: ${oldTask.getId}, linkisJobId: ${oldTask.getLinkisJobId}).")
    LinkisJobManager.getLinkisJobManager.stop(oldTask.getLinkisJobId, userName)
    info(s"StreamJob-${job.getName} has stopped with task(taskId: ${oldTask.getId}, linkisJobId: ${oldTask.getLinkisJobId}).")
    val taskModel = new StreamTask()
    taskModel.setId(oldTask.getId)
    taskModel.setLastUpdateTime(new Date)
    taskModel.setStatus(JobConf.JOBMANAGER_FLINK_JOB_STATUS_SEVEN.getValue)
    streamTaskMapper.updateTask(taskModel)
  }

  /**
   * Query execute history(查询运行历史)
   * @param jobId
   * @param version
   * @return
   */
  def executeHistory(jobId: Long, version: String): util.List[StreamTaskListVO] ={
    if(StringUtils.isEmpty(version)) throw new JobFetchFailedErrorException(30355, "version cannot be empty.")
    val job = streamJobMapper.getJobById(jobId)
    if(job == null) throw new JobFetchFailedErrorException(30355, s"Unknown job $jobId.")
    val jobVersion = streamJobMapper.getJobVersionById(jobId, version)
    if(jobVersion == null) return new util.ArrayList[StreamTaskListVO]
    val tasks = streamTaskMapper.getByJobVersionId(jobVersion.getId, version)
    if(tasks == null || tasks.isEmpty) return new util.ArrayList[StreamTaskListVO]
    val list = new util.ArrayList[StreamTaskListVO]
    tasks.asScala.foreach{ f =>
      val svo = new StreamTaskListVO()
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

  def getRealtimeLog(jobId: Long, userName: String, requestPayload: LogRequestPayload): util.Map[String, Any] = {
    val streamTask = streamTaskMapper.getRunningTaskByJobId(jobId)
    LinkisJobManager.getLinkisJobManager match {
      case jobManager: FlinkJobManager =>
        val logIterator = jobManager.fetchLogs(streamTask.getLinkisJobId, streamTask.getSubmitUser, requestPayload)
        val returnMap = new util.HashMap[String, Any]
        returnMap.put("logPath", logIterator.getLogPath)
        returnMap.put("logs", logIterator.getLogs)
        logIterator.close()
        returnMap
    }
  }

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
   * job status(每次任务状态)
   * @param jobId
   * @return
   */
  def getByJobStatus(jobId:Long, version: String): JobProgressVO ={
    val jobVersion = streamJobMapper.getJobVersionById(jobId, version)
    if(jobVersion == null) return new JobProgressVO
    val tasks = streamTaskMapper.getTasksByJobIdAndJobVersionId(jobVersion.getJobId, jobVersion.getId)
    if(tasks == null || tasks.isEmpty) return new JobProgressVO
    val task = tasks.get(0)
    val jobProgressVO = new JobProgressVO()
    jobProgressVO.setTaskId(task.getId)
    jobProgressVO.setProgress(task.getStatus)
    jobProgressVO
  }

  private def saveTask(jobId: Long, jobVersionId: Long, userName:String, date:Date,version:String): StreamTask ={
    val task = new StreamTask()
    task.setJobId(jobId)
    task.setStatus(JobConf.JOBMANAGER_FLINK_JOB_STATUS_FIVE.getValue)
    task.setJobVersionId(jobVersionId)
    task.setStartTime(date)
    task.setLastUpdateTime(date)
    task.setSubmitUser(userName)
    task.setVersion(version)
    streamTaskMapper.insertTask(task)
    task
  }


}

object TaskService {

  def updateStreamTaskStatus(task: StreamTask, jobName: String, executeUser: String)(implicit logger: Logger): Unit = {
    val jobInfo = LinkisJobManager.getLinkisJobManager.getJobInfo(task.getLinkisJobId, executeUser)
    logger.info(s"StreamJob-$jobName is ${jobInfo.getStatus} with $jobInfo.")
    task.setLastUpdateTime(new Date)
    task.setStatus(JobConf.linkisStatusToStreamisStatus(jobInfo.getStatus))
    if(JobConf.isCompleted(task.getStatus) && StringUtils.isNotEmpty(jobInfo.getCompletedMsg))
      task.setErrDesc(jobInfo.getCompletedMsg)
    task.setLinkisJobInfo(DWSHttpClient.jacksonJson.writeValueAsString(jobInfo))
  }
}