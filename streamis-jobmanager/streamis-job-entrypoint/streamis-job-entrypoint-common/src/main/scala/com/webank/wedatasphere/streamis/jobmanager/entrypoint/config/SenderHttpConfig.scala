package com.webank.wedatasphere.streamis.jobmanager.entrypoint.config

import java.net.URL

class SenderHttpConfig {

  /**
   * Request url
   */
  private var url: URL = _

  /**
   * Request type
   * POST/GET
   */
  private var requestType: String = _

  /**
   * Request headers
   */
  private var headers: Map[String, String] = _

  /**
   * Request body
   */
  private var requestBody: String = _

  /**
   * Timeout of http request
   */
  private var requestTimeout : scala.concurrent.duration.Duration = _

  def getUrl: URL = this.url
  def setUrl(url: URL): Unit = this.url = url

  def getRequestType: String = this.requestType
  def setRequestType(requestType: String): Unit = this.requestType = requestType

  def getHeaders: Map[String, String] = this.headers
  def setHeaders(headers: Map[String, String]): Unit = this.headers = headers

  def getRequestBody: String = this.requestBody
  def setRequestBody(requestBody: String): Unit = this.requestBody = requestBody

  def getRequestTimeout: scala.concurrent.duration.Duration = this.requestTimeout
  def setRequestTimeout(requestTimeout: scala.concurrent.duration.Duration): Unit = this.requestTimeout = requestTimeout
}
