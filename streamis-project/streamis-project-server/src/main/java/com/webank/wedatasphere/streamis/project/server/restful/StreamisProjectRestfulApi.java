package com.webank.wedatasphere.streamis.project.server.restful;


import com.webank.wedatasphere.streamis.project.server.constant.ProjectUserPrivilegeEnum;
import com.webank.wedatasphere.streamis.project.server.entity.StreamisProject;
import com.webank.wedatasphere.streamis.project.server.entity.StreamisProjectPrivilege;
import com.webank.wedatasphere.streamis.project.server.entity.request.CreateProjectRequest;
import com.webank.wedatasphere.streamis.project.server.entity.request.DeleteProjectRequest;
import com.webank.wedatasphere.streamis.project.server.entity.request.SearchProjectRequest;
import com.webank.wedatasphere.streamis.project.server.entity.request.UpdateProjectRequest;
import com.webank.wedatasphere.streamis.project.server.service.StreamisProjectService;
import com.webank.wedatasphere.streamis.project.server.utils.StreamisProjectRestfulUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.webank.wedatasphere.streamis.project.server.utils.StreamisProjectPrivilegeUtils.createStreamisProjectPrivilege;


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
    public Message createProject( HttpServletRequest request,@Validated @RequestBody CreateProjectRequest createProjectRequest){
        String username = SecurityFilter.getLoginUsername(request);
        try{
            StreamisProject streamisProject = new StreamisProject(createProjectRequest.getProjectName(), createProjectRequest.getDescription(), null);
            streamisProject.setCreateBy(username);
            List<StreamisProjectPrivilege> privilegeList = new ArrayList<>();
            privilegeList.addAll(createStreamisProjectPrivilege(streamisProject.getId(),createProjectRequest.getReleaseUsers(),ProjectUserPrivilegeEnum.RELEASE.getRank()));
            privilegeList.addAll(createStreamisProjectPrivilege(streamisProject.getId(),createProjectRequest.getEditUsers(), ProjectUserPrivilegeEnum.EDIT.getRank()));
            privilegeList.addAll(createStreamisProjectPrivilege(streamisProject.getId(),createProjectRequest.getAccessUsers(),ProjectUserPrivilegeEnum.ACCESS.getRank()));
            streamisProject.setProjectPrivileges(privilegeList);
            streamisProject = projectService.createProject(streamisProject);
            return StreamisProjectRestfulUtils.dealOk("create project success",
                    new Pair<>("projectName", streamisProject.getName()), new Pair<>("projectId", streamisProject.getId()));
        }catch(Exception e){
            LOGGER.error("failed to create project for user {}", username, e);
            return StreamisProjectRestfulUtils.dealError("failed to create project,reason is:" + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    @RequestMapping(path = "/updateProject", method = RequestMethod.PUT)
    public Message updateProject( HttpServletRequest request, @Validated @RequestBody UpdateProjectRequest updateProjectRequest){
        String username = SecurityFilter.getLoginUsername(request);
        try{
            StreamisProject streamisProject = new StreamisProject(updateProjectRequest.getProjectName(), updateProjectRequest.getDescription(), null);
            streamisProject.setId(updateProjectRequest.getProjectId());
            streamisProject.setCreateBy(username);
            List<StreamisProjectPrivilege> privilegeList = new ArrayList<>();
            privilegeList.addAll(createStreamisProjectPrivilege(streamisProject.getId(),updateProjectRequest.getReleaseUsers(),ProjectUserPrivilegeEnum.RELEASE.getRank()));
            privilegeList.addAll(createStreamisProjectPrivilege(streamisProject.getId(),updateProjectRequest.getEditUsers(), ProjectUserPrivilegeEnum.EDIT.getRank()));
            privilegeList.addAll(createStreamisProjectPrivilege(streamisProject.getId(),updateProjectRequest.getAccessUsers(),ProjectUserPrivilegeEnum.ACCESS.getRank()));
            streamisProject.setProjectPrivileges(privilegeList);
            projectService.updateProject(streamisProject);
            return StreamisProjectRestfulUtils.dealOk("update project success");
        }catch(Exception e){
            LOGGER.error("failed to update project for user {}", username, e);
            return StreamisProjectRestfulUtils.dealError("failed to update project,reason is:" + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    @RequestMapping(path = "/deleteProject", method = RequestMethod.DELETE)
    public Message deleteProject( HttpServletRequest request, @Validated @RequestBody DeleteProjectRequest deleteProjectRequest){
        String username = SecurityFilter.getLoginUsername(request);
        try{
            projectService.deleteProjectById(deleteProjectRequest.getProjectId());
            return StreamisProjectRestfulUtils.dealOk("delete project success");
        }catch(Exception e){
            LOGGER.error("failed to delete project for user {}", username, e);
            return StreamisProjectRestfulUtils.dealError("failed to delete project,reason is:" + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    @RequestMapping(path = "/searchProject", method = RequestMethod.GET)
    public Message searchProject( HttpServletRequest request, @Validated @RequestBody SearchProjectRequest searchProjectRequest){
        String username = SecurityFilter.getLoginUsername(request);
        try{
            StreamisProject streamisProject = projectService.queryProject(searchProjectRequest.getProjectId());
            return StreamisProjectRestfulUtils.dealOk("search project success",
                    new Pair<>("projectName", streamisProject.getName()), new Pair<>("projectId", streamisProject.getId()));
        }catch(Exception e){
            LOGGER.error("failed to search project for user {}", username, e);
            return StreamisProjectRestfulUtils.dealError("failed to search project,reason is:" + ExceptionUtils.getRootCauseMessage(e));
        }
    }


}
