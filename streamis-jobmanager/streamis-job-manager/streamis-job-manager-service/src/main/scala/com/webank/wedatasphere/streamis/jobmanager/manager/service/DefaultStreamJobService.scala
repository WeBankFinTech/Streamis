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
import java.util.{Date, List}
import com.github.pagehelper.PageInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.conf.JobConfKeyConstants
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf.JobConf
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.constants.JobConstants
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.exception.{JobCreateErrorException, JobErrorException, JobFetchErrorException}
import com.webank.wedatasphere.streamis.jobmanager.launcher.service.StreamJobConfService
import com.webank.wedatasphere.streamis.jobmanager.manager.alert.AlertLevel
import com.webank.wedatasphere.streamis.jobmanager.manager.constrants.JobConstrants
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.{StreamAlertMapper, StreamJobMapper, StreamTaskMapper}
import com.webank.wedatasphere.streamis.jobmanager.manager.entity._
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.{JobStatusVo, QueryJobListVo, TaskCoreNumVo, VersionDetailVo}
import com.webank.wedatasphere.streamis.jobmanager.manager.service.DefaultStreamJobService.JobDeployValidateResult
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.JobContentParser
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity.StreamisTransformJobContent
import com.webank.wedatasphere.streamis.jobmanager.manager.util.{JobUtils, JsonUtils, ReaderUtils, ZipHelper}
import com.webank.wedatasphere.streamis.jobmanager.manager.utils.SourceUtils
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.ObjectUtils
import org.apache.linkis.common.exception.ErrorException
import org.apache.linkis.common.utils.Logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConverters._


@Service
class DefaultStreamJobService extends StreamJobService with Logging {

  @Autowired
  private var streamJobMapper: StreamJobMapper = _
  @Autowired
  private var streamTaskMapper: StreamTaskMapper = _
  @Autowired
  private var bmlService: BMLService = _
  @Autowired
  private var jobContentParsers: Array[JobContentParser] = _
  @Autowired
  private var streamJobConfService: StreamJobConfService = _
  @Autowired
  private var streamAlertMapper:StreamAlertMapper = _

  override def getJobById(jobId: Long): StreamJob = {
    this.streamJobMapper.getJobById(jobId)
  }
  override def getLatestJobVersion(jobId: Long): StreamJobVersion = {
    this.streamJobMapper.getLatestJobVersion(jobId)
  }

  override def getJobByName(jobName: String): util.List[StreamJob] = streamJobMapper.getJobByName(jobName)

  override def getByProList(projectName: String, userName: String, jobName: String, jobStatus: Integer, jobCreator: String, label: String, enable: java.lang.Boolean = null,jobType :String): PageInfo[QueryJobListVo] = {
    var streamJobList: util.List[QueryJobListVo] = null
    if (StringUtils.isNotBlank(jobName) && jobName.contains(JobConstants.JOB_NAME_DELIMITER)) {
      val jobNameList = new util.ArrayList[String]()
      jobName.split(JobConstants.JOB_NAME_DELIMITER).filter(StringUtils.isNotBlank(_)).foreach(jobNameList.add)
      streamJobList = streamJobMapper.getJobLists(projectName, userName, null, jobStatus, jobCreator, JobUtils.escapeChar(label), JobConfKeyConstants.MANAGE_MODE_KEY.getValue, jobNameList, enable,jobType)
    } else {
      streamJobList = streamJobMapper.getJobLists(projectName, userName, JobUtils.escapeChar(jobName), jobStatus, jobCreator, JobUtils.escapeChar(label), JobConfKeyConstants.MANAGE_MODE_KEY.getValue, null, enable,jobType)
    }
    if (streamJobList != null && !streamJobList.isEmpty) {
      val pageInfo = new PageInfo[QueryJobListVo](streamJobList)
      return pageInfo
    }
    new PageInfo[QueryJobListVo](new util.ArrayList[QueryJobListVo]())
  }

