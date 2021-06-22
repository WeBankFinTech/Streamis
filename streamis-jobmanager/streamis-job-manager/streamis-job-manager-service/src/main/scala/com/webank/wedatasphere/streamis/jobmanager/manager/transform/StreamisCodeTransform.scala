package com.webank.wedatasphere.streamis.jobmanager.manager.transform
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LaunchJob
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity.{StreamisCodeTransformJob, StreamisTransformJob}

/**
 *
 * @date 2021-06-06
 * @author enjoyyin
 * @since 0.5.0
 */
trait StreamisCodeTransform extends Transform {

  override def transform(streamisTransformJob: StreamisTransformJob, job: LaunchJob): LaunchJob = streamisTransformJob match {
    case transformJob: StreamisCodeTransformJob => transformCode(transformJob, job)
    case _ => job
  }

  protected def transformCode(transformJob: StreamisCodeTransformJob, job: LaunchJob): LaunchJob
}
