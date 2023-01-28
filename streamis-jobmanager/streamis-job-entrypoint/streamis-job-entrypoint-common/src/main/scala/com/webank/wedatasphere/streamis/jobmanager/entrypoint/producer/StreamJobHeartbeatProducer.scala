package com.webank.wedatasphere.streamis.jobmanager.entrypoint.producer

import com.webank.wedatasphere.streamis.jobmanager.entrypoint.config.StreamJobProducerConfig

trait StreamJobHeartbeatProducer {

  def produce(configMap: java.util.Map[String, Object]): Unit

  def getProducerConfig(engineType: String, configMap: java.util.Map[String, Object]): StreamJobProducerConfig

}