  /**
   * Page list query of version info
   *
   * @param jobId job id
   * @return
   */
  override def getVersionList(jobId: Long): PageInfo[VersionDetailVo] = {
    val jobVersions = streamJobMapper.getJobVersionDetails(jobId)
    if (null == jobVersions){
      new PageInfo[VersionDetailVo](new util.ArrayList[VersionDetailVo]())
    } else new PageInfo[VersionDetailVo](jobVersions)
  }

  /**
   * COre indicator(核心指标)
   */
  override def countByCores(projectName: String, userName: String): TaskCoreNumVo = {
    val jobs = streamJobMapper.getJobLists(projectName, userName, null, null, null, null, JobConfKeyConstants.MANAGE_MODE_KEY.getValue, null,null,null)
    val taskNum = new TaskCoreNumVo()
    taskNum.setProjectName(projectName)
    if (jobs != null && !jobs.isEmpty) {
      jobs.asScala.filter(_.getStatus != null).groupBy(_.getStatus).map(m => (m._1, m._2.size)).foreach(fo => {
        if (fo._1.equals(JobConf.FLINK_JOB_STATUS_COMPLETED.getValue)) taskNum.setSuccessNum(fo._2)
        else if (fo._1.equals(JobConf.FLINK_JOB_STATUS_WAIT_RESTART.getValue)) taskNum.setWaitRestartNum(fo._2)
        else if (fo._1.equals(JobConf.FLINK_JOB_STATUS_ALERT_RUNNING.getValue)) taskNum.setAlertNum(fo._2)
        else if (fo._1.equals(JobConf.FLINK_JOB_STATUS_SLOW_RUNNING.getValue)) taskNum.setSlowTaskNum(fo._2)
        else if (fo._1.equals(JobConf.FLINK_JOB_STATUS_RUNNING.getValue)) taskNum.setRunningNum(fo._2)
        else if (fo._1.equals(JobConf.FLINK_JOB_STATUS_FAILED.getValue)) taskNum.setFailureNum(fo._2)
        else if (fo._1.equals(JobConf.FLINK_JOB_STATUS_STOPPED.getValue)) taskNum.setStoppedNum(fo._2)
        else if (fo._1.equals(JobConf.FLINK_JOB_STATUS_STARTING.getValue)) taskNum.setStartingNum(fo._2)
        else if (fo._1.equals(JobConf.FLINK_JOB_STATUS_STOPPING.getValue)) taskNum.setStoppingNum(fo._2)
      })
    }
    taskNum
  }

  /**
   * job version detail(任务版本详情)
   *
   * @param jobId
   */
  override def versionDetail(jobId: Long, version: String): VersionDetailVo = {
    streamJobMapper.getVersionDetail(jobId, version)
  }


  override def rollingJobVersion(preVersion: String): String = {
    val newVersion = preVersion.substring(1).toInt + 1
    val codeFormat = "%05d"
    "v" + String.format(codeFormat, new Integer(newVersion))
  }

  override def uploadFiles(metaJsonInfo: MetaJsonInfo, version: StreamJobVersion, path: String): Unit = {
    val readerUtils = new ReaderUtils
    readerUtils.listFiles(path).asScala.foreach(path => {
      val response = bmlService.upload(version.getCreateBy, path)
      val jobVersionFiles = new StreamJobVersionFiles
      jobVersionFiles.setJobId(version.getJobId)
      jobVersionFiles.setJobVersionId(version.getId)
      jobVersionFiles.setCreateBy(version.getCreateBy)
      jobVersionFiles.setVersion(version.getVersion)
      jobVersionFiles.setFileName(readerUtils.getFileName(path))
      jobVersionFiles.setCreateTime(new Date(System.currentTimeMillis()))
      jobVersionFiles.setStorePath(readerUtils.readAsJson(response.get("version").toString, response.get("resourceId").toString))
      streamJobMapper.insertJobVersionFiles(jobVersionFiles)
    })
  }


