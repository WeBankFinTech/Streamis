/*
 * Copyright 2021 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.manager

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.LaunchJob
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobState
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.core.FlinkLogIterator
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LogRequestPayload
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.FlinkJobLaunchErrorException
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state.Checkpoint
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.{LinkisJobInfo, LinkisJobLaunchManager}
import org.apache.linkis.common.utils.{Logging, Utils}
import org.apache.linkis.computation.client.once.{OnceJob, SubmittableOnceJob}
import org.apache.linkis.computation.client.utils.LabelKeyUtils

import java.util


trait FlinkJobLaunchManager extends LinkisJobLaunchManager with Logging {

  //TODO use guava cache instead of hash map
  protected val onceJobs = new util.HashMap[String, OnceJob]

  protected val onceJobIdToJobInfo = new util.HashMap[String, LinkisJobInfo]

  protected var jobStateManager: JobStateManager = _

  protected def buildOnceJob(job: LaunchJob): SubmittableOnceJob

  protected def createSubmittedOnceJob(id: String): OnceJob

  protected def getOnceJob(id: String): OnceJob = {
    if (onceJobs.containsKey(id)) return onceJobs.get(id)
    onceJobs synchronized {
      if (!onceJobs.containsKey(id)) {
        val onceJob = createSubmittedOnceJob(id)
        onceJobs.put(id, onceJob)
      }
    }
    onceJobs.get(id)
  }

  protected def createJobInfo(onceJob: SubmittableOnceJob, job: LaunchJob): LinkisJobInfo

  protected def createJobInfo(jobInfo: String): LinkisJobInfo

  /**
   * This method is used to launch a new job.
   *
   * @param job      a StreamisJob wanted to be launched.
   * @param jobState job state used to launch
   * @return the job id.
   */
  override def launch(job: LaunchJob, jobState: JobState): String = {
    launch(job)
  }

  override def launch(job: LaunchJob): String = {
    job.getLabels.get(LabelKeyUtils.ENGINE_TYPE_LABEL_KEY) match {
      case engineConnType: String =>
        if(!engineConnType.toLowerCase.startsWith(FlinkJobLaunchManager.FLINK_ENGINE_CONN_TYPE))
          throw new FlinkJobLaunchErrorException(30401, s"Only ${FlinkJobLaunchManager.FLINK_ENGINE_CONN_TYPE} job is supported to be launched to Linkis, but $engineConnType is found.", null)
      case _ => throw new FlinkJobLaunchErrorException(30401, s"Not exists ${LabelKeyUtils.ENGINE_TYPE_LABEL_KEY}, StreamisJob cannot be submitted to Linkis successfully.", null)
    }
    val onceJob = buildOnceJob(job)
    onceJob.submit()
    onceJobs synchronized onceJobs.put(onceJob.getId, onceJob)
    val linkisJobInfo = Utils.tryCatch(createJobInfo(onceJob, job)){
      case e: FlinkJobLaunchErrorException =>
        throw e
      case t: Throwable =>
        error(s"${job.getSubmitUser} create jobInfo failed, now stop this EngineConn ${onceJob.getId}.")
        stop(onceJob)
        throw new FlinkJobLaunchErrorException(-1, "Fail to obtain launched job info", t)
    }
    onceJobs synchronized onceJobIdToJobInfo.put(onceJob.getId, linkisJobInfo)
    onceJob.getId
  }


  override def connect(id: String, jobInfo: String): LinkisJobInfo = launchExistedJob(id, createJobInfo(jobInfo))

  override def connect(id: String, jobInfo: LinkisJobInfo): Unit = launchExistedJob(id, jobInfo)

  private def launchExistedJob(id: String, getJobInfo: => LinkisJobInfo): LinkisJobInfo = {
    Option(onceJobIdToJobInfo.get(id)) match {
      case Some(jobInfo: LinkisJobInfo) => jobInfo
      case None =>
        onceJobs synchronized {
          onceJobIdToJobInfo.computeIfAbsent(id, new util.function.Function[String, LinkisJobInfo]{
            override def apply(t: String): LinkisJobInfo = getJobInfo
          })
        }
    }
  }

  override def isExists(id: String): Boolean = onceJobIdToJobInfo.containsKey(id)

  override def getJobInfo(id: String): LinkisJobInfo = {
    if (onceJobIdToJobInfo.containsKey(id)) {
      val jobInfo = onceJobIdToJobInfo.get(id)
      jobInfo.setStatus(getStatus(id))
      jobInfo
    } else throw new FlinkJobLaunchErrorException(30402, "LinkisJobInfo is not exists or removed, please launch/reload it first.", null)
  }

  protected def getStatus(id: String): String

  /**
   * Stop a launched job by id, You should use [[launch()]] method before use this method.
   *
   * @param id       job id
   * @param snapshot if do snapshot to save the job state
   */
  override def stop(id: String, snapshot: Boolean): Unit = {
    if (snapshot){
       // Begin to call the savepoint operator
       info(s"Trigger Savepoint operator for job [$id]")
       triggerSavepoint(getJobInfo(id))
    }
    stop(getOnceJob(id))
  }

  override def stop(id: String): Unit = stop(id, false)

  def stop(onceJob: OnceJob): Unit = {
    onceJob.kill()
    deleteOnceJob(onceJob.getId)
  }

  protected def deleteOnceJob(id: String): Unit = onceJobs synchronized {
    onceJobs.remove(id)
    onceJobIdToJobInfo.remove(id)
  }


  /**
   * Job state manager(store the state information, example: Checkpoint/Savepoint)
   *
   * @return state manager instance
   */
  override def getJobStateManager: JobStateManager = {
    Option(jobStateManager) match {
      case None =>
        this synchronized{
          // Flink job state manager
          jobStateManager = new FlinkJobStateManager
        }
        jobStateManager
      case Some(stateManager) => stateManager
    }
  }

  /**
   * Fetch logs
   * @param id id
   * @param requestPayload request payload
   * @return
   */
  def fetchLogs(id: String, requestPayload: LogRequestPayload): FlinkLogIterator

  /**
   * Get check points
   * @param jobInfo linkis job info
   * @return
   */
  def getCheckpoints(jobInfo: LinkisJobInfo): Array[Checkpoint]

  /**
   * Trigger save point operation
   * @param jobInfo linkis job info
   */
  def triggerSavepoint(jobInfo: LinkisJobInfo): Unit

}
object FlinkJobLaunchManager {
  val FLINK_ENGINE_CONN_TYPE = "flink"
}