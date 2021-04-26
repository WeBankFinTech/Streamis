package com.webank.wedatasphere.streamis.worflow.server.receiver

import com.google.common.collect.Lists
import com.webank.wedatasphere.linkis.common.conf.CommonVars
import com.webank.wedatasphere.linkis.common.utils.{Logging, Utils}
import com.webank.wedatasphere.linkis.rpc.{Receiver, Sender}
import com.webank.wedatasphere.streamis.workflow.common.protocol.{StreamFlowCreateRequest, StreamFlowCreateResponse, StreamFlowExportRequest, StreamFlowExportResponse, StreamFlowImportRequest, StreamFlowImportResponse}
import com.webank.wedatasphere.streamis.workflow.server.conf.StreamisFlowConf
import com.webank.wedatasphere.streamis.workflow.server.service.StreamFlowService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.concurrent.duration.Duration
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

/**
 * created by yangzhiyue on 2021/4/20
 * Description:
 */
@Component
class StreamisWorkflowReceiver extends Receiver with Logging {

  @Autowired
  var streamFlowService: StreamFlowService = _


  private val timeout = CommonVars("wds.streamis.workflow.ask.timeout", 300).getValue


  override def receive(message: Any, sender: Sender): Unit = {

  }

  override def receiveAndReply(message: Any, sender: Sender): Any = receiveAndReply(message, Duration(timeout, "seconds"), sender)

  override def receiveAndReply(message: Any, duration: Duration, sender: Sender): Any = message match {
    case streamFlowCreateRequest: StreamFlowCreateRequest => Utils.tryCatch {
      val flow = streamFlowService.createStreamFlow(streamFlowCreateRequest)
      StreamFlowCreateResponse(0, flow.getId, StreamisFlowConf.DEFAULT_VERSION, flow.getName, "")
    } {
      t => logger.error(s"failed to create stream flow ${streamFlowCreateRequest.parentFlowId} in streamis", t)
        StreamFlowCreateResponse(-1, -1, "", streamFlowCreateRequest.flowName, t.getCause.getMessage)
    }
    case streamFlowExportRequest: StreamFlowExportRequest => Utils.tryCatch{
      val exportResponse = streamFlowService.exportStreamFlow(streamFlowExportRequest)
      StreamFlowExportResponse(0, streamFlowExportRequest.streamFlowId, "", exportResponse.getResourceId, exportResponse.getVersion, "")
    }{
      t => logger.error(s"failed to export stream flow ${streamFlowExportRequest.streamFlowId}", t)
        StreamFlowExportResponse(-1, streamFlowExportRequest.streamFlowId, "" , null, null, t.getCause.getMessage)
    }
    case streamFlowImportRequest: StreamFlowImportRequest => Utils.tryCatch{
      val dssFlows = streamFlowService.importStreamFlow(streamFlowImportRequest)
      StreamFlowImportResponse(0, Lists.newArrayList(dssFlows.map(_.getId).toList.asJavaCollection), "")
    }{
      t => logger.error(s"failed to import resourceId: ${streamFlowImportRequest.bmlResourceId} version is ${streamFlowImportRequest.bmlVersion}", t)
        StreamFlowImportResponse(-1, null, t.getCause.getMessage)
    }
  }


}
