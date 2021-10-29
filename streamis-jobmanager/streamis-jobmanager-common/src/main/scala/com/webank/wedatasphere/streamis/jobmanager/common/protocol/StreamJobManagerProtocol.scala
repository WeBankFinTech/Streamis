package com.webank.wedatasphere.streamis.jobmanager.common.protocol

import com.webank.wedatasphere.linkis.protocol.Protocol

/**
 * created by yangzhiyue on 2021/4/26
 * Description:
 */
trait StreamJobManagerProtocol extends Protocol


case class ImportJobManagerRequest(streamJobName:String,
                                   `type`:String,
                                   executionCode:String,
                                   createBy:String,
                                   updateBy:String,
                                   description:String,
                                   tags:java.util.List[String],
                                   publishUser:String,
                                   workspaceName:String,
                                   version:String,
                                   projectName:String) extends StreamJobManagerProtocol


case class ImportJobManagerResponse(status:Int,
                                    streamJobId:Long,
                                    errorMessage:String) extends StreamJobManagerProtocol