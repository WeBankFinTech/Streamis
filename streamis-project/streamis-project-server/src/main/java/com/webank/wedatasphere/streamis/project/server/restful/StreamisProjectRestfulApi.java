package com.webank.wedatasphere.streamis.project.server.restful;


import com.webank.wedatasphere.streamis.project.common.DeleteStreamProjectRequest;
import com.webank.wedatasphere.streamis.project.common.UpdateStreamProjectRequest;
import com.webank.wedatasphere.streamis.project.server.entity.StreamisProject;
import com.webank.wedatasphere.streamis.project.server.entity.request.CreateProjectRequest;
import com.webank.wedatasphere.streamis.project.server.entity.request.DeleteProjectRequest;
import com.webank.wedatasphere.streamis.project.server.entity.request.UpdateProjectRequest;
import com.webank.wedatasphere.streamis.project.server.service.StreamisProjectService;
import com.webank.wedatasphere.streamis.project.server.utils.StreamisProjectRestfulUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
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


/**
 * this is the restful class for streamis project
 */

@Component
@Path("/streamis")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StreamisProjectRestfulApi {



    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisProjectRestfulApi.class);


    @Autowired
    private StreamisProjectService projectService;




    @POST
    @Path("createProject")
    public Message createProject(@Context HttpServletRequest request, @Valid CreateProjectRequest createProjectRequest){
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
    public Message updateProject(@Context HttpServletRequest request, @Valid UpdateProjectRequest updateProjectRequest){
        String username = SecurityFilter.getLoginUsername(request);
        try{
             projectService.updateProject(new UpdateStreamProjectRequest(updateProjectRequest.getId(),updateProjectRequest.getProjectName(),updateProjectRequest.getDescription(),username));
            return StreamisProjectRestfulUtils.dealOk("更新工程成功",
                    new Pair<>("projectName", updateProjectRequest.getProjectName()), new Pair<>("projectId", updateProjectRequest.getId()));
        }catch(final Throwable t){
            LOGGER.error("failed to update project for user {}", username, t);
            return StreamisProjectRestfulUtils.dealError("更新工程失败,原因是:" + t.getMessage());
        }
    }


    @POST
    @Path("deleteProject")
    public Message deleteProject(@Context HttpServletRequest request, @Valid DeleteProjectRequest deleteProjectRequest){
        String username = SecurityFilter.getLoginUsername(request);
        try{
            projectService.deleteProject(new DeleteStreamProjectRequest(0L,deleteProjectRequest.getProjectName()));
            return StreamisProjectRestfulUtils.dealOk("删除工程成功",
                    new Pair<>("projectName", deleteProjectRequest.getProjectName()));
        }catch(final Throwable t){
            LOGGER.error("failed to delete project for user {}", username, t);
            return StreamisProjectRestfulUtils.dealError("删除工程失败,原因是:" + t.getMessage());
        }
    }





}
