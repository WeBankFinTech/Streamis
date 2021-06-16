package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity
import java.util

/**
 *
 * @date 2021-06-05
 * @author enjoyyin
 * @since 0.5.0
 */
class FlinkJobInfo extends YarnJobInfo {

  private var id: String = _
  private var user: String = _
  private var savepoint: String = _
  private var checkpoint: String = _
  private var applicationId: String = _
  private var applicationUrl: String = _
  private var status: String = _
  private var logPath: String = _
  private var resources: java.util.Map[String, Object] = _
  private var completedMsg: String = _

  override def getApplicationId: String = applicationId
  def setApplicationId(applicationId: String): Unit = this.applicationId = applicationId

  override def getApplicationUrl: String = applicationUrl
  def setApplicationUrl(applicationUrl: String): Unit = this.applicationUrl = applicationUrl

  override def getId: String = id
  def setId(id: String): Unit = this.id = id


  override def getUser: String = user
  def setUser(user: String): Unit = this.user = user

  override def getStatus: String = status
  override def setStatus(status: String): Unit = this.status = status

  override def getLogPath: String = logPath
  def setLogPath(logPath: String): Unit = this.logPath = logPath

  override def getResources: util.Map[String, Object] = resources
  def setResources(resources: java.util.Map[String, Object]): Unit = this.resources = resources

  def getSavepoint: String = savepoint
  def setSavepoint(savepoint: String): Unit = this.savepoint = savepoint

  def getCheckpoint: String = checkpoint
  def setCheckpoint(checkpoint: String): Unit = this.checkpoint = checkpoint

  override def getCompletedMsg: String = completedMsg
  def setCompletedMsg(completedMsg: String): Unit = this.completedMsg = completedMsg

  override def toString: String = s"FlinkJobInfo(id: $id, status: $status, applicationId: $applicationId, applicationUrl: $applicationUrl, logPath: $logPath)"
}
