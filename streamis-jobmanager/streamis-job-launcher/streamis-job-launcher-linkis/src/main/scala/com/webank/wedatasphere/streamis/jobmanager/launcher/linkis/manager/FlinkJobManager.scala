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

  protected def buildOnceJob(job: LaunchJob): SubmittableOnceJob

  protected def createSubmittedOnceJob(id: String, user: String): OnceJob

  protected def getOnceJob(id: String, user: String): OnceJob = null

  override def launch(job: LaunchJob): String = {
    val onceJob = buildOnceJob(job)
    onceJob.submit()
    onceJobs synchronized onceJobs.put(onceJob.getId, onceJob)
    onceJob.getId
  }

  override def stop(id: String, user: String): Unit = getOnceJob(id, user).kill()

  def getCheckpoints(id: String): LinkisJobInfo

  def triggerSavepoint(id: String): LinkisJobInfo

}
