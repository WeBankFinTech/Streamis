package com.webank.wedatasphere.streamis.worflow.server.receiver

import com.webank.wedatasphere.linkis.rpc.{RPCMessageEvent, Receiver, ReceiverChooser}
import com.webank.wedatasphere.streamis.workflow.common.protocol.StreamFlowProtocol
import com.webank.wedatasphere.streamis.workflow.server.service.StreamFlowService
import org.springframework.beans.factory.annotation.Autowired

import javax.annotation.PostConstruct

/**
 * created by yangzhiyue on 2021/4/30
 * Description:
 */
class StreamisWorkflowReceiverChooser extends ReceiverChooser{

  @Autowired
  var streamFlowService:StreamFlowService = _


  private var receiver: Option[StreamisWorkflowReceiver] = _

  @PostConstruct
  def init():Unit = {
    receiver = Some(new StreamisWorkflowReceiver(streamFlowService))
  }

  override def chooseReceiver(event: RPCMessageEvent): Option[Receiver] = event.message match {
    case streamFlowProtocol: StreamFlowProtocol => receiver
    case _ => None
  }

}
