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
import org.apache.linkis.common.utils.{Logging, Utils}
import org.apache.linkis.computation.client.once.{OnceJob, SubmittableOnceJob}


trait FlinkJobManager extends LinkisJobManager with Logging {

  protected val onceJobs = new util.HashMap[String, OnceJob]
  protected val onceJobIdToJobInfo = new util.HashMap[String, LinkisJobInfo]

  protected def buildOnceJob(job: LaunchJob): SubmittableOnceJob

  protected def createSubmittedOnceJob(id: String, user: String): OnceJob

  protected def getOnceJob(id: String, user: String): OnceJob = {
    if (onceJobs.containsKey(id)) return onceJobs.get(id)
    onceJobs synchronized {
      if (!onceJobs.containsKey(id)) {
        val onceJob = createSubmittedOnceJob(id, user)
        onceJobs.put(id, onceJob)
      }
    }
    onceJobs.get(id)
  }

  def createJobInfo(id: String, user: String): LinkisJobInfo

  override def launch(job: LaunchJob): String = {
    val onceJob = buildOnceJob(job)
    onceJob.submit()
    onceJobs synchronized onceJobs.put(onceJob.getId, onceJob)
    val linkisJobInfo = Utils.tryCatch(createJobInfo(onceJob.getId, job.getSubmitUser)){ t =>
      error(s"${job.getSubmitUser} create jobInfo failed, now stop this EngineConn ${onceJob.getId}.")
      stop(onceJob.getId, job.getSubmitUser)
      throw t
    }
    onceJobs synchronized onceJobIdToJobInfo.put(onceJob.getId, linkisJobInfo)
    onceJob.getId
  }

  override def getJobInfo(id: String, user: String): LinkisJobInfo = {
    val jobInfo = if (onceJobIdToJobInfo.containsKey(id)) onceJobIdToJobInfo.get(id)
    else onceJobs synchronized {
      if (!onceJobIdToJobInfo.containsKey(id)) onceJobIdToJobInfo.put(id, createJobInfo(id, user))
      onceJobIdToJobInfo.get(id)
    }
    jobInfo.setStatus(getStatus(id, user))
    jobInfo
  }

  protected def getStatus(id: String, user: String): String

  override def stop(id: String, user: String): Unit = {
    getOnceJob(id, user).kill()
    deleteOnceJob(id)
  }

  def deleteOnceJob(id: String): Unit = onceJobs synchronized {
    onceJobs.remove(id)
    onceJobIdToJobInfo.remove(id)
  }

  def fetchLogs(id: String, user: String, requestPayload: LogRequestPayload): FlinkLogIterator

  def getCheckpoints(id: String, user: String): LinkisJobInfo

  def triggerSavepoint(id: String, user: String): LinkisJobInfo

}
