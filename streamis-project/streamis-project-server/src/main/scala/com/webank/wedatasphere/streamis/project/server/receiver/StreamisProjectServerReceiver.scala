package com.webank.wedatasphere.streamis.project.server.receiver

import com.webank.wedatasphere.linkis.common.utils.{Logging, Utils}
import com.webank.wedatasphere.linkis.rpc.{Receiver, Sender}
import com.webank.wedatasphere.streamis.project.common.{CreateStreamProjectRequest, CreateStreamProjectResponse, UpdateStreamProjectRequest, UpdateStreamProjectResponse}
import com.webank.wedatasphere.streamis.project.server.entity.request.CreateProjectRequest
import com.webank.wedatasphere.streamis.project.server.service.StreamisProjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.concurrent.duration.Duration

/**
 * created by yangzhiyue on 2021/4/23
 * Description: streamis project 支持rest 和 rpc两种方式
 * 本receiver是rpc的方式
 */
class StreamisProjectServerReceiver(projectService:StreamisProjectService) extends Receiver with Logging{




  override def receive(message: Any, sender: Sender): Unit = {

  }

  override def receiveAndReply(message: Any, sender: Sender): Any = receiveAndReply(message, Duration.create(300, "seconds"), sender)

  override def receiveAndReply(message: Any, duration: Duration, sender: Sender): Any = message match {
    case createStreamProjectRequest: CreateStreamProjectRequest =>
      Utils.tryCatch{
        val streamisProject = projectService.createProject(createStreamProjectRequest)
        CreateStreamProjectResponse(0, streamisProject.getName, streamisProject.getId, "")
      }{
        t => logger.error("failed to create project in streamis", t)
          CreateStreamProjectResponse(-1, createStreamProjectRequest.projectName, -1, t.getCause.getMessage)
      }
    case updateStreamProjectRequest: UpdateStreamProjectRequest => Utils.tryCatch{
      projectService.updateProject(updateStreamProjectRequest)
      UpdateStreamProjectResponse(0, updateStreamProjectRequest.streamisProjectId, "")
    }{
      t => logger.error(s"failed to update project ${updateStreamProjectRequest.projectName} in streamis",t)
        UpdateStreamProjectResponse(-1, updateStreamProjectRequest.streamisProjectId, t.getCause.getMessage)
    }
    case _ =>
  }



}
