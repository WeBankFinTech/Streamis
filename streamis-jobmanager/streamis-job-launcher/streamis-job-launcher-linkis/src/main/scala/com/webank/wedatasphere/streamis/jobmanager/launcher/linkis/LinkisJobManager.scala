package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis

import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.{LaunchJob, LinkisJobInfo}

/**
 *
 * @date 2021-06-05
 * @author enjoyyin
 * @since 0.5.0
 */
trait LinkisJobManager {

  def launch(job: LaunchJob): String

  def getStatus(id: String, user: String): String

  def stop(id: String, user: String): Unit

  def getJobInfo(id: String, user: String): LinkisJobInfo

}
