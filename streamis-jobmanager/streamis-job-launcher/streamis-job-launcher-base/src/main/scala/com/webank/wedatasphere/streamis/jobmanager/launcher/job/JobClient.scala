package com.webank.wedatasphere.streamis.jobmanager.launcher.job

/**
 * Job client
 * @tparam T job info type
 */
trait JobClient[T <: JobInfo] {

  def getJobInfo: T

  /**
   * Refresh job info and return
   * @param refresh refresh
   * @return
   */
  def getJobInfo(refresh: Boolean): T
  /**
   * Stop the job connected remote
   * @param snapshot if do snapshot to save the job state
   */
  def stop(snapshot: Boolean): Unit

  /**
   * Stop directly
   */
  def stop(): Unit


}
