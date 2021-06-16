package com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity
import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.vo.ConfigKeyVO
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.{StreamJob, StreamJobVersion}

/**
 *
 * @date 2021-06-06
 * @author enjoyyin
 * @since 0.5.0
 */
class StreamisTransformJobImpl extends StreamisTransformJob {

  private var streamJob: StreamJob = _
  private var streamJobVersion: StreamJobVersion = _
  private var config: ConfigKeyVO = _

  override def getStreamJob: StreamJob = streamJob
  def setStreamJob(streamJob: StreamJob): Unit = this.streamJob = streamJob

  override def getStreamJobVersion: StreamJobVersion = streamJobVersion
  def setStreamJobVersion(streamJobVersion: StreamJobVersion): Unit = this.streamJobVersion = streamJobVersion

  override def getConfig: ConfigKeyVO = config
  def setConfig(config: ConfigKeyVO): Unit = this.config = config

}
