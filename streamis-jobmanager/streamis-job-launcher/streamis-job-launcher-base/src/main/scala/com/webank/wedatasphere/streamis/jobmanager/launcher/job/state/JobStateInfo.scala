package com.webank.wedatasphere.streamis.jobmanager.launcher.job.state

import org.apache.linkis.common.utils.JsonUtils

/**
 * Basic info
 */
class JobStateInfo {
  /**
   * Location
   */
  private var location: String = _

  /**
   * Timestamp
   */
  private var timestamp: Long = -1

  /**
   * Mark if the state is restored
   */
  private var restore: Boolean = false

  def this(location: String, timestamp: Long, restore: Boolean) {
    this()
    this.location = location
    this.timestamp = timestamp
    this.restore = restore
  }

  def setLocation(location: String): Unit = {
    this.location = location
  }

  def getLocation: String = {
    this.location
  }

  def setTimestamp(timestamp: Long): Unit = {
    this.timestamp = timestamp
  }

  def getTimestamp: Long = {
    timestamp
  }

  def isRestore: Boolean = this.restore

  def setRestore(restore: Boolean): Unit  = {
    this.restore = restore
  }
}
