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

package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.manager

import java.util

import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.LinkisJobManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.core.FlinkLogIterator
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.{LaunchJob, LinkisJobInfo, LogRequestPayload}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.FlinkJobLaunchErrorException
import org.apache.linkis.common.utils.{Logging, Utils}
import org.apache.linkis.computation.client.once.{OnceJob, SubmittableOnceJob}
import org.apache.linkis.computation.client.utils.LabelKeyUtils


trait FlinkJobManager extends LinkisJobManager with Logging {

  protected val onceJobs = new util.HashMap[String, OnceJob]
  protected val onceJobIdToJobInfo = new util.HashMap[String, LinkisJobInfo]

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

  override def launch(job: LaunchJob): String = {
    job.getLabels.get(LabelKeyUtils.ENGINE_TYPE_LABEL_KEY) match {
      case engineConnType: String =>
        if(!engineConnType.toLowerCase.startsWith(FlinkJobManager.FLINK_ENGINE_CONN_TYPE))
          throw new FlinkJobLaunchErrorException(30401, s"Only ${FlinkJobManager.FLINK_ENGINE_CONN_TYPE} job is supported to be launched to Linkis, but $engineConnType is found.")
      case _ => throw new FlinkJobLaunchErrorException(30401, s"Not exists ${LabelKeyUtils.ENGINE_TYPE_LABEL_KEY}, StreamisJob cannot be submitted to Linkis successfully.")
    }
    val onceJob = buildOnceJob(job)
    onceJob.submit()
    onceJobs synchronized onceJobs.put(onceJob.getId, onceJob)
    val linkisJobInfo = Utils.tryCatch(createJobInfo(onceJob, job)){ t =>
      error(s"${job.getSubmitUser} create jobInfo failed, now stop this EngineConn ${onceJob.getId}.")
      stop(onceJob)
      throw t
    }
    onceJobs synchronized onceJobIdToJobInfo.put(onceJob.getId, linkisJobInfo)
    onceJob.getId
  }


  override def launch(id: String, jobInfo: String): Unit = launchExistedJob(id, createJobInfo(jobInfo))

  override def launch(id: String, jobInfo: LinkisJobInfo): Unit = launchExistedJob(id, jobInfo)

  private def launchExistedJob(id: String, getJobInfo: => LinkisJobInfo): Unit = if(!onceJobIdToJobInfo.containsKey(id)) {
    onceJobs synchronized {
      if(!onceJobIdToJobInfo.containsKey(id)) {
        onceJobIdToJobInfo.put(id, getJobInfo)
      }
    }
  }

  override def isExists(id: String): Boolean = onceJobIdToJobInfo.containsKey(id)

  override def getJobInfo(id: String): LinkisJobInfo = {
    if (onceJobIdToJobInfo.containsKey(id)) {
      val jobInfo = onceJobIdToJobInfo.get(id)
      jobInfo.setStatus(getStatus(id))
      jobInfo
    } else throw new FlinkJobLaunchErrorException(30402, "LinkisJobInfo is not exists, please launch it first.")
  }

  protected def getStatus(id: String): String

  override def stop(id: String): Unit = stop(getOnceJob(id))

  def stop(onceJob: OnceJob): Unit = {
    onceJob.kill()
    deleteOnceJob(onceJob.getId)
  }

  protected def deleteOnceJob(id: String): Unit = onceJobs synchronized {
    onceJobs.remove(id)
    onceJobIdToJobInfo.remove(id)
  }

  def fetchLogs(id: String, requestPayload: LogRequestPayload): FlinkLogIterator

  def getCheckpoints(id: String): LinkisJobInfo

  def triggerSavepoint(id: String): LinkisJobInfo

}
object FlinkJobManager {
  val FLINK_ENGINE_CONN_TYPE = "flink"
}