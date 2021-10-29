package com.webank.wedatasphere.streamis.project.server.restful;


import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import com.webank.wedatasphere.streamis.project.server.entity.StreamisProject;
import com.webank.wedatasphere.streamis.project.server.entity.request.CreateProjectRequest;
import com.webank.wedatasphere.streamis.project.server.entity.request.DeleteProjectRequest;
import com.webank.wedatasphere.streamis.project.server.entity.request.UpdateProjectRequest;
import com.webank.wedatasphere.streamis.project.server.service.StreamisProjectService;
import com.webank.wedatasphere.streamis.project.server.utils.StreamisProjectRestfulUtils;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * this is the restful class for streamis project
 */

@Component
@Path("/streamis")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StreamisProjectRestful {



    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisProjectRestful.class);


    @Autowired
    private StreamisProjectService projectService;




    @POST
    @Path("createProject")
    public Response createProject(@Context HttpServletRequest request, @Valid CreateProjectRequest createProjectRequest){
        String username = SecurityFilter.getLoginUsername(request);
        try{
            StreamisProject streamisProject = projectService.createProject(username, createProjectRequest);
            return StreamisProjectRestfulUtils.dealOk("创建工程成功",
                    new Pair<>("projectName", streamisProject.getName()), new Pair<>("projectId", streamisProject.getId()));
        }catch(final Throwable t){
            LOGGER.error("failed to create project for user {}", username, t);
            return StreamisProjectRestfulUtils.dealError("创建工程失败,原因是:" + t.getMessage());
        }
    }




    @POST
    @Path("updateProject")
    public Response updateProject(@Context HttpServletRequest request, @Valid UpdateProjectRequest updateProjectRequest){
        return null;
    }


    @POST
    @Path("deleteProject")
    public Response deleteProject(@Context HttpServletRequest request, @Valid DeleteProjectRequest deleteProjectRequest){
        return null;
    }





}
