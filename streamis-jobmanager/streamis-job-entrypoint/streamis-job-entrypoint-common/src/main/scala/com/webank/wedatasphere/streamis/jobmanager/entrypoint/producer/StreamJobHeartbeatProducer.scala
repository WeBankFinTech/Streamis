package com.webank.wedatasphere.streamis.jobmanager.entrypoint.producer

import com.webank.wedatasphere.streamis.jobmanager.entrypoint.config.{StreamJobConfig, StreamJobProducerConfig}
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.message.JobHeartbeatMessage

trait StreamJobHeartbeatProducer {

  def produce(streamJobConfig: StreamJobConfig): JobHeartbeatMessage

  def getProducerConfig(engineType: String, configMap: java.util.Map[String, Object]): StreamJobProducerConfig

}
