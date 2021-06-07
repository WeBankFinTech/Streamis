package com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity

import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.vo.ConfigKeyVO
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.{StreamJob, StreamJobVersion}

/**
 *
 * @date 2021-06-06
 * @author enjoyyin
 * @since 0.5.0
 */
trait StreamisTransformJob {

  def getStreamJob: StreamJob

  def getStreamJobVersion: StreamJobVersion

  def getConfig: ConfigKeyVO

}
