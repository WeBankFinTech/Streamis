package com.webank.wedatasphere.streamis.jobmanager.entrypoint.sender

import com.webank.wedatasphere.streamis.jobmanager.entrypoint.message.JobHeartbeatMessage

class SparkStreamJobHeartbeatSender extends StreamJobHeartbeatSender {


  /**
   * Send heartbeat request
   *
   * @return
   */
  override def send(message: JobHeartbeatMessage): String = {

    // Heartbeat request
    val result = this.getHttpClientUtil.executeAndGet(this.getHttpClient, this.getPostRequest, classOf[String])
    result
  }

}
