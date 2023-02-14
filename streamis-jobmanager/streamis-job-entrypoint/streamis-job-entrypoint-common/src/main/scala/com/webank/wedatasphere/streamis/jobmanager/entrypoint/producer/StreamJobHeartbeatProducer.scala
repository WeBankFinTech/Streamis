package com.webank.wedatasphere.streamis.jobmanager.entrypoint.producer

import com.webank.wedatasphere.streamis.jobmanager.entrypoint.config.StreamJobConfig
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.message.JobHeartbeatMessage

trait StreamJobHeartbeatProducer {

  def produce(streamJobConfig: StreamJobConfig): JobHeartbeatMessage

}
