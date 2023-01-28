package com.webank.wedatasphere.streamis.jobmanager.entrypoint.sender

import com.webank.wedatasphere.streamis.jobmanager.entrypoint.config.SenderHttpConfig
import org.apache.http.impl.client.CloseableHttpClient

class StreamJobHeartbeatSender {

  private var httpClient: CloseableHttpClient = null

  private var senderHttpConfig: SenderHttpConfig = null

  def getHttpClient: CloseableHttpClient = this.httpClient
  def setHttpClient(httpClient: CloseableHttpClient): Unit = this.httpClient = httpClient

  def getSenderHttpConfig: SenderHttpConfig = this.senderHttpConfig
  def setSenderHttpConfig(senderHttpConfig: SenderHttpConfig): Unit = this.senderHttpConfig = senderHttpConfig

  /**
   * Send heartbeat request
   * @return
   */
  def send(): String = {
  }
}
