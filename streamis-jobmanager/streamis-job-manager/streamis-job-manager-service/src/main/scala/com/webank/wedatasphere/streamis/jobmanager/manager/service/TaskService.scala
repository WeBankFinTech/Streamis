package com.webank.wedatasphere.streamis.jobmanager.manager.service


import java.util
import java.util.Date

import com.webank.wedatasphere.linkis.common.utils.Logging
import com.webank.wedatasphere.linkis.httpclient.dws.DWSHttpClient
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.LinkisJobManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LaunchJob
import com.webank.wedatasphere.streamis.jobmanager.manager.conf.JobConf
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.{StreamJobMapper, StreamTaskMapper}
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.{JobProgressVO, StreamTaskListVO}
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.{StreamJob, StreamTask}
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
/**
 * @author limeng
 */
@Service
class TaskService extends Logging{

  @Autowired private var streamTaskMapper:StreamTaskMapper=_
  @Autowired private var streamJobMapper:StreamJobMapper=_
  @Autowired private var streamisTransformJobBuilders: Array[StreamisTransformJobBuilder] = _


  def saveJob(streamJob:StreamJob): Unit ={
    streamJobMapper.insertJob(streamJob)
  }

  def updateJob(streamJob:StreamJob): Unit ={
    streamJobMapper.updateJob(streamJob)
  }

  def saveJobVersion(): Unit ={

  }

  def updateJobVersion(): Unit ={

  }

  /**
   * 执行job
   * @param jobId
   */
    @Transactional(rollbackFor = Array(classOf[Exception]))
  def executeJob(jobId: Long, userName: String): Unit = {
    val jobs = streamJobMapper.getJobById(jobId)
    if(jobs == null)
      throw new JobExecuteFailedErrorException(30350, s"StreamJob-$jobId is not exists.")
    else if(StringUtils.isEmpty(jobs.getCurrentVersion))
      throw new JobExecuteFailedErrorException(30350, s"StreamJob-${jobs.getName} does not exists any JobVersion.")
    val jobVersions = streamJobMapper.getJobVersionsById(jobId, jobs.getCurrentVersion)
    val jobVersionSize = if(jobVersions == null) 0 else jobVersions.size()
    if(jobVersionSize != 1) throw new JobExecuteFailedErrorException(30350,
      s"StreamJob-${jobs.getName} has $jobVersionSize JobVersions with version-${jobs.getCurrentVersion}.")
    val versionsJob = jobVersions.get(0)
    val task = if(jobs.getCurrentTaskId == null){
      saveTask(jobs.getId, versionsJob.getId, userName, new Date(), versionsJob.getVersion)
    } else {
      val task = streamTaskMapper.getTaskById(jobs.getCurrentTaskId)
      if(!JobConf.isCompleted(task.getStatus)) throw new JobExecuteFailedErrorException(30350, s"StreamJob-${jobs.getName} is in ${JobConf.getStatusString(task.getStatus)}, please stop it in first.")
      saveTask(jobs.getId, versionsJob.getId, userName, new Date(), versionsJob.getVersion)
    }
    val streamJob = new StreamJob
    streamJob.setId(jobId)
    streamJob.setCurrentTaskId(task.getId)
    streamJobMapper.updateJob(streamJob)
    val transformJob = streamisTransformJobBuilders.find(_.canBuild(jobs)).map(_.build(jobs))
      .getOrElse(throw new TransformFailedErrorException(30408, s"Cannot find a TransformJobBuilder to build StreamJob ${jobs.getName}."))
    var launchJob = LaunchJob.builder().setSubmitUser(userName).build()
    launchJob = Transform.getTransforms.foldLeft(launchJob)((job, transform) => transform.transform(transformJob, job))
    info(s"StreamJob-${jobs.getName} has transformed with launchJob $launchJob.")
    //TODO getLinkisJobManager should use jobManagerType to instance in future, since not only `simpleFlink` mode is supported in future.
    val id = LinkisJobManager.getLinkisJobManager.launch(launchJob)
    task.setLinkisJobId(id)
    info(s"StreamJob-${jobs.getName} has launched with id $id.")
    TaskService.updateStreamTaskStatus(task, jobs.getName, userName)
    streamTaskMapper.updateTask(task)
  }

