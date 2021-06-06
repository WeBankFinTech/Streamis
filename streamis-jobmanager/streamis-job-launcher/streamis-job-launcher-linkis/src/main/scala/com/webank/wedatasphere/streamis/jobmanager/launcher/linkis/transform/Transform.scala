package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.transform

import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LaunchJob

/**
 *
 * @date 2021-06-06
 * @author enjoyyin
 * @since 0.5.0
 */
trait Transform[T] {

  def transform(t: T, job: LaunchJob): LaunchJob

}
