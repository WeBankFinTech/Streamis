package com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity

import com.webank.wedatasphere.linkis.manager.label.entity.engine.RunType._
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJobSqlResource


/**
 *
 * @date 2021-06-06
 * @author enjoyyin
 * @since 0.5.0
 */
trait StreamisCodeTransformJob extends StreamisTransformJob {

  def getRunType: RunType

  def getEngineConnType: String

}

class StreamisSqlTransformJob extends StreamisTransformJobImpl with StreamisCodeTransformJob {

  private var engineConnType: String = _
  private var streamJobSqlResource: StreamJobSqlResource = _

  override def getRunType: RunType = SQL

  override def getEngineConnType: String = engineConnType
  def setEngineConnType(engineConnType: String): Unit = this.engineConnType = engineConnType

  def getStreamJobSqlResource: StreamJobSqlResource = streamJobSqlResource
  def setStreamJobSqlResource(streamJobSqlResource: StreamJobSqlResource): Unit = this.streamJobSqlResource = streamJobSqlResource

}
