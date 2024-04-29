package com.webank.wedatasphere.streamis.jobmanager.manager.service

import com.github.pagehelper.PageInfo
import com.webank.wedatasphere.streamis.jobmanager.manager.alert.AlertLevel
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.{JobTemplateFiles, MetaJsonInfo, StreamAlertRecord, StreamJob, StreamJobVersion, StreamJobVersionFiles}
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.{JobStatusVo, QueryJobListVo, TaskCoreNumVo, VersionDetailVo}
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity.StreamisTransformJobContent

import java.util

/**
 * Job service
 */
trait StreamJobService {


  def getJobById(jobId: Long): StreamJob

  def getLatestJobVersion(jobId :Long): StreamJobVersion

  def getJobByName(jobName: String): util.List[StreamJob]

  /**
   * Page list query
   *
   * @param projectName project name
   * @param jobName     job name
   * @param jobStatus   job status
   * @param jobCreator  job creator
   * @return
   */
  def getByProList(projectName: String, userName: String, jobName: String, jobStatus: Integer, jobCreator: String, label: String, enable: java.lang.Boolean = null,jobType :String): PageInfo[QueryJobListVo]

  /**
   * Page list query of version info
   *
   * @param jobId job id
   * @return
   */
  def getVersionList(jobId: Long): PageInfo[VersionDetailVo]

  /**
   * Count core norm
   *
   * @param projectName project name
   * @return
   */
  def countByCores(projectName: String, userName: String): TaskCoreNumVo

  /**
   * Version detail information
   *
   * @param jobId   job id
   * @param version version
   */
  def versionDetail(jobId: Long, version: String): VersionDetailVo

  /**
   * Rolling job version
   *
   * @param preVersion version
   */
  def rollingJobVersion(preVersion: String): String

  /**
   * Upload files
   *
   * @param metaJsonInfo meta json
   * @param version      version
   * @param path         path
   */
  def uploadFiles(metaJsonInfo: MetaJsonInfo, version: StreamJobVersion, path: String): Unit

  /**
   * Deploy stream job
   *
   * @param metaJsonInfo  meta json
   * @param userName      username
   * @param updateVersion should update version
   * @return
   */
  def deployStreamJob(streamJob: StreamJob, metaJsonInfo: MetaJsonInfo, userName: String, updateVersion: Boolean,source: String = null): StreamJobVersion

  /**
   * Upload job
   *
   * @param projectName  project name
   * @param userName     username
   * @param inputZipPath input zip path
   * @return
   */
  def uploadJob(projectName: String, userName: String, inputZipPath: String, source: String): StreamJobVersion

  /**
   * Create or update job with meta json
   *
   * @param userName     username
   * @param metaJsonInfo meta json
   * @return
   */
  def createOrUpdate(userName: String, metaJsonInfo: MetaJsonInfo): StreamJobVersion

  /**
   * Get job content
   *
   * @param jobId   job id
   * @param version version
   * @return
   */
  def getJobContent(jobId: Long, version: String): StreamisTransformJobContent

  /**
   *
   * @param jobId
   * @param version
   * @return
   */
  def updateArgs(jobId: Long, version: String, args: util.List[String], isHighAvailable: Boolean, highAvailableMessage: String): StreamisTransformJobContent

  /**
   * Has permission
   *
   * @param jobId    job id
   * @param username username
   * @return
   */
  def hasPermission(jobId: Long, username: String): Boolean

  def hasPermission(job: StreamJob, username: String): Boolean

  /**
   * Alert user
   *
   * @param job stream job
   * @return
   */
  def getAlertUsers(job: StreamJob): util.List[String]

  /**
   * Alert level
   *
   * @param job stream job
   * @return
   */
  def getAlertLevel(job: StreamJob): AlertLevel

  /**
   * Is creator
   *
   * @param jobId    job id
   * @param username username
   * @return
   */
  def isCreator(jobId: Long, username: String): Boolean

  /**
   * List alert message list
   *
   * @param username username
   * @param jobId    job id
   * @param version  version
   * @return
   */
  def getJobVersionById(username: String, jobId: Long, version: String): StreamJobVersion

  def getAlertByProList(username: String, jobId: Long,  versionId: Long): PageInfo[StreamAlertRecord]


  def updateLabel(streamJob: StreamJob): Unit

  def getLinkisFlinkAlertLevel(job: StreamJob): AlertLevel

  def canBeDisabled(jobId: Long): Boolean

  def disableJob(streamJob: StreamJob): Unit

  def canbeActivated(jobId: Long): Boolean

  def activateJob(streamJob: StreamJob): Unit

  def getEnableStatus(jobId: Long): Boolean

  def getJobFileById(id: Long): StreamJobVersionFiles

  def getLatestJobTemplate(projectName: String): String

  def getJobTemplateConfMap(streamJob: StreamJob): util.Map[String, AnyRef]

  def mergeJobConfig(destination: util.Map[String, AnyRef], source: util.Map[String, AnyRef]): Unit
}