  override def deployStreamJob(streamJob: StreamJob,
                               metaJsonInfo: MetaJsonInfo, userName: String, updateVersion: Boolean, source: String = null): StreamJobVersion = {
    if(StringUtils.isBlank(metaJsonInfo.getJobType))
      throw new JobCreateErrorException(30030, s"jobType is needed.")
    else if(!JobConf.SUPPORTED_JOB_TYPES.getValue.contains(metaJsonInfo.getJobType)) {
      throw new JobCreateErrorException(30030, s"jobType ${metaJsonInfo.getJobType} is not supported.")
    }
    if(metaJsonInfo.getJobContent == null || metaJsonInfo.getJobContent.isEmpty)
      throw new JobCreateErrorException(30030, s"jobContent is needed.")
    val jobVersion = new StreamJobVersion()
    val newStreamJob = new StreamJob()
    if (streamJob == null) {
      logger.info("StreamJob is null, create a new streamJob")
      jobVersion.setVersion("v00001")
      newStreamJob.setCreateBy(userName)
      newStreamJob.setSubmitUser(userName)
      newStreamJob.setJobType(metaJsonInfo.getJobType)
      newStreamJob.setDescription(metaJsonInfo.getDescription)
      newStreamJob.setCurrentVersion(jobVersion.getVersion)
      newStreamJob.setCreateTime(new Date())
      newStreamJob.setLabel(metaJsonInfo.getTags)
      newStreamJob.setName(metaJsonInfo.getJobName)
      newStreamJob.setProjectName(metaJsonInfo.getProjectName)
      newStreamJob.setEnable(true)
      streamJobMapper.insertJob(newStreamJob)
    } else {
      val jobVersions = streamJobMapper.getJobVersions(streamJob.getId)
      if (jobVersions == null || jobVersions.isEmpty) jobVersion.setVersion("v00001")
      else
        jobVersion.setVersion(rollingJobVersion(jobVersions.get(0).getVersion))
      if(streamJob.getJobType != metaJsonInfo.getJobType)
        throw new JobCreateErrorException(30030, s"StreamJob-${streamJob.getName} has already created with jobType ${streamJob.getJobType}, you cannot change it to ${metaJsonInfo.getJobType}.")
      streamJob.setId(streamJob.getId)
      if (updateVersion){
        // update version
        streamJob.setCurrentVersion(jobVersion.getVersion)
      }
      if (StringUtils.isNotEmpty(metaJsonInfo.getDescription))
        streamJob.setDescription(metaJsonInfo.getDescription)
      streamJobMapper.updateJob(streamJob)
    }
    if (ObjectUtils.isNotEmpty(streamJob)) {
      jobVersion.setJobId(streamJob.getId)
    } else {
      logger.info("newStreamJob is {}", newStreamJob)
      jobVersion.setJobId(newStreamJob.getId)
    }
    jobVersion.setJobContent(metaJsonInfo.getMetaInfo)
    jobVersion.setCreateBy(userName)
    jobVersion.setCreateTime(new Date)
    jobVersion.setSource(source)
    if (StringUtils.isNotBlank(metaJsonInfo.getComment))
      jobVersion.setComment(metaJsonInfo.getComment)
    else jobVersion.setComment("upload by user.")
    // Should build unique key using job id and version number, to avoid the duplicate version
    streamJobMapper.insertJobVersion(jobVersion)
    jobVersion
  }

