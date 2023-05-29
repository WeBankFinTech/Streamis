package com.webank.wedatasphere.streamis.project.server.receiver

import org.apache.linkis.common.utils.{Logging, Utils}
import org.apache.linkis.rpc.{Receiver, Sender}
import com.webank.wedatasphere.streamis.project.common.{CreateStreamProjectRequest, CreateStreamProjectResponse, DeleteStreamProjectRequest, DeleteStreamProjectResponse, UpdateStreamProjectRequest, UpdateStreamProjectResponse}
import com.webank.wedatasphere.streamis.project.server.entity.request.CreateProjectRequest
import com.webank.wedatasphere.streamis.project.server.service.StreamisProjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.concurrent.duration.Duration

/**
 * Description: streamis project 支持rest 和 rpc两种方式
 * 本receiver是rpc的方式
 */
class StreamisProjectServerReceiver(projectService:StreamisProjectService) extends Receiver with Logging{




  override def receive(message: Any, sender: Sender): Unit = {
    //nothing
  }

  override def receiveAndReply(message: Any, sender: Sender): Any = receiveAndReply(message, Duration.create(300, "seconds"), sender)

  override def receiveAndReply(message: Any, duration: Duration, sender: Sender): Any = null




}
