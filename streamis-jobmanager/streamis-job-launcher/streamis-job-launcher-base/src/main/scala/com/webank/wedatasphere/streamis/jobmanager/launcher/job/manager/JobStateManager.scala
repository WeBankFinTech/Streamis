package com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager
import java.util
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.JobInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.{JobState, JobStateFetcher}

trait JobStateManager {

  def getOrCreateJobStateFetcher[T <: JobState](clazz: Class[T]): JobStateFetcher[T];

  def getJobState[T <: JobState](jobInfo: JobInfo): T


}


