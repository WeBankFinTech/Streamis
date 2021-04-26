package com.webank.wedatasphere.streamis.jobmanager.manager.service


import com.webank.wedatasphere.linkis.common.utils.Logging
import com.webank.wedatasphere.streamis.jobmanager.manager.conf.JobConf
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.{StreamJobMapper, StreamTaskMapper}
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.{StreamJob, StreamJobVersion, StreamTask}
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.{JobProgressVO, StreamTaskListVO}
import com.webank.wedatasphere.streamis.jobmanager.manager.util.DateUtils
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.util.Date
import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
/**
 * @author limeng
 */
@Service
class TaskService extends Logging{

  @Autowired private var streamTaskMapper:StreamTaskMapper=_
  @Autowired private var streamJobMapper:StreamJobMapper=_


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
  def executeJob(jobId: Long,userName:String): Unit ={
    val jobs = streamJobMapper.getJobById(jobId)
    if(jobs == null || StringUtils.isEmpty(jobs.getCurrentVersion)) return
    val versionsJob: StreamJobVersion = streamJobMapper.getJobVersionsById(jobId, jobs.getCurrentVersion).asScala.head
    var taskId:Long = 0
    if(jobs.getCurrentTaskId == null){
      taskId = saveTask(versionsJob.getId,userName,new Date(),"v1")
    }else{
      val task = streamTaskMapper.getTaskById(jobs.getCurrentTaskId)
      val versionVal = task.getVersion.split("v").last.toInt+1
      taskId = saveTask(versionsJob.getId,userName,new Date(),"v"+versionVal)
    }
    val streamJob = new StreamJob()
    streamJob.setId(jobId)
    streamJob.setCurrentTaskId(taskId)
    streamJobMapper.updateJob(streamJob)
    //todo 执行linkis

  }

  def stopJob(jobId: Long,userName:String): Unit ={
    //todo 执行linkis
    val jobs = streamJobMapper.getJobById(jobId)
    if(jobs == null) return
    val task = streamTaskMapper.getTaskById(jobs.getCurrentTaskId)
    if(task==null) return
    val taskModel = new StreamTask()
    taskModel.setId(task.getId)
    taskModel.setStatus(JobConf.JOBMANAGER_FLINK_JOB_STATUS_SEVEN.getValue)
    streamTaskMapper.updateTask(taskModel)

    val versionsJob: StreamJobVersion = streamJobMapper.getJobVersionsById(jobId, jobs.getCurrentVersion).asScala.head
    val taskId = saveTask(versionsJob.getId,userName,new Date(),"v1")
    val streamJob = new StreamJob()
    streamJob.setId(jobId)
    streamJob.setCurrentTaskId(taskId)
    streamJobMapper.updateJob(streamJob)
  }

  /**
   * 查询运行历史
   * @param jobId
   * @param version
   * @return
   */
  def executeHistory(jobId:Long,version:String): java.util.List[StreamTaskListVO] ={
    if(version == null || version =="") return null
    val jobVersions = streamJobMapper.getJobVersionsById(jobId, version)
    if(jobVersions==null || jobVersions.isEmpty) return null
    val jobVersionId = jobVersions.asScala.head.getId
    val tasks = streamTaskMapper.getByJobIds(jobVersionId, null)
    if(tasks == null || tasks.isEmpty) return null
    val list = new ListBuffer[StreamTaskListVO]
    tasks.asScala.foreach(f=>{
      val svo = new StreamTaskListVO()
      svo.setTaskId(f.getId)
      svo.setStatus(f.getStatus)
      svo.setCreator(f.getSubmitUser)
      svo.setVersion(f.getVersion)
      svo.setJobName(f.getJobName)
      svo.setStartTime(DateUtils.formatDate(f.getStartTime))
      svo.setEndTime(DateUtils.formatDate(f.getEndTime))
      svo.setJobVersionId(f.getJobVersionId)
      svo.setRunTime(DateUtils.intervals(f.getStartTime,f.getEndTime))
      svo.setStopCause(f.getErrDesc)
      list.append(svo)
    })
    list.asJava
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

  private def saveTask(jobVersionId:Long,userName:String,date:Date,version:String): Long ={
    val task = new StreamTask()
    task.setVersion(version)
    task.setStatus(JobConf.JOBMANAGER_FLINK_JOB_STATUS_FIVE.getValue)
    task.setJobVersionId(jobVersionId)
    task.setStartTime(date)
    task.setEndTime(date)
    task.setSubmitUser(userName)
    streamTaskMapper.insertTask(task)
    task.getId
  }


}
