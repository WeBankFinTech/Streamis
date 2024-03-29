package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client.factory

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.{JobClient, JobInfo}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.jobInfo.LinkisJobInfo
import org.apache.linkis.computation.client.once.OnceJob

trait JobClientFactory {

  def createJobClient(onceJob: OnceJob, jobInfo: JobInfo, jobStateManager: JobStateManager): JobClient[LinkisJobInfo]

  /**
   * Init the factory
   */
  def init(): Unit
}
