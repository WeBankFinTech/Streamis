package com.webank.wedatasphere.streamis.jobmanager.entrypoint.producer

import com.webank.wedatasphere.streamis.jobmanager.entrypoint.config.StreamJobConfig
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.message.JobHeartbeatMessage

class SparkStreamJobHeartbeatProducer extends StreamJobHeartbeatProducer {

  override def produce(streamJobConfig: StreamJobConfig): JobHeartbeatMessage = {
    var message: JobHeartbeatMessage = new JobHeartbeatMessage
    message.setStreamJobConfig(streamJobConfig)
    message.setEngineType("spark")
    //todo get engineVersion from env
    message
  }
}