  @throws(classOf[ErrorException])
  @Transactional(rollbackFor = Array(classOf[Exception]))
  override def uploadJob(projectName: String, userName: String, inputZipPath: String,source: String): StreamJobVersion = {
    val inputPath = ZipHelper.unzip(inputZipPath)
    val readerUtils = new ReaderUtils
    val metaJsonInfo = readerUtils.parseJson(inputPath)
    if (StringUtils.isNotBlank(projectName) && !projectName.equals(metaJsonInfo.getProjectName)) {
      if (JobConf.PROJECT_NAME_STRICT_CHECK_SWITCH.getHotValue && StringUtils.isNotBlank(metaJsonInfo.getProjectName)){
        logger.error(s"The projectName [${projectName}] dose not match metaJson ProjectName [${metaJsonInfo.getProjectName}]")
        throw new JobCreateErrorException(30030,s"The projectName [${projectName}] dose not match metaJson ProjectName [${metaJsonInfo.getProjectName}]")
      }
      logger.warn(s"The projectName [${metaJsonInfo.getProjectName}] is not matching the project, will change it to [${projectName}] automatically")
      metaJsonInfo.setProjectName(projectName)

    }
    val validateResult = validateJobDeploy(metaJsonInfo.getProjectName, metaJsonInfo.getJobName, userName)
    //  生成StreamJob，根据StreamJob生成StreamJobVersion
    val version = deployStreamJob(validateResult.streamJob, metaJsonInfo, userName, validateResult.updateVersion, source)
    // Save the job configuration, lock the job again if exists
    if (null != metaJsonInfo.getJobConfig){
      this.streamJobConfService.saveJobConfig(version.getJobId, metaJsonInfo.getJobConfig.asInstanceOf[util.Map[String, AnyRef]])
    }
    //  上传所有非meta.json的文件
    uploadFiles(metaJsonInfo, version, inputZipPath)
    version
  }

  @throws(classOf[ErrorException])
  @Transactional(rollbackFor = Array(classOf[Exception]))
  override def createOrUpdate(userName: String, metaJsonInfo: MetaJsonInfo): StreamJobVersion = {
    val validateResult = validateJobDeploy(metaJsonInfo.getProjectName, metaJsonInfo.getJobName, userName)
    val readerUtils = new ReaderUtils
    metaJsonInfo.setMetaInfo(readerUtils.readAsJson(metaJsonInfo))
    val version = deployStreamJob(validateResult.streamJob, metaJsonInfo, userName, validateResult.updateVersion)
    // Save the job configuration, lock the job again if exists
    if (null != metaJsonInfo.getJobConfig){
      this.streamJobConfService.saveJobConfig(version.getJobId, metaJsonInfo.getJobConfig.asInstanceOf[util.Map[String, AnyRef]])
    }
    version
  }

  override def getJobContent(jobId: Long, version: String): StreamisTransformJobContent = {
    val job = streamJobMapper.getJobById(jobId)
    if(job == null) throw new JobFetchErrorException(30030, s"job is not exists.")
    val jobVersion = if(StringUtils.isBlank(version)) {
      streamJobMapper.getJobVersions(jobId).get(0)
    } else streamJobMapper.getJobVersionById(jobId, version)
    if(jobVersion == null)
      throw new JobFetchErrorException(30030, s"job has no versions.")
//    val highAvailablePolicy = streamJobConfService.getJobConfValue(jobId, "wds.streamis.app.highavailable.policy")
//    val highAvailableVo = SourceUtils.manageJobProjectFile(highAvailablePolicy, jobVersion.getSource)
//    jobVersion.setSource(highAvailableVo.toString)
    jobContentParsers.find(_.canParse(job, jobVersion)).map(_.parseTo(job, jobVersion))
      .getOrElse(throw new JobFetchErrorException(30030, s"Cannot find a JobContentParser to parse jobContent."))
  }

