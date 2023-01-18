package com.webank.wedatasphere.streamis.jobmanager.entrypoint.producer

trait StreamJobHeartbeatProducer {

  def produce(): Unit

}
