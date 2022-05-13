package com.webank.wedatasphere.streamis.jobmanager.launcher.job.state

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.JobInfo

trait JobStateFetcher[T <: JobState] {
  /**
   * Get state information
   * @param jobInfo JobInfo
   * @return
   */
    def getState(jobInfo: JobInfo): T
}
