package com.webank.wedatasphere.streamis.jobmanager.manager.transform.impl

import java.util

import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LaunchJob
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.Transform
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity.StreamisTransformJob

/**
 *
 * @date 2021-06-06
 * @author enjoyyin
 * @since 0.5.0
 */
class SourceTransform extends Transform {
  override def transform(streamisTransformJob: StreamisTransformJob, job: LaunchJob): LaunchJob = {
    val source = new util.HashMap[String, Any]
    source.put("project", streamisTransformJob.getStreamJob.getProjectId)
    source.put("workspace", streamisTransformJob.getStreamJob.getWorkspaceId)
    source.put("job", streamisTransformJob.getStreamJob.getName)
    LaunchJob.builder().setLaunchJob(job).setSource(source).build()
  }
}
