package com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobState
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.{JobInfo, LaunchJob}

/**
  * Basic job manager interface for launching job
 */
trait JobLaunchManager {


  def launch(job: LaunchJob): String

  /**
   * This method is used to launch a new job.
   * @param job a StreamisJob wanted to be launched.
   * @param jobState job state used to launch
   * @return the job id.
   */
  def launch(job: LaunchJob, jobState: JobState): String
  /**
   * Connect the job which already launched in another process,
   * if the job has been stored in process, just return the job info
   * @param id id
   * @param jobInfo job info
   * @return
   */
  def connect(id: String, jobInfo: String): JobInfo

  def connect(id: String, jobInfo: JobInfo): Unit
  /**
   * Job state manager(store the state information, example: Checkpoint/Savepoint)
   * @return state manager instance
   */
  def getJobStateManager: JobStateManager

  /**
   * Stop a launched job by id, You should use [[launch()]] method before use this method.
   * @param snapshot if do snapshot to save the job state
   */
  def stop(snapshot: Boolean): Unit

  /**
   * get jo info of a launched Job by JobId.
   * You should use [[launch()]] method before use this method.
   * @param id launched job id
   */
  def getJobInfo(id: String): JobInfo
}