  def stopJob(jobId: Long, userName: String): Unit ={
    val jobs = streamJobMapper.getJobById(jobId)
    if(jobs == null) throw new JobStopFailedErrorException(30355, s"Job-$jobId is not exists.")
    info(s"Try to stop StreamJob-${jobs.getName}.")
    val oldTask = streamTaskMapper.getTaskById(jobs.getCurrentTaskId)
    if(oldTask == null) throw new JobStopFailedErrorException(30355, s"Task-${jobs.getCurrentTaskId} is not exists.")
    LinkisJobManager.getLinkisJobManager.stop(oldTask.getLinkisJobId, userName)
    info(s"StreamJob-${jobs.getName} has stopped with taskId ${oldTask.getId} and linkisJobId ${oldTask.getLinkisJobId}.")
    val taskModel = new StreamTask()
    taskModel.setId(oldTask.getId)
    taskModel.setLastUpdateTime(new Date)
    taskModel.setEndTime(new Date)
    taskModel.setStatus(JobConf.JOBMANAGER_FLINK_JOB_STATUS_SEVEN.getValue)
    streamTaskMapper.updateTask(taskModel)
  }

  /**
   * 查询运行历史
   * @param jobId
   * @param version
   * @return
   */
  def executeHistory(jobId: Long, version: String): util.List[StreamTaskListVO] ={
    if(StringUtils.isEmpty(version)) throw new JobFetchFailedErrorException(30355, "version cannot be empty.")
    val job = streamJobMapper.getJobById(jobId)
    if(job == null) throw new JobFetchFailedErrorException(30355, s"Unknown job $jobId.")
    val jobVersions = streamJobMapper.getJobVersionsById(jobId, version)
    if(jobVersions == null || jobVersions.isEmpty) return new util.ArrayList[StreamTaskListVO]
    val jobVersion = jobVersions.asScala.head
    val tasks = streamTaskMapper.getByJobIds(jobVersion.getId, version)
    if(tasks == null || tasks.isEmpty) return new util.ArrayList[StreamTaskListVO]
    val list = new util.ArrayList[StreamTaskListVO]
    tasks.asScala.foreach{ f =>
      val svo = new StreamTaskListVO()
      svo.setTaskId(f.getId)
      svo.setStatus(f.getStatus)
      svo.setCreator(f.getSubmitUser)
      svo.setVersion(f.getVersion)
      svo.setJobName(job.getName)
      svo.setStartTime(DateUtils.formatDate(f.getStartTime))
      svo.setEndTime(DateUtils.formatDate(f.getEndTime))
      svo.setJobVersionId(f.getJobVersionId)
      svo.setRunTime(DateUtils.intervals(f.getStartTime,f.getEndTime))
      svo.setStopCause(f.getErrDesc)
      list.add(svo)
    }
    list
  }

  /**
   * 每次任务状态
   * @param jobId
   * @return
   */
  def getByJobStatus(jobId:Long): JobProgressVO ={
    val job = streamJobMapper.getJobById(jobId)
    if(job == null) return null
    if(job.getCurrentTaskId == null) return null
    val task = streamTaskMapper.getTaskById(job.getCurrentTaskId)
    if(task == null) return null
    val jobProgressVO = new JobProgressVO()
    jobProgressVO.setTaskId(job.getCurrentTaskId)
    jobProgressVO.setProgress(task.getStatus)
    jobProgressVO
  }

  private def saveTask(jobId: Long, jobVersionId:Long, userName:String, date:Date, version:String): StreamTask ={
    val task = new StreamTask()
    task.setJobId(jobId)
    task.setVersion(version)
    task.setStatus(JobConf.JOBMANAGER_FLINK_JOB_STATUS_FIVE.getValue)
    task.setJobVersionId(jobVersionId)
    task.setStartTime(date)
    task.setLastUpdateTime(date)
    task.setSubmitUser(userName)
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