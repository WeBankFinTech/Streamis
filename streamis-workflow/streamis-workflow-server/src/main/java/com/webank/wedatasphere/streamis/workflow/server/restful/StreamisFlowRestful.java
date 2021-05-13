package com.webank.wedatasphere.streamis.workflow.server.restful;

import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import com.webank.wedatasphere.streamis.workflow.common.protocol.StreamFlowCreateRequest;
import com.webank.wedatasphere.streamis.workflow.server.exception.StreamisFlowErrorException;
import com.webank.wedatasphere.streamis.workflow.server.service.StreamFlowService;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

/**
 * created by yangzhiyue on 2021/4/19
 * Description:
 */
@Component
@Path("/streamis")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StreamisFlowRestful {


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisFlowRestful.class);


    @Autowired
    private StreamFlowService streamingFlowService;





    @POST
    @Path("createStreamFlow")
    public Response createStreamFlow(@Context HttpServletRequest request, JsonNode jsonNode){
        String createBy = SecurityFilter.getLoginUsername(request);
        String flowName = jsonNode.get("flowName").getTextValue();
        String description = jsonNode.get("description").getTextValue();
        String workspaceName = jsonNode.get("workspaceName").getTextValue();
        Long parentFlowId = jsonNode.get("parentFlowId").getLongValue();
        String uses = jsonNode.get("uses").getTextValue();
        String contextId = jsonNode.get("contextId").getTextValue();
        Long projectId = jsonNode.get("projectId").getLongValue();
        String projectName = jsonNode.get("projectName").getTextValue();
        StreamFlowCreateRequest streamFlowCreateRequest = new StreamFlowCreateRequest(flowName,
                createBy, description, workspaceName, parentFlowId, uses, new ArrayList<>(), contextId, projectId, projectName);
        try {
            DSSFlow dssFlow = streamingFlowService.createStreamFlow(streamFlowCreateRequest);
            return Message.messageToResponse(Message.ok().data("streamFlow", dssFlow.getId()));
        } catch (StreamisFlowErrorException e) {
            LOGGER.error("failed to create stream flow ", e);
            return Message.messageToResponse(Message.error("Failed to create stream flow"));
        }
    }

    @GET
    @Path("getStreamFlow")
    public Response getStreamFlow(@Context HttpServletRequest request, @QueryParam("flowId") Long flowId){
        String username = SecurityFilter.getLoginUsername(request);
        try{
            DSSFlow dssFlow = streamingFlowService.getStreamFlow(flowId);
            return Message.messageToResponse(Message.ok().data("flow", dssFlow));
        }catch(final Exception e){
            LOGGER.error("failed to get stream flow", e);
            return Message.messageToResponse(Message.error("failed to get stream flow"));
        }

    }



    @POST
    @Path("updateStreamFlow")
    public Response updateStreamFlow(@Context HttpServletRequest request,  JsonNode jsonNode){
        return null;
    }

    @POST
    @Path("deleteStreamFlow")
    public Response deleteStreamFlow(@Context HttpServletRequest request,  JsonNode jsonNode){
        return null;
    }



    @POST
    @Path("exportStreamFlow")
    public Response exportStreamFlow(@Context HttpServletRequest request,  JsonNode jsonNode){
        return null;
    }


    @POST
    @Path("importStreamFlow")
    public Response importStreamFlow(@Context HttpServletRequest request,  JsonNode jsonNode){
        return null;
    }


    @POST
    @Path("publishStreamFlow")
    public Response publishStreamFlow(@Context HttpServletRequest request,  JsonNode jsonNode){
        return null;
    }











}
