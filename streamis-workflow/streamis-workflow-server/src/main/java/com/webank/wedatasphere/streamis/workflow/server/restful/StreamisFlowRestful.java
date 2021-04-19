package com.webank.wedatasphere.streamis.workflow.server.restful;

import com.webank.wedatasphere.streamis.workflow.server.service.StreamFlowService;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * created by yangzhiyue on 2021/4/19
 * Description:
 */
public class StreamisFlowRestful {


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisFlowRestful.class);


    @Autowired
    private StreamFlowService streamingFlowService;





    @POST
    @Path("createNewStreamisOrc")
    public Response createNewStreamisOrc(@Context HttpServletRequest request, JsonNode jsonNode){
        //主要是通过workflowserver模块将工作流新建
        return null;
    }

}
