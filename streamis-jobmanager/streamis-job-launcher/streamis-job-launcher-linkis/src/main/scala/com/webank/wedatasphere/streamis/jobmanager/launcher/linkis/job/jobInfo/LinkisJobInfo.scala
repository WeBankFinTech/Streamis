package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.jobInfo

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.JobInfo
import org.apache.linkis.common.ServiceInstance

trait LinkisJobInfo extends JobInfo {

  /**
   * Fetch engine conn manager instance info
   *
   * @return
   */
  def getECMInstance: ServiceInstance

  /**
   * Job log directory suffix
   *
   * @return
   */
  def getLogDirSuffix: String

  def setLogDirSuffix(logDirSuffix: String): Unit
}
