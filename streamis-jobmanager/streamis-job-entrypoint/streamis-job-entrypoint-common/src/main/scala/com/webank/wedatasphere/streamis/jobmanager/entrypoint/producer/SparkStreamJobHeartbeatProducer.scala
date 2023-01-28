package com.webank.wedatasphere.streamis.jobmanager.entrypoint.producer

import com.webank.wedatasphere.streamis.jobmanager.entrypoint.config.{SparkStreamJobProducerConfig, StreamJobProducerConfig}

import java.util

class SparkStreamJobHeartbeatProducer extends StreamJobHeartbeatProducer{

  override def produce(configMap: java.util.Map[String, Object]): Unit = {
    val fileName = configMap.get("fileName")
    val sparkConfigMap = configMap.get("SparkConfigMap")

    val sparkConfig = getProducerConfig("spark", configMap)


  }

  override def getProducerConfig(engineType: String, configMap: util.Map[String, Object]): StreamJobProducerConfig = {
    var producerConfig = new SparkStreamJobProducerConfig


    producerConfig.asInstanceOf[StreamJobProducerConfig]
  }
}
