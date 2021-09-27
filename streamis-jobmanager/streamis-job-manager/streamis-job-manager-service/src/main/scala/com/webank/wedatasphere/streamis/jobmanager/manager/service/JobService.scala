package com.webank.wedatasphere.streamis.jobmanager.manager.service


import com.webank.wedatasphere.linkis.common.utils.Logging
import com.webank.wedatasphere.streamis.jobmanager.manager.conf.JobConf
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.{StreamJobMapper, StreamJobRunRelationMapper, StreamProjectMapper}
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.{StreamJob, StreamJobRunRelation, StreamJobSqlResource, StreamJobVersion, StreamProject}
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.{JobFlowVO, PublishRequestVO, QueryJobListVO, TaskCoreNumVO}
import com.webank.wedatasphere.streamis.jobmanager.manager.util.DateUtils
import org.apache.commons.lang.StringUtils
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
/**
 * @author limeng
 */
@Service
class JobService extends Logging{
  @Autowired private var streamJobMapper:StreamJobMapper=_
  @Autowired private var streamProjectMapper:StreamProjectMapper=_
  @Autowired private var streamJobRunRelationMapper:StreamJobRunRelationMapper=_

  def getByProList(projectId:Long,jobName:String,jobStatus:Integer,jobCreator:String):java.util.List[QueryJobListVO] = {
    val jobLists = streamJobMapper.getJobLists(projectId, jobName, jobStatus, jobCreator).asScala
    if(jobLists != null && jobLists.nonEmpty){
      val list = new ListBuffer[QueryJobListVO]()
      jobLists.foreach(f=>{
        val queryJobList = new QueryJobListVO()
        queryJobList.setJobId(f.getId)
        queryJobList.setJobName(f.getName)
        queryJobList.setLabel(f.getLabel)
        queryJobList.setVersion(f.getCurrentVersion)
        queryJobList.setDescription(f.getDescription)
        queryJobList.setLastRelease(f.getCreateBy)
        queryJobList.setLastReleaseTime(DateUtils.formatDate(f.getCurrentReleaseTime))
        queryJobList.setTaskStatus(f.getStatus)
        list.append(queryJobList)
      })
      return list.toList.asJava
    }
    null
  }

  /**
   * 核心指标
   */
  def countByCores(proId: Long): TaskCoreNumVO ={
    val jobs = streamJobMapper.getByJobCount(proId).asScala
    if(jobs != null && jobs.nonEmpty){
      val taskNum = new TaskCoreNumVO()
      taskNum.setProjectId(proId)
      jobs.filter(f=> f.getStatus!= null).groupBy(_.getStatus).map(m=>(m._1,m._2.size)).foreach(fo=>{
        if(fo._1.equals(JobConf.JOBMANAGER_FLINK_JOB_STATUS_ONE.getValue)) taskNum.setSuccessNum(fo._2)
        if(fo._1.equals(JobConf.JOBMANAGER_FLINK_JOB_STATUS_TWO.getValue)) taskNum.setWaitRestartNum(fo._2)
        if(fo._1.equals(JobConf.JOBMANAGER_FLINK_JOB_STATUS_THREE.getValue)) taskNum.setAlertNum(fo._2)
        if(fo._1.equals(JobConf.JOBMANAGER_FLINK_JOB_STATUS_FOUR.getValue)) taskNum.setSlowTaskNum(fo._2)
        if(fo._1.equals(JobConf.JOBMANAGER_FLINK_JOB_STATUS_FIVE.getValue)) taskNum.setRunningNum(fo._2)
        if(fo._1.equals(JobConf.JOBMANAGER_FLINK_JOB_STATUS_SIX.getValue)) taskNum.setFailureNum(fo._2)
      })

      return taskNum
    }
    null
  }


  def getJobFlow(jobId:Long,version:String): JobFlowVO ={
    val job = streamJobMapper.getJobById(jobId)
    if(job == null) return null
    val jobVersions = streamJobMapper.getJobVersionsById(jobId, version)
    if(jobVersions == null || jobVersions.isEmpty) return null
    val jobVersionHead = jobVersions.asScala.head
    val jobFlow = new JobFlowVO()
    jobFlow.setName(job.getName)
    jobFlow.setJobId(jobId)
    jobFlow.setCreator(job.getCreateBy)
    jobFlow.setBmlVersion(jobVersionHead.getBmlVersion)
    jobFlow.setResourceId(jobVersionHead.getBmlId)
    jobFlow.setDescription(job.getDescription)
    jobFlow
  }



  /**
   * 任务版本
   * @param jobId
   */
  def getByJobVersion(jobId:Long): Unit ={
    val job = streamJobMapper.getJobById(jobId)
    if(job == null) return null
    val jobVersions = streamJobMapper.getJobVersionsById(jobId, job.getCurrentVersion)
    if(jobVersions == null || jobVersions.isEmpty) return null
    val jobVersionHead = jobVersions.asScala.head

  }

