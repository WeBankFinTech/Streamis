package com.webank.wedatasphere.streamis.jobmanager.restful.api;

import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import com.webank.wedatasphere.streamis.jobmanager.exception.ProjectException;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.TaskCoreNumVO;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.JobService;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Component
@Path("/streamis/streamJobManager/project")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectRestfulApi {
    @Autowired
    ProjectService projectService;
    @Autowired
    JobService jobService;
    @GET
    @Path("/core/target")
    public Response getView(@Context HttpServletRequest req, @QueryParam("projectId") Long projectId) throws IOException, ProjectException {

        if(projectId==null){
            throw new ProjectException("params cannot be empty!");
        }
        TaskCoreNumVO taskCoreNumVO = jobService.countByCores(projectId);
        return  Message.messageToResponse(Message.ok().data("taskCore",taskCoreNumVO));
    }
}
