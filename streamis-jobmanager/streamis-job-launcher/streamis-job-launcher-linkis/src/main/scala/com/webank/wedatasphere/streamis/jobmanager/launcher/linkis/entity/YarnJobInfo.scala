package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity

/**
 *
 * @date 2021-06-05
 * @author enjoyyin
 * @since 0.5.0
 */
trait YarnJobInfo extends LinkisJobInfo {

  def getApplicationId: String

  def getApplicationUrl: String

}