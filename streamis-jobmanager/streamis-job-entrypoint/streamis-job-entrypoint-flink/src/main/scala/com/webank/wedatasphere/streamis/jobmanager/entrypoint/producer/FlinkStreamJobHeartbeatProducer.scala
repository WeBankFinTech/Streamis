package com.webank.wedatasphere.streamis.jobmanager.entrypoint.producer

import com.webank.wedatasphere.streamis.jobmanager.entrypoint.config.StreamJobConfig
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.message.JobHeartbeatMessage

class FlinkStreamJobHeartbeatProducer extends StreamJobHeartbeatProducer {

  override def produce(streamJobConfig: StreamJobConfig): JobHeartbeatMessage = {
    var message: JobHeartbeatMessage = new JobHeartbeatMessage
    message.setStreamJobConfig(streamJobConfig)
    message.setEngineType("flink")
    message.setEngineVersion("1.12.2")
    message
  }
}
