package com.webank.wedatasphere.streamis.jobmanager.manager.transform.impl

import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LaunchJob
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.StreamisCodeTransform
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity.{StreamisCodeTransformJob, StreamisSqlTransformJob}

import java.util

/**
 *
 * @date 2021-06-06
 * @author enjoyyin
 * @since 0.5.0
 */
class SqlStreamisCodeTransform extends StreamisCodeTransform {
  override protected def transformCode(transformJob: StreamisCodeTransformJob, job: LaunchJob): LaunchJob = transformJob match {
    case sqlTransformJob: StreamisSqlTransformJob =>
      val jobContent = new util.HashMap[String, Any]
      jobContent.put("code", sqlTransformJob.getStreamJobSqlResource.getExecuteSql)
      jobContent.put("runType", sqlTransformJob.getRunType.toString)
      LaunchJob.builder().setLaunchJob(job).setJobContent(jobContent).build()
    case _ => job
  }
}
