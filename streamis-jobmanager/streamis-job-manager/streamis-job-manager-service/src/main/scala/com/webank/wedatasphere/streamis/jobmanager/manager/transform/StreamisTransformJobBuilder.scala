package com.webank.wedatasphere.streamis.jobmanager.manager.transform

import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJob
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity.StreamisTransformJob

/**
 *
 * @date 2021-06-06
 * @author enjoyyin
 * @since 0.5.0
 */
trait StreamisTransformJobBuilder {

  def canBuild(streamJob: StreamJob): Boolean

  def build(streamJob: StreamJob): StreamisTransformJob

}
