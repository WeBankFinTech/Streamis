package com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobState
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.{JobInfo, LaunchJob}

import java.util.concurrent.ConcurrentHashMap

/**
  * Basic job manager interface for launching job
 */
trait JobLaunchManager[T <: JobInfo] {

  /**
   * Manager name
   * @return
   */
  def getName: String

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
  def connect(id: String, jobInfo: String): T

  def connect(id: String, jobInfo: T): Unit
  /**
   * Job state manager(store the state information, example: Checkpoint/Savepoint)
   * @return state manager instance
   */
  def getJobStateManager: JobStateManager

  /**
   * Stop a launched job by id, You should use [[launch()]] method before use this method.
   * @param id job id
   * @param snapshot if do snapshot to save the job state
   */
  def stop(id: String, snapshot: Boolean): Unit

  def stop(id: String): Unit
  /**
   * get jo info of a launched Job by JobId.
   * You should use [[launch()]] method before use this method.
   * @param id launched job id
   */
  def getJobInfo(id: String): T
}
object JobLaunchManager{

  /**
   * Store the job launch managers
   */
  private val launchManagers = new ConcurrentHashMap[String, JobLaunchManager[_ <: JobInfo]]()

  def registerJobManager(name: String, jobLaunchManager: JobLaunchManager[_ <: JobInfo]): Unit = {
    launchManagers.put(name, jobLaunchManager)
  }

  def getJobManager(name: String): JobLaunchManager[_ <: JobInfo] = {
    launchManagers.get(name)
  }
}