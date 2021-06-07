package com.webank.wedatasphere.streamis.jobmanager.manager.transform.impl

import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.vo.ConfigKeyVO
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LaunchJob

import scala.collection.convert.WrapAsScala._

/**
 *
 * @date 2021-06-06
 * @author enjoyyin
 * @since 0.5.0
 */
class ExtraConfigTransform extends ResourceConfigTransform {

  override protected def transform(config: ConfigKeyVO, job: LaunchJob): LaunchJob = transformConfig(config.getParameterConfig, job)

}
