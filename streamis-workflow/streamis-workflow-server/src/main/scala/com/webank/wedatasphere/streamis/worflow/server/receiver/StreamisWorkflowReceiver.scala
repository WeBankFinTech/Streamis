package com.webank.wedatasphere.streamis.worflow.server.receiver

import com.webank.wedatasphere.linkis.common.utils.Logging
import com.webank.wedatasphere.linkis.rpc.{Receiver, Sender}
import com.webank.wedatasphere.streamis.workflow.common.protocol.{StreamFlowCreateRequest, StreamFlowExportRequest}
import com.webank.wedatasphere.streamis.workflow.server.service.StreamFlowService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.concurrent.duration.Duration

/**
 * created by yangzhiyue on 2021/4/20
 * Description:
 */
@Component
class StreamisWorkflowReceiver extends Receiver with Logging{

  @Autowired
  var streamFlowService:StreamFlowService = _





  override def receive(message: Any, sender: Sender): Unit = {

  }

  override def receiveAndReply(message: Any, sender: Sender): Any = message match {
    case streamFlowCreateRequest: StreamFlowCreateRequest => streamFlowService.createStreamFlow(streamFlowCreateRequest)
    case streamFlowExportRequest: StreamFlowExportRequest => streamFlowService.exportStreamFlow(streamFlowExportRequest)

  }

  override def receiveAndReply(message: Any, duration: Duration, sender: Sender): Any = ???


}
