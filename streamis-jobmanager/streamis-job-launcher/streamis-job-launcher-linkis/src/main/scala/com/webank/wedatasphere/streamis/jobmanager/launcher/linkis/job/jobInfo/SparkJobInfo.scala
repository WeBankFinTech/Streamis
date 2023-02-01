package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.jobInfo

import com.webank.wedatasphere.streamis.jobmanager.entrypoint.message.JobHeartbeatMessage
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.`type`.JobClientType
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobStateInfo

import java.util

class SparkJobInfo extends RestJobInfo {
  private var id: String = _
  private var name: String = _
  private var user: String = _
  private var status: String = _
  private var logPath: String = _
  private var resources: java.util.Map[String, Object] = _
  private var completedMsg: String = _
  private var jobStates: Array[JobStateInfo] = _
  private var engineType: String = "spark"
  private var clientType: JobClientType.Value = JobClientType.DETACH

  private var jobHeartbeatMessage: JobHeartbeatMessage = _

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

  override def getCompletedMsg: String = completedMsg

  def setCompletedMsg(completedMsg: String): Unit = this.completedMsg = completedMsg

  override def toString: String = s"FlinkJobInfo(id: $id, status: $status, logPath: $logPath)"

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
  override def getClientType: String = clientType.toString

  def setClientType(clientType: String): Unit = {
    this.clientType = JobClientType.withName(clientType)
  }
  /**
   * Engine version
   *
   * @return
   */
  override def getEngineVersion: String = "1.12.2"

  def setEngineVersion(version: String): Unit = {

  }

  /**
   * Rest job heartbeat message
   *
   * @return
   */
  override def getMessage: JobHeartbeatMessage = this.jobHeartbeatMessage

  override def setMessage(message: JobHeartbeatMessage): Unit = {
    this.jobHeartbeatMessage = message
  }
}
