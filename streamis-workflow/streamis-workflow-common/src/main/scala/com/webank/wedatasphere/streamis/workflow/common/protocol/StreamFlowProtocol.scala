package com.webank.wedatasphere.streamis.workflow.common.protocol

import com.webank.wedatasphere.linkis.protocol.Protocol

/**
 * created by yangzhiyue on 2021/4/20
 * Description:
 */
trait StreamFlowProtocol extends Protocol




case class StreamFlowCreateRequest(flowName:String,
                                   createBy:String,
                                   description:String,
                                   workspaceName:String,
                                   parentFlowId:Long,
                                   uses:String,
                                   linkedAppConnNames:java.util.List[String],
                                   contextId:String,
                                   projectId:Long,
                                   projectName:String
                                   ) extends StreamFlowProtocol



case class StreamFlowCreateResponse(status:Int,
                                    flowId:Long,
                                    flowVersion:String,
                                    flowName:String,
                                    errorMessage:String) extends StreamFlowProtocol



case class StreamFlowExportRequest(username:String,
                                   streamFlowId:Long,
                                   projectId:Long,
                                   projectName:String,
                                   workspaceStr:String
                                  ) extends StreamFlowProtocol


case class StreamFlowCopyRequest(username:String,
                                 workspaceName:String,
                                 streamFlowId:Long,
                                 contextId:String,
                                 projectName:String,
                                 orchestratorVersionId:Long,
                                 version:String,
                                 description:String,
                                 orchestratorId:Long) extends StreamFlowProtocol

case class StreamFlowExportResponse(status:Int,
                                    streamFlowId:Long,
                                    streamFlowName:String,
                                    bmlResourceId:String,
                                    bmlVersion:String,
                                    errorMessage:String) extends StreamFlowProtocol



case class StreamFlowCopyResponse(status:Int,
                                  oldStreamFlowId:Long,
                                  newStreamFlowId:Long,
                                  orchestratorId:Long,
                                  errorMessage:String) extends StreamFlowProtocol








case class StreamFlowImportRequest(streamFlowName:String,
                                   username:String,
                                   projectName:String,
                                   projectId:Long,
                                   bmlResourceId:String,
                                   bmlVersion:String,
                                   orchestratorVersion:String,
                                   workspaceName:String,
                                   workspaceStr:String
                                  ) extends StreamFlowProtocol


case class StreamFlowImportResponse(status:Int,
                                    flowIds:java.util.List[Long],
                                    errorMessage:String) extends StreamFlowProtocol


case class StreamFlowQueryRequest()