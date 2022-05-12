package com.webank.wedatasphere.streamis.project.server.service.impl;


import com.webank.wedatasphere.streamis.project.common.CreateStreamProjectRequest;
import com.webank.wedatasphere.streamis.project.server.dao.StreamisProjectMapper;
import com.webank.wedatasphere.streamis.project.server.entity.StreamisProject;
import com.webank.wedatasphere.streamis.project.server.entity.request.CreateProjectRequest;
import com.webank.wedatasphere.streamis.project.server.entity.request.DeleteProjectRequest;
import com.webank.wedatasphere.streamis.project.server.entity.request.UpdateProjectRequest;
import com.webank.wedatasphere.streamis.project.server.exception.StreamisProjectErrorException;
import com.webank.wedatasphere.streamis.project.server.service.StreamisProjectPrivilegeService;
import com.webank.wedatasphere.streamis.project.server.service.StreamisProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Description:
 */
@Service
public class StreamisProjectServiceImpl implements StreamisProjectService {


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisProjectServiceImpl.class);

    @Autowired
    private StreamisProjectMapper streamisProjectMapper;

    @Autowired
    private StreamisProjectPrivilegeService streamisProjectPrivilegeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StreamisProject createProject(String username, CreateProjectRequest createProjectRequest) throws StreamisProjectErrorException {
        LOGGER.info("user {} starts to create project {}", username, createProjectRequest.getProjectName());
        if (!CollectionUtils.isEmpty(streamisProjectMapper.findProjectByName(createProjectRequest.getProjectName()))) {
            throw new StreamisProjectErrorException(600500, "the project name is exist");
        }
        StreamisProject streamisProject = new StreamisProject(createProjectRequest.getProjectName(), createProjectRequest.getDescription(), createProjectRequest.getWorkspaceName());
        streamisProject.setCreateBy(username);
        streamisProjectMapper.createProject(streamisProject);
        LOGGER.info("user {} ends to create project {} and id is {}", streamisProject.getCreateBy(), streamisProject.getName(), streamisProject.getId());
        streamisProjectPrivilegeService.addProjectPrivilege(streamisProject.getId(),createProjectRequest);
        return streamisProject;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StreamisProject createProject(CreateStreamProjectRequest createStreamProjectRequest) throws StreamisProjectErrorException {
        LOGGER.info("user {} starts to create project {}", createStreamProjectRequest.createBy(), createStreamProjectRequest.projectName());
        if (!CollectionUtils.isEmpty(streamisProjectMapper.findProjectByName(createStreamProjectRequest.projectName()))) {
            throw new StreamisProjectErrorException(600500, "the project name is exist");
        }
        StreamisProject streamisProject = new StreamisProject(createStreamProjectRequest.projectName(), createStreamProjectRequest.description(), null);
        streamisProject.setCreateBy(createStreamProjectRequest.createBy());
        streamisProjectMapper.createProject(streamisProject);
        LOGGER.info("user {} ends to create project {} and id is {}", createStreamProjectRequest.createBy(), createStreamProjectRequest.projectName(), streamisProject.getId());
        return streamisProject;
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProject(String username, UpdateProjectRequest updateProjectRequest) throws StreamisProjectErrorException {
        LOGGER.info("User {} begins to update project {}", updateProjectRequest.getUpdateBy(), updateProjectRequest.getProjectName());
        List<Long> list = streamisProjectMapper.findProjectByName(updateProjectRequest.getProjectName());
        if (!CollectionUtils.isEmpty(list) && list.get(0) != updateProjectRequest.getProjectId()) {
            throw new StreamisProjectErrorException(600500, "the project name is exist");
        }
        StreamisProject streamisProject = new StreamisProject(updateProjectRequest.getProjectName(), updateProjectRequest.getDescription(), null);
        streamisProject.setId(updateProjectRequest.getProjectId());
        streamisProject.setLastUpdateBy(username);
        streamisProjectMapper.updateProject(streamisProject);
        LOGGER.info("user {} ends to update, project name is {} and id is {}",updateProjectRequest.getUpdateBy(),updateProjectRequest.getProjectName(),updateProjectRequest.getProjectId());
        streamisProjectPrivilegeService.updateProjectPrivilege(updateProjectRequest.getProjectId(),updateProjectRequest);
    }


    @Override
    public void deleteProject(DeleteProjectRequest deleteProjectRequest) throws StreamisProjectErrorException {
        streamisProjectMapper.deleteProjectByName(deleteProjectRequest.getProjectName());
        LOGGER.info("delete project {}", deleteProjectRequest.getProjectName());
        streamisProjectPrivilegeService.deleteProjectPrivilege(deleteProjectRequest.getProjectId());
    }
}
