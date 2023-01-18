package com.webank.wedatasphere.streamis.jobmanager.launcher.job.state

import java.net.URI

/**
 * Generic job state
 * @param location location
 */
class JobGenericState(location: String) extends JobState {

  /**
   * Restore flag
   */
  private var restore: Boolean = false

  private var timestamp: Long = -1

  private var id: String = "{ID}"

  private var metadataInfo: Any = _

  override def getLocation: URI = URI.create(location)

  override def getMetadataInfo: Any = {
    metadataInfo
  }

  def setMetadataInfo(metadataInfo: Any): Unit = {
    this.metadataInfo = metadataInfo
  }

  /**
   * Job state id
   *
   * @return
   */
  override def getId: String = id

  def setId(id: String): Unit = {
    this.id = id
  }
  /**
   * Timestamp to save the state
   *
   * @return
   */
  override def getTimestamp: Long = timestamp

  def setTimestamp(timestamp: Long): Unit = {
    this.timestamp = timestamp
  }

  /**
   * If need to restore
   * @return
   */
  override def isRestore: Boolean = this.restore

  override def setToRestore(restore: Boolean): Unit = {
    this.restore = restore
  }
}
