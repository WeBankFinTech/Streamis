package com.webank.wedatasphere.streamis.jobmanager.entrypoint.config


class StreamJobProducerConfig {

  /**
   * Application Id
   */
  private var applicationId : String = _

  /**
   * ResourceManagerAddress
   */
  private var resourceManagerAddress: String = _

  /**
   * JobStatus
   */
  private var jobStatus: String = _

  /**
   * JobName
   */
  private var jobName: String = _

  /**
   * ProjectName
   */
  private var projectName: String = _

  def getApplicationId: String = this.applicationId
  def setApplicationId(applicationId: String): Unit = this.applicationId = applicationId

  def getResourceManagerAddress: String = this.resourceManagerAddress
  def setResourceManagerAddress(resourceManagerAddress: String): Unit = this.resourceManagerAddress = resourceManagerAddress

  def getJobStatus: String = this.jobStatus
  def setJobStatus(jobStatus: String): Unit = this.jobStatus = jobStatus

  def getJobName: String = this.jobName
  def setJobName(jobName: String): Unit = this.jobName = jobName

  def getProjectName: String = this.projectName
  def setProjectName(projectName: String): Unit = this.projectName = projectName
}

class SparkStreamJobProducerConfig extends StreamJobProducerConfig {

}

class FLinkStreamJobProducerConfig extends StreamJobProducerConfig {

  /**
   * ApplicationUrl
   */
  private var applicationUrl: String = _

  /**
   * JobId
   */
  private var jobId: String = _

  def getApplicationUrl: String = this.applicationUrl
  def setApplicationUrl(applicationUrl: String): Unit = this.applicationUrl = applicationUrl

  def getJobId: String = this.jobId
  def setJobId(jobId: String): Unit = this.jobId = jobId

}