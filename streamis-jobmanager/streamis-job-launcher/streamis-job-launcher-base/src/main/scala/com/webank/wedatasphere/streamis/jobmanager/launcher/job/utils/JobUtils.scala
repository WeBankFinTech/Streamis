package com.webank.wedatasphere.streamis.jobmanager.launcher.job.utils

object JobUtils {

  def isAnyVal[T](x: T)(implicit m: Manifest[T]) = m <:< manifest[AnyVal]

}
