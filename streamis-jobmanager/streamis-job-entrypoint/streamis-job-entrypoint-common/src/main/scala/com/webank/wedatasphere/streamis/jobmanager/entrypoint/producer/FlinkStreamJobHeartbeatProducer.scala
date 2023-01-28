package com.webank.wedatasphere.streamis.jobmanager.entrypoint.producer
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.config.{FLinkStreamJobProducerConfig, StreamJobProducerConfig}

import java.util

class FlinkStreamJobHeartbeatProducer extends StreamJobHeartbeatProducer {

  override def produce(configMap: java.util.Map[String, Object]): Unit = {
    val configuration = configMap.get("configuration")
    val streamExecutionEnvironment = configMap.get("streamExecutionEnvironment")

    val flinkConfig = getProducerConfig("flink", configMap)


  }

  override def getProducerConfig(engineType: String, configMap: util.Map[String, Object]): StreamJobProducerConfig = {
    var producerConfig = new FLinkStreamJobProducerConfig
    producerConfig.setApplicationUrl(configMap.get("applicationUrl").toString)
    producerConfig.setJobId(configMap.get("jobId").toString)

    producerConfig.asInstanceOf[StreamJobProducerConfig]
  }
}
