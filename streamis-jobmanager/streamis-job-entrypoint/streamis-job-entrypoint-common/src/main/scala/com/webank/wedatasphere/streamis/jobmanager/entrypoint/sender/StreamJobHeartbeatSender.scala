package com.webank.wedatasphere.streamis.jobmanager.entrypoint.sender

import com.webank.wedatasphere.streamis.jobmanager.entrypoint.config.SenderHttpConfig
import org.apache.http.impl.client.CloseableHttpClient

trait StreamJobHeartbeatSender {

  /**
   * Encapsulate the httpclient
   * @return
   */
  def send(httpClient: CloseableHttpClient, senderHttpConfig: SenderHttpConfig): Unit
}
