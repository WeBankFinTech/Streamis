package com.webank.wedatasphere.streamis.jobmanager.entrypoint.producer

import com.webank.wedatasphere.streamis.jobmanager.entrypoint.config.{FLinkStreamJobProducerConfig, StreamJobConfig, StreamJobProducerConfig}
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.message.JobHeartbeatMessage

import java.util

class FlinkStreamJobHeartbeatProducer extends StreamJobHeartbeatProducer {

  override def produce(streamJobConfig: StreamJobConfig): JobHeartbeatMessage = {
    var message: JobHeartbeatMessage = new JobHeartbeatMessage
    message.setStreamJobConfig(streamJobConfig)
    message.setEngineType("flink")
    message.setEngineVersion("1.12.2")//todo get from env
    message
  }

  override def getProducerConfig(engineType: String, configMap: util.Map[String, Object]): StreamJobProducerConfig = {
    var producerConfig = new FLinkStreamJobProducerConfig
    producerConfig.setApplicationUrl(configMap.get("applicationUrl").toString)
    producerConfig.setJobId(configMap.get("jobId").toString)

    producerConfig.asInstanceOf[StreamJobProducerConfig]
  }
}
