package com.webank.wedatasphere.streamis.project.server.service.impl;


import com.webank.wedatasphere.streamis.project.server.entity.StreamisProject;
import com.webank.wedatasphere.streamis.project.server.entity.request.CreateProjectRequest;
import com.webank.wedatasphere.streamis.project.server.exception.StreamisProjectErrorException;
import com.webank.wedatasphere.streamis.project.server.service.ProjectService;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by yangzhiyue on 2021/4/19
 * Description:
 */
@Service
public class ProjectServiceImpl implements ProjectService {





    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceImpl.class);



    @Override
    public StreamisProject createProject(String username, CreateProjectRequest createProjectRequest) throws StreamisProjectErrorException {
        LOGGER.info("user {} starts to create project {}", username, createProjectRequest.getProjectName());
        return null;
    }

    @Override
    public void updateProject() throws StreamisProjectErrorException {

    }


    @Override
    public void deleteProject() throws StreamisProjectErrorException {

    }
}
