package com.webank.wedatasphere.streamis.jobmanager.entrypoint.message

import com.webank.wedatasphere.streamis.jobmanager.entrypoint.config.StreamJobConfig

class JobHeartbeatMessage {

  var streamJobConfig: StreamJobConfig = _

  var engineType: String = _

  var engineVersion: String = _

  def getStreamJobConfig: StreamJobConfig = this.streamJobConfig

  def setStreamJobConfig(streamJobConfig: StreamJobConfig): Unit = this.streamJobConfig = streamJobConfig

  def getEngineType: String = this.engineType

  def setEngineType(engineType: String): Unit = this.engineType = engineType

  def getEngineVersion: String = this.engineVersion

  def setEngineVersion(engineVersion: String): Unit = this.engineVersion = engineVersion


}
