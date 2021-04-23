package com.webank.wedatasphere.streamis.workflow.common.protocol

import com.webank.wedatasphere.linkis.protocol.Protocol

/**
 * created by yangzhiyue on 2021/4/20
 * Description:
 */
trait StreamisProtocol extends Protocol



case class ProjectCreateRequest(projectName:String,
                                description:String,
                                createBy:String,
                                tags:String) extends StreamisProtocol



case class ProjectCreateResponse(projectName:String,
                                 projectId:Long) extends StreamisProtocol



case class StreamFlowCreateRequest(flowName:String,
                                   createBy:String,
                                   description:String,
                                   workspaceName:String,
                                   parentFlowId:Long,
                                   uses:String,
                                   projectId:Long,
                                   projectName:String
                                   ) extends StreamisProtocol



case class StreamFlowCreateResponse(flowId:Long,
                                    flowVersion:String,
                                    flowName:String) extends StreamisProtocol



case class StreamFlowExportRequest(streamFlowId:Long,
                                   StreamFlowName:String) extends StreamisProtocol



case class StreamFlowExportResponse(streamFlowId:Long,
                                    streamFlowName:String,
                                    bmlResourceId:String,
                                    bmlVersion:String) extends StreamisProtocol