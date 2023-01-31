package com.webank.wedatasphere.streamis.jobmanager.entrypoint.producer

import com.webank.wedatasphere.streamis.jobmanager.entrypoint.config.{SparkStreamJobProducerConfig, StreamJobConfig, StreamJobProducerConfig}
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.message.JobHeartbeatMessage

import java.util

class SparkStreamJobHeartbeatProducer extends StreamJobHeartbeatProducer {

  override def produce(streamJobConfig: StreamJobConfig): JobHeartbeatMessage = {
    var message: JobHeartbeatMessage = new JobHeartbeatMessage
    message.setStreamJobConfig(streamJobConfig)
    message.setEngineType("spark")
    //todo get engineVersion from env
    message
  }

  override def getProducerConfig(engineType: String, configMap: util.Map[String, Object]): StreamJobProducerConfig = {
    var producerConfig = new SparkStreamJobProducerConfig

    producerConfig.asInstanceOf[StreamJobProducerConfig]
  }
}