  /**
   * 发布
   * @param publishRequestVO
   */
  def publishToJobManager(publishRequestVO:PublishRequestVO): Unit ={
    //处理项目
    val projectName = publishRequestVO.getProjectName
    if(StringUtils.isEmpty(projectName)){
        error("projectName is null")
        return
    }
    if(StringUtils.isEmpty(publishRequestVO.getStreamisJobName)){
        error("jobName is null")
        return
    }

    val projectId = addPublishProject(publishRequestVO)
    publishRequestVO.setProjectId(projectId)
    addPublishJob(publishRequestVO)

  }

  /**
   * 存储项目
   * @param publishRequestVO
   */
  def addPublishProject(publishRequestVO:PublishRequestVO): Long ={
    val projectName = publishRequestVO.getProjectName
    val projectList = streamProjectMapper.getByProjects(null, null, projectName)
    if(projectList!=null && projectList.size() > 0){
      val project = new StreamProject()
      BeanUtils.copyProperties(projectList.asScala.head,project)
      streamProjectMapper.updateProject(project)
      project.getId
    }else{
      val project = new StreamProject()
      project.setName(projectName)
      project.setCreateBy(publishRequestVO.getCreateBy)
      streamProjectMapper.instertProject(project)
      project.getId
    }
  }

  private def addPublishJob(publishRequestVO:PublishRequestVO): Unit ={
    val jobName = publishRequestVO.getStreamisJobName
    val jobLists = streamJobMapper.getJobLists(null, jobName, null, null)
    val job = new StreamJob()
    val jobVersion = new StreamJobVersion()
    val jobSql = new StreamJobSqlResource()

    if(jobLists != null && jobLists.size() > 0) {
      val jobHead = jobLists.asScala.head
      BeanUtils.copyProperties(jobHead,job)
      val tags = publishRequestVO.getTags
      if(tags != null && tags.size() > 0){
        job.setLabel(tags.asScala.mkString(","))
      }

      job.setCreateBy(publishRequestVO.getCreateBy)
      publishRequestVO.getType match {
        case "sql"=> job.setJobType(JobConf.JOBMANAGER_FLINK_SQL.getValue)
      }
      val preVersion = job.getCurrentVersion

      job.setCurrentVersion(publishRequestVO.getVersion)
      streamJobMapper.updateJob(job)

      jobVersion.setJobId(job.getId)
      jobVersion.setVersion(publishRequestVO.getVersion)
      streamJobMapper.insertJobVersion(jobVersion)

      publishRequestVO.getType match {
        case "sql"=> {
          jobSql.setJobVersionId(jobVersion.getId)
          jobSql.setExecuteSql(publishRequestVO.getExecutionCode)
          streamJobMapper.updateJobSqlResource(jobSql)
        }
      }

      val preVersionList = streamJobMapper.getJobVersionsById(job.getId,preVersion)
      val jobRunRelation = new StreamJobRunRelation()
      jobRunRelation.setJobId(job.getId)
      jobRunRelation.setParentId(preVersionList.asScala.head.getId)
      jobRunRelation.setJobVersionId(jobVersion.getId)

      addJobRunRelation(job.getStatus.toInt,jobRunRelation)

    }else{
      val tags = publishRequestVO.getTags
      if(tags != null && tags.size() > 0){
        job.setLabel(tags.asScala.mkString(","))
      }

      publishRequestVO.getType match {
        case "sql"=> job.setJobType(JobConf.JOBMANAGER_FLINK_SQL.getValue)
      }
      job.setProjectId(publishRequestVO.getProjectId)
      job.setName(publishRequestVO.getStreamisJobName)
      job.setCurrentVersion(publishRequestVO.getVersion)
      job.setCreateBy(publishRequestVO.getCreateBy)
      job.setSubmitUser(publishRequestVO.getPublishUser)
      streamJobMapper.insertJob(job)

      jobVersion.setJobId(job.getId)
      jobVersion.setVersion(publishRequestVO.getVersion)
      streamJobMapper.insertJobVersion(jobVersion)

      publishRequestVO.getType match {
        case "sql"=> {
          jobSql.setJobVersionId(jobVersion.getId)
          jobSql.setExecuteSql(publishRequestVO.getExecutionCode)
          streamJobMapper.insertJobSqlResource(jobSql)
        }
      }

    }

  }

  def addJobRunRelation(status:Int,jobRunRelation:StreamJobRunRelation): Unit ={
    val relations = streamJobRunRelationMapper.getJobRunRelationList(jobRunRelation.getJobId)
    if(relations == null || relations.isEmpty){
      jobRunRelation.setParentId(null)
    }
    status match {
      case JobConf.JOBMANAGER_FLINK_JOB_STATUS_TWO.getValue => {
        streamJobRunRelationMapper.insertJobRunRelation(jobRunRelation)
      }
      case JobConf.JOBMANAGER_FLINK_JOB_STATUS_THREE.getValue => {
        streamJobRunRelationMapper.insertJobRunRelation(jobRunRelation)
      }
      case JobConf.JOBMANAGER_FLINK_JOB_STATUS_FOUR.getValue => {
        streamJobRunRelationMapper.insertJobRunRelation(jobRunRelation)
      }
    }
  }


}
