package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity

/**
 *
 * @date 2021-06-05
 * @author enjoyyin
 * @since 0.5.0
 */
trait LinkisJobInfo {

  def getId: String

  def getUser: String

  def getStatus: String
  def setStatus(status: String): Unit

  def getLogPath: String

  def getResources: java.util.Map[String, Object]

  def getCompletedMsg: String

}