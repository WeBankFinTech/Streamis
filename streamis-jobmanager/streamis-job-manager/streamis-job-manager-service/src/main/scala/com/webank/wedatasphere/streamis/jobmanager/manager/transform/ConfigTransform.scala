package com.webank.wedatasphere.streamis.jobmanager.manager.transform

import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.vo.ConfigKeyVO
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LaunchJob
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity.StreamisTransformJob

/**
 *
 * @date 2021-06-06
 * @author enjoyyin
 * @since 0.5.0
 */
trait ConfigTransform extends Transform {

  override def transform(streamisTransformJob: StreamisTransformJob, job: LaunchJob): LaunchJob = transform(streamisTransformJob.getConfig, job)

  protected def transform(config: ConfigKeyVO, job: LaunchJob): LaunchJob

}