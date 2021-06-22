package com.webank.wedatasphere.streamis.jobmanager.manager.transform.impl

import java.util

import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LaunchJob
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.StreamisCodeTransform
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity.StreamisCodeTransformJob

/**
 *
 * @date 2021-06-06
 * @author enjoyyin
 * @since 0.5.0
 */
class LabelsStreamisCodeTransform extends StreamisCodeTransform {
  override protected def transformCode(transformJob: StreamisCodeTransformJob,
                                       job: LaunchJob): LaunchJob = {
    val labels = new util.HashMap[String, Any]
    labels.put("engineType", transformJob.getEngineConnType)
    labels.put("userCreator", transformJob.getStreamJob.getSubmitUser + "-Streamis")
    labels.put("engineConnMode", "once")
    if(job.getLabels != null) labels.putAll(job.getLabels)
    LaunchJob.builder().setLaunchJob(job).setLabels(labels).build()
  }
}
