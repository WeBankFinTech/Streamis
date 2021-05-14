package com.webank.wedatasphere.streamis.jobmanager.restful.api;

import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.streamis.jobmanager.exception.ConfigurationException;
import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.vo.ConfigKeyVO;
import com.webank.wedatasphere.streamis.jobmanager.launcher.service.ConfigurationService;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

/**
 * @author limeng
 */
@Component
@Path("/streamis/streamJobManager/config")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConfigurationRestfulApi {
    @Autowired
    ConfigurationService configurationService;

    ObjectMapper mapper = new ObjectMapper();


    @GET
    @Path("/view")
    public Response getView(@Context HttpServletRequest req,@QueryParam("jobId") Long jobId) throws IOException, ConfigurationException {
        if(jobId==null){
            throw new ConfigurationException("params cannot be empty!");
        }
        ConfigKeyVO fullTree = configurationService.getFullTree(jobId);
        return  Message.messageToResponse(Message.ok().data("fullTree",fullTree));
    }

    @POST
    @Path("/update")
    public Response updateFullTree(@Context HttpServletRequest req, JsonNode json) throws  IOException,ConfigurationException {
        ConfigKeyVO fullTrees = mapper.readValue(json.get("fullTree"), ConfigKeyVO.class);

        configurationService.addKeyValue(fullTrees);
        return  Message.messageToResponse(Message.ok());
    }

    @POST
    @Path("/add")
    public Response saveFullTree(@Context HttpServletRequest req, JsonNode json) throws  IOException,ConfigurationException {
        ConfigKeyVO fullTrees = mapper.readValue(json.get("fullTree"), ConfigKeyVO.class);

        configurationService.addKeyValue(fullTrees);
        return  Message.messageToResponse(Message.ok());
    }


}
