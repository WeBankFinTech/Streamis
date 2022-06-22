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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


/**
 * this is the restful class for streamis project
 */

@RequestMapping(path = "/streamis")
@RestController
public class StreamisProjectRestfulApi {



    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisProjectRestfulApi.class);


    @Autowired
    private StreamisProjectService projectService;




    @RequestMapping(path = "/createProject", method = RequestMethod.POST)
    public Message createProject( HttpServletRequest request,@RequestBody CreateProjectRequest createProjectRequest){
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




    @RequestMapping(path = "/updateProject", method = RequestMethod.POST)
    public Message updateProject( HttpServletRequest request, @RequestBody UpdateProjectRequest updateProjectRequest){
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


    @RequestMapping(path = "/deleteProject", method = RequestMethod.POST)
    public Message deleteProject( HttpServletRequest request, @RequestBody DeleteProjectRequest deleteProjectRequest){
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
