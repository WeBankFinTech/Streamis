package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.manager

import java.util

import com.webank.wedatasphere.linkis.computation.client.once.{OnceJob, SubmittableOnceJob}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.LinkisJobManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.{LaunchJob, LinkisJobInfo}

/**
 *
 * @date 2021-06-05
 * @author enjoyyin
 * @since 0.5.0
 */
trait FlinkJobManager extends LinkisJobManager {

  protected val onceJobs = new util.HashMap[String, OnceJob]
  protected val onceJobIdToJobInfo = new util.HashMap[String, LinkisJobInfo]

  protected def buildOnceJob(job: LaunchJob): SubmittableOnceJob

  protected def createSubmittedOnceJob(id: String, user: String): OnceJob

  protected def getOnceJob(id: String, user: String): OnceJob = {
    if(onceJobs.containsKey(id)) return onceJobs.get(id)
    onceJobs synchronized {
      if(!onceJobs.containsKey(id)) {
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
    val linkisJobInfo = createJobInfo(onceJob.getId, job.getSubmitUser)
    onceJobs synchronized {
      onceJobs.put(onceJob.getId, onceJob)
      onceJobIdToJobInfo.put(onceJob.getId, linkisJobInfo)
    }
    onceJob.getId
  }

  override def getJobInfo(id: String, user: String): LinkisJobInfo = {
    val jobInfo = if(onceJobIdToJobInfo.containsKey(id)) onceJobIdToJobInfo.get(id)
    else onceJobs synchronized {
      if(!onceJobIdToJobInfo.containsKey(id)) onceJobIdToJobInfo.put(id, createJobInfo(id, user))
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

  def getCheckpoints(id: String, user: String): LinkisJobInfo

  def triggerSavepoint(id: String, user: String): LinkisJobInfo

}
