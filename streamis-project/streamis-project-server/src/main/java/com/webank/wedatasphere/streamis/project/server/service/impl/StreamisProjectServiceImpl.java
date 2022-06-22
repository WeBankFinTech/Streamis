package com.webank.wedatasphere.streamis.project.server.service.impl;


import com.webank.wedatasphere.streamis.project.common.CreateStreamProjectRequest;
import com.webank.wedatasphere.streamis.project.common.DeleteStreamProjectRequest;
import com.webank.wedatasphere.streamis.project.common.UpdateStreamProjectRequest;
import com.webank.wedatasphere.streamis.project.server.dao.StreamisProjectMapper;
import com.webank.wedatasphere.streamis.project.server.entity.StreamisProject;
import com.webank.wedatasphere.streamis.project.server.entity.request.CreateProjectRequest;
import com.webank.wedatasphere.streamis.project.server.exception.StreamisProjectErrorException;
import com.webank.wedatasphere.streamis.project.server.service.StreamisProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Description:
 */
@Service
public class StreamisProjectServiceImpl implements StreamisProjectService {


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisProjectServiceImpl.class);

    @Autowired
    private StreamisProjectMapper streamisProjectMapper;


    @Override
    public StreamisProject createProject(String username, CreateProjectRequest createProjectRequest) throws StreamisProjectErrorException {
        LOGGER.info("user {} starts to create project {}", username, createProjectRequest.getProjectName());
        if (!CollectionUtils.isEmpty(streamisProjectMapper.findProjectByName(createProjectRequest.getProjectName()))) {
            throw new StreamisProjectErrorException(600500, "the project name is exist");
        }
        StreamisProject streamisProject = new StreamisProject(createProjectRequest.getProjectName(), createProjectRequest.getDescription(), null);
        streamisProject.setCreateBy(username);
        streamisProject.setTags(createProjectRequest.getTags());
        streamisProjectMapper.createProject(streamisProject);
        LOGGER.info("user {} ends to create project {} and id is {}", streamisProject.getCreateBy(), streamisProject.getName(), streamisProject.getId());
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
    public void updateProject(UpdateStreamProjectRequest updateStreamProjectRequest) throws StreamisProjectErrorException {
        LOGGER.info("User {} begins to update project {}", updateStreamProjectRequest.updateBy(), updateStreamProjectRequest.projectName());
        List<Long> list = streamisProjectMapper.findProjectByName(updateStreamProjectRequest.projectName());
        if (!CollectionUtils.isEmpty(list) && list.get(0) != updateStreamProjectRequest.streamisProjectId()) {
            throw new StreamisProjectErrorException(600500, "the project name is exist");
        }
        StreamisProject streamisProject = new StreamisProject(updateStreamProjectRequest.projectName(), updateStreamProjectRequest.description(), updateStreamProjectRequest.updateBy());
        streamisProject.setId(updateStreamProjectRequest.streamisProjectId());
        streamisProject.setLastUpdateBy(updateStreamProjectRequest.updateBy());
        streamisProjectMapper.updateProject(streamisProject);
        LOGGER.info("user {} ends to update, project name is {} and id is {}",updateStreamProjectRequest.updateBy(),updateStreamProjectRequest.projectName(),updateStreamProjectRequest.streamisProjectId());
    }


    @Override
    public void deleteProject(DeleteStreamProjectRequest deleteStreamProjectRequest) throws StreamisProjectErrorException {
        streamisProjectMapper.deleteProjectByName(deleteStreamProjectRequest.projectName());
        LOGGER.info("delete project {}", deleteStreamProjectRequest.projectName());
    }
}
