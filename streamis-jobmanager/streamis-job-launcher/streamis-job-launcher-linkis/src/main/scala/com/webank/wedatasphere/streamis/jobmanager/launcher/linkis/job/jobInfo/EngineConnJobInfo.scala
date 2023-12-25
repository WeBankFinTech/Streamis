package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.jobInfo

import com.fasterxml.jackson.annotation.JsonIgnore
import com.webank.wedatasphere.streamis.jobmanager.launcher.enums.JobClientType
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobStateInfo
import org.apache.linkis.common.ServiceInstance

import java.util

class EngineConnJobInfo extends YarnJobInfo with LinkisJobInfo {
  private var id: String = _
  private var name: String = _
  private var user: String = _
  private var status: String = _
  private var logPath: String = _
  private var resources: java.util.Map[String, Object] = _
  private var completedMsg: String = _
  private var jobStates: Array[JobStateInfo] = _
  private var engineType: String = "flink"
  private var engineVersion: String = "1.12.2"
  private var clientType: String = JobClientType.ATTACH.toString

  private var savepoint: String = _
  private var checkpoint: String = _
  private var applicationId: String = _
  private var applicationUrl: String = _

  private var ecmInstance: ServiceInstance = _
  private var logDirSuffix: String = _
  @JsonIgnore
  private var jobParams: java.util.Map[String, Object] = _
  private var ecInstance: ServiceInstance = _
  @JsonIgnore
  private var highAvailablePolicy: String = _

  override def getApplicationId: String = applicationId

  def setApplicationId(applicationId: String): Unit = this.applicationId = applicationId

  override def getApplicationUrl: String = applicationUrl

  def setApplicationUrl(applicationUrl: String): Unit = this.applicationUrl = applicationUrl

  override def getId: String = id

  def setId(id: String): Unit = this.id = id

  override def getECMInstance: ServiceInstance = ecmInstance

  def setECMInstance(ecmInstance: ServiceInstance): Unit = this.ecmInstance = ecmInstance

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

  override def toString: String = s"FlinkJobInfo(id: $id, name: $name, status: $status, applicationId: $applicationId, applicationUrl: $applicationUrl, logPath: $logPath)"

  /**
   * Contains the check point and save points
   *
   * @return
   */
  override def getJobStates: Array[JobStateInfo] = {
    jobStates
  }

  def setJobStates(jobStates: Array[JobStateInfo]): Unit = {
    this.jobStates = jobStates
  }

  /**
   * Job name
   *
   * @return name
   */
  override def getName: String = name

  def setName(name: String): Unit = {
    this.name = name
  }

  /**
   * Job log directory suffix
   *
   * @return
   */
  override def getLogDirSuffix: String = this.logDirSuffix

  override def setLogDirSuffix(logDirSuffix: String): Unit = {
    this.logDirSuffix = logDirSuffix
  }

  /**
   * Engine type
   *
   * @return
   */
  override def getEngineType: String = engineType

  def setEngineType(engineType: String): Unit = {
    this.engineType = engineType
  }
  /**
   * Client type
   *
   * @return
   */
  override def getClientType: String = clientType

  def setClientType(clientType: String): Unit = {
    this.clientType = clientType
  }
  /**
   * Engine version
   *
   * @return
   */
  override def getEngineVersion: String = engineVersion

  def setEngineVersion(version: String): Unit = {
    this.engineVersion = version
  }

  def getJobParams(): util.Map[String, Object] = jobParams

  def setJobParams(params: util.Map[String, Object]): EngineConnJobInfo = {
    this.jobParams = params
    this
  }

  def getEcInstance(): ServiceInstance = ecInstance

  def setEcInstance(instance: ServiceInstance): EngineConnJobInfo = {
    this.ecInstance = instance
    this
  }

  override def getHighAvailablePolicy(): String = highAvailablePolicy

  def setHighAvailablePolicy(highAvailablePolicy: String): Unit = {
    this.highAvailablePolicy = highAvailablePolicy
  }
}
