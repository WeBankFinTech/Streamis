package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.jobInfo

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.JobInfo

trait YarnJobInfo extends JobInfo {

  def getApplicationId: String

  def getApplicationUrl: String

}
