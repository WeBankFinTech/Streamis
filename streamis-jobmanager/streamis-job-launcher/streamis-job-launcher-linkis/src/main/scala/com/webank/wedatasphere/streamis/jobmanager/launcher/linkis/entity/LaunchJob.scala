package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity

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

object LaunchJob {

  def builder(): Builder = new Builder

  class Builder {
    private var submitUser: String = _
    private var labels: util.Map[String, Any] = _
    private var jobContent: util.Map[String, Any] = _
    private var params: util.Map[String, Any] = _
    private var source: util.Map[String, Any] = _
    def setSubmitUser(submitUser: String): this.type = {
      this.submitUser = submitUser
      this
    }
    def setLabels(labels: util.Map[String, Any]): this.type = {
      this.labels = labels
      this
    }
    def setJobContent(jobContent: util.Map[String, Any]): this.type = {
      this.jobContent = jobContent
      this
    }
    def setParams(param: util.Map[String, Any]): this.type = {
      this.params = param
      this
    }
    def setSource(source: util.Map[String, Any]): this.type = {
      this.source = source
      this
    }
    def setLaunchJob(launchJob: LaunchJob): this.type = {
      setSubmitUser(launchJob.getSubmitUser).setLabels(launchJob.getLabels)
        .setJobContent(launchJob.getJobContent).setParams(launchJob.getParams).setSource(launchJob.getSource)
    }
    def build(): LaunchJob = new LaunchJob {
      override def getSubmitUser: String = submitUser

      override def getLabels: util.Map[String, Any] = labels

      override def getJobContent: util.Map[String, Any] = jobContent

      override def getParams: util.Map[String, Any] = params

      override def getSource: util.Map[String, Any] = source

      override def toString: String = s"LaunchJob(submitUser: $submitUser, labels: $labels, jobContent: $jobContent, params: $params, source: $source)"
    }
  }

}