  override def updateArgs(jobId: Long, version: String,args: util.List[String],isHighAvailable: Boolean, highAvailableMessage: String): StreamisTransformJobContent = {
    val jobVersion =streamJobMapper.getLatestJobVersion(jobId)
    val checkContent = getJobContent(jobId, jobVersion.getVersion)
    val streamJob = streamJobMapper.queryJobById(jobId)
    val jobContent = jobVersion.getJobContent
    if (args != null){
      val newJobContent = JsonUtils.manageArgs(jobContent,args)
      val streamJobVersion = new StreamJobVersion
      streamJobVersion.setJobId(jobVersion.getJobId)
      streamJobVersion.setCreateBy(jobVersion.getCreateBy)
      streamJobVersion.setManageMode(jobVersion.getManageMode)
      streamJobVersion.setCreateTime(new Date())
      streamJobVersion.setJobContent(newJobContent)
      streamJobVersion.setSource("update args")
      streamJobVersion.setVersion(rollingJobVersion(jobVersion.getVersion))
      streamJobVersion.setComment("用户"+ jobVersion.getCreateBy + "修改args")
      streamJobMapper.insertJobVersion(streamJobVersion)
      val oldVersionFiles: util.List[StreamJobVersionFiles] = streamJobMapper.getStreamJobVersionFiles(jobId, jobVersion.getId)
      oldVersionFiles.asScala.foreach { version =>
        val jobVersionFiles = new StreamJobVersionFiles
        jobVersionFiles.setJobId(version.getJobId)
        jobVersionFiles.setJobVersionId(streamJobVersion.getId)
        jobVersionFiles.setCreateBy(version.getCreateBy)
        jobVersionFiles.setVersion(streamJobVersion.getVersion)
        jobVersionFiles.setFileName(version.getFileName)
        jobVersionFiles.setCreateTime(new Date(System.currentTimeMillis()))
        jobVersionFiles.setStorePath(version.getStorePath)
        streamJobMapper.insertJobVersionFiles(jobVersionFiles)
      }
      val task = streamTaskMapper.getLatestByJobId(streamJob.getId)
      if (task != null && !JobConf.isCompleted(task.getStatus)) {
        logger.warn(s"StreamJob-${streamJob.getName} is in status ${task.getStatus}, your deployment will not update the version in job")
      } else {
        streamJob.setCurrentVersion(rollingJobVersion(jobVersion.getVersion))
        streamJobMapper.updateJob(streamJob)
      }
      getJobContent(jobId,streamJobVersion.getVersion)
    } else {
      var source = jobVersion.getSource
      if (source == null){
        source = JsonUtils.manageSource(JobConstrants.DEFAULT_JSON, isHighAvailable, highAvailableMessage)
      }
      else {
        source = JsonUtils.manageSource(jobVersion.getSource, isHighAvailable, highAvailableMessage)
      }
      jobVersion.setSource(source)
      streamJobMapper.updateSource(jobVersion)
      getJobContent(jobId,jobVersion.getVersion)
    }
  }


  override def hasPermission(jobId: Long, username: String): Boolean = {
    hasPermission(this.streamJobMapper.getJobById(jobId), username)
  }

  override def hasPermission(job: StreamJob, username: String): Boolean = {
    Option(job) match {
      case Some(job: StreamJob) =>
        if (!username.equals(job.getCreateBy)){
          Option(this.streamJobConfService.getJobConfValue(job.getId,
            JobConfKeyConstants.AUTHORITY_AUTHOR_VISIBLE.getValue)) match {
            case Some(authors) =>
              authors.split(",").toList.contains(username)
            case _ => false
          }
        } else true
      case None => false
    }
  }

  override def getAlertUsers(job: StreamJob): util.List[String] = {
    val alertUsers = this.streamJobConfService.getJobConfValue(job.getId, JobConfKeyConstants.ALERT_USER.getValue)
    if (StringUtils.isBlank(alertUsers)) return null
    alertUsers.split(",").toList.asJava
  }

  override def getAlertLevel(job: StreamJob): AlertLevel = {
    val level = this.streamJobConfService.getJobConfValue(job.getId, JobConfKeyConstants.ALERT_LEVEL.getValue)
    if (StringUtils.isBlank(level)) return AlertLevel.MINOR
    AlertLevel.valueOf(level)
  }

  override def getLinkisFlinkAlertLevel(job: StreamJob): AlertLevel = {
    val level = this.streamJobConfService.getJobConfValue(job.getId, JobConfKeyConstants.ALERT_LEVEL.getValue)
    if (StringUtils.isBlank(level)) return AlertLevel.MAJOR
    AlertLevel.valueOf(level)
  }

  override def isCreator(jobId: Long, username: String): Boolean = {
    val job = streamJobMapper.getJobById(jobId)
    if (job == null) return false
    username.equals(job.getCreateBy)
  }

  override def getAlert(username: String, jobId: Long, version: String): util.List[StreamAlertRecord] = {
    val job = streamJobMapper.getJobVersionById(jobId, version)
    if (job == null) return null
    streamAlertMapper.getAlertByJobIdAndVersion(username,jobId,job.getId)
  }

  private def validateJobDeploy(projectName: String, jobName: String, userName: String): JobDeployValidateResult = {
    if(StringUtils.isBlank(jobName)) throw new JobCreateErrorException(30030, s"jobName is needed.")
    if(StringUtils.isBlank(projectName)) throw new JobCreateErrorException(30030, s"projectName is needed.")
    // Try to lock the stream job to create version
    Option(streamJobMapper.queryAndLockJobInCondition(projectName, jobName)) match {
      case Some(streamJob) =>
        var updateVersion = true
        val task = streamTaskMapper.getLatestByJobId(streamJob.getId)
        if (task != null && !JobConf.isCompleted(task.getStatus)) {
          logger.warn(s"StreamJob-$jobName is in status ${task.getStatus}, your deployment will not update the version in job")
          updateVersion = false
        }
        JobDeployValidateResult(streamJob, updateVersion)
      case _ =>
        JobDeployValidateResult(null, updateVersion = true)
    }

  }

  @throws(classOf[ErrorException])
  @Transactional(rollbackFor = Array(classOf[Exception]))
  override def updateLabel(streamJob: StreamJob): Unit = streamJobMapper.updateJob(streamJob)

  override def canBeDisabled(jobId: Long): Boolean = {
    val streamJob = streamJobMapper.getJobById(jobId);
    if(streamJob == null) throw new JobFetchErrorException(30030, s"job does not exist.")
    //处理job是首次上传且未被启动的情况
    if (streamJob.getStatus == 0){
      val streamTask = this.streamTaskMapper.getLatestByJobId(jobId)
      if (streamTask != null) {
        val statusCode = streamTask.getStatus
        val status = JobConf.getStatusString(statusCode)
        if (!JobConf.isFinished(statusCode)) {
          logger.warn(s"StreamJob-${jobId} is in status ${status}, the job has not completed, can not be disabled")
          return false
        }
      }
    }
    if (!streamJob.getEnable) {
      logger.warn(s"StreamJob-${jobId} has been disabled, could not be disabled again")
      false
    } else {
      true
    }
  }


  override def disableJob(streamJob: StreamJob): Unit = {
    if(streamJob == null) throw new JobFetchErrorException(30030, s"job does not exist.")
    streamJob.setEnable(false)
    streamJobMapper.updateJobEnable(streamJob)
  }

  override def canbeActivated(jobId: Long): Boolean = {
    val streamJob = streamJobMapper.getJobById(jobId)
    if(streamJob == null) throw new JobFetchErrorException(30030, s"job does not exist.")
    if (streamJob.getEnable){
      false
    } else {
      true
    }
  }

  override def activateJob(streamJob: StreamJob): Unit = {
    streamJob.setEnable(true)
    streamJobMapper.updateJobEnable(streamJob)
  }

  override def getEnableStatus(jobId: Long): Boolean = {
    val streamJob = streamJobMapper.getJobById(jobId)
    if (streamJob.getEnable) {
      true
    } else {
      false
    }
  }

}

object DefaultStreamJobService{
  /**
   * Deploy validate result
   * @param updateVersion should update version
   */
  case class JobDeployValidateResult(streamJob: StreamJob, updateVersion: Boolean)
}
