package com.webank.wedatasphere.streamis.jobmanager.entrypoint.sender

import com.webank.wedatasphere.streamis.jobmanager.entrypoint.exception.JobHeartbeatException
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.message.JobHeartbeatMessage
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.utils.HttpClientUtil
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.linkis.common.utils.Logging

class StreamJobHeartbeatSender extends Logging {

  private var httpClient: CloseableHttpClient = null

  private var postRequest: HttpPost = null

  private var httpClientUtil: HttpClientUtil = null

//  private var senderHttpConfig: SenderHttpConfig = null

  def getHttpClient: CloseableHttpClient = this.httpClient
  def setHttpClient(httpClient: CloseableHttpClient): Unit = this.httpClient = httpClient

  def getPostRequest: HttpPost = this.postRequest
  def setPostRequest(postRequest: HttpPost): Unit = this.postRequest = postRequest

  def getHttpClientUtil: HttpClientUtil = this.httpClientUtil
  def setHttpClientUtil(httpClientUtil: HttpClientUtil): Unit = this.httpClientUtil = httpClientUtil

//  def getSenderHttpConfig: SenderHttpConfig = this.senderHttpConfig
//  def setSenderHttpConfig(senderHttpConfig: SenderHttpConfig): Unit = this.senderHttpConfig = senderHttpConfig

  def init(httpClient: CloseableHttpClient, postRequest: HttpPost): Unit = {
    this.httpClient = httpClient
    this.postRequest = postRequest
    this.httpClientUtil = new HttpClientUtil()
  }

  /**
   * Send heartbeat request
   * @return
   */
  def send(message: JobHeartbeatMessage): String = {
    throw new JobHeartbeatException(-1, "This method cannot be called, call the method of the subclass")
  }
}
