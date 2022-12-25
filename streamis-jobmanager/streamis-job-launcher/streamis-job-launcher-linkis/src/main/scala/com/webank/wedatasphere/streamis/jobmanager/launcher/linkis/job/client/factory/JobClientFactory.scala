package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client.factory

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.{JobClient, LaunchJob}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.{FlinkJobInfo, LinkisJobInfo}
import org.apache.linkis.computation.client.once.OnceJob

trait JobClientFactory {

  def createJobClient(job: LaunchJob, onceJob: OnceJob, flinkJobInfo: FlinkJobInfo, jobStateManager: JobStateManager): JobClient[LinkisJobInfo]

  /**
   * Init the factory
   */
  def init(): Unit
}
