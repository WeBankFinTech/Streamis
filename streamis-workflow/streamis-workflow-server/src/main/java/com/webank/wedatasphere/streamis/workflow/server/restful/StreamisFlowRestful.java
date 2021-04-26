package com.webank.wedatasphere.streamis.workflow.server.restful;

import com.webank.wedatasphere.streamis.workflow.server.service.StreamFlowService;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
        //todo 先不实现，先通过rpc方式
        return null;
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
