package com.webank.wedatasphere.streamis.jobmanager.entrypoint.sender

import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.utils.HttpClientUtil
import org.apache.http.client.methods.HttpGet

class FlinkStreamJobHeartbeatSender extends StreamJobHeartbeatSender {

  /**
   * Send heartbeat request
   *
   * @return
   */
  override def send(): String = {
    // Heartbeat request
    val httpGet: HttpGet = HttpClientUtil.getGetRequest
    httpGet.setURI(this.getSenderHttpConfig.getUrl.toURI)
    val result = HttpClientUtil.executeAndGet(this.getHttpClient, httpGet, Class[String])

    result

    // Save jobInfo



  }

}
