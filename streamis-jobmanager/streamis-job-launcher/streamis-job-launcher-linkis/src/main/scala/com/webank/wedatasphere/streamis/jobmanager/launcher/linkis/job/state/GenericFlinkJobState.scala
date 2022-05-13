package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobState

class GenericFlinkJobState extends JobState{
  override def getLocation: String = ???

  override def metadataInfo: Any = ???
}
