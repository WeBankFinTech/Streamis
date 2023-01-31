package com.webank.wedatasphere.streamis.jobmanager.entrypoint.sender

import com.webank.wedatasphere.streamis.jobmanager.entrypoint.message.JobHeartbeatMessage

class FlinkStreamJobHeartbeatSender extends StreamJobHeartbeatSender {

  /**
   * Send heartbeat request
   *
   * @return
   */
  override def send(message: JobHeartbeatMessage): String = {
    print("Current message is: ", message.getStreamJobConfig.toString)

    // Heartbeat request
    val result = this.getHttpClientUtil.executeAndGet(this.getHttpClient, this.getPostRequest, classOf[String])
    result

  }

}
