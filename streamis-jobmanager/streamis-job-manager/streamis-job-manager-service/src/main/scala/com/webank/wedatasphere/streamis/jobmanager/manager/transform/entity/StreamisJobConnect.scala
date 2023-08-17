package com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity

import org.apache.linkis.manager.label.entity.engine.RunType.RunType

trait StreamisJobConnect {
  /**
   * like: flink
   *
   * @return
   */
  def getRunType: RunType

  /**
   * like: 1.12.2
   *
   * @return
   */
  def getRunEngineVersion: String
}

class StreamisJobConnectImpl extends StreamisJobConnect {

  private var runEngineVersion: String = _
  private var runType: RunType = _

  def setRunType(runType: RunType): Unit = this.runType = runType
  override def getRunType: RunType = runType

  override def getRunEngineVersion: String = runEngineVersion
  def setRunEngineVersion(runEngineVersion: String): Unit = this.runEngineVersion = runEngineVersion

}