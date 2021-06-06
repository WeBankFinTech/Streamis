package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity

import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.vo.ConfigKeyVO

import java.util

/**
 *
 * @date 2021-06-05
 * @author enjoyyin
 * @since 0.5.0
 */
trait LaunchJob {

  def getSubmitUser: String

  def getLabels: util.Map[String, Any]

  def getJobContent: util.Map[String, Any]

  def getParams: util.Map[String, Any]

  def getSource: util.Map[String, Any]

}
