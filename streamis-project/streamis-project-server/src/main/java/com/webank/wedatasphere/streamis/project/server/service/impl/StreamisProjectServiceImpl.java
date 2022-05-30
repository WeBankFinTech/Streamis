package com.webank.wedatasphere.streamis.project.server.service.impl;

import com.webank.wedatasphere.streamis.project.server.dao.StreamisProjectMapper;
import com.webank.wedatasphere.streamis.project.server.entity.StreamisProject;
import com.webank.wedatasphere.streamis.project.server.entity.StreamisProjectPrivilege;
import com.webank.wedatasphere.streamis.project.server.exception.StreamisProjectErrorException;
import com.webank.wedatasphere.streamis.project.server.service.StreamisProjectPrivilegeService;
import com.webank.wedatasphere.streamis.project.server.service.StreamisProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    public StreamisProject createProject(StreamisProject streamisProject) throws StreamisProjectErrorException {
        LOGGER.info("user {} starts to create project {}", streamisProject.getCreateBy(), streamisProject.getName());
        if (!CollectionUtils.isEmpty(streamisProjectMapper.findProjectByName(streamisProject.getName()))) {
            throw new StreamisProjectErrorException(600500, "the project name is exist");
        }
        streamisProjectMapper.createProject(streamisProject);
        List<StreamisProjectPrivilege> projectPrivileges = streamisProject.getProjectPrivileges();
        for (StreamisProjectPrivilege privilege : projectPrivileges) privilege.setProjectId(streamisProject.getId());
        streamisProjectPrivilegeService.addProjectPrivilege(projectPrivileges);
        LOGGER.info("user {} ends to create project {} and id is {}", streamisProject.getCreateBy(), streamisProject.getName(), streamisProject.getId());
        return streamisProject;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProject(StreamisProject streamisProject) throws StreamisProjectErrorException {
        LOGGER.info("User {} begins to update project {}", streamisProject.getLastUpdateBy(), streamisProject.getName());
        List<Long> list = streamisProjectMapper.findProjectByName(streamisProject.getName());
        if (!CollectionUtils.isEmpty(list) && !list.get(0).equals(streamisProject.getId())) {
            throw new StreamisProjectErrorException(600500, "the project name is exist");
        }
        streamisProjectMapper.updateProject(streamisProject);
        streamisProjectPrivilegeService.updateProjectPrivilege(streamisProject.getProjectPrivileges());
        LOGGER.info("user {} ends to update, project name is {} and id is {}",streamisProject.getLastUpdateBy(),streamisProject.getName(),streamisProject.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProjectById(Long projectId) {
        streamisProjectMapper.deleteProjectById(projectId);
        streamisProjectPrivilegeService.deleteProjectPrivilegeByProjectId(projectId);
        LOGGER.info("delete projectId {}", projectId);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public StreamisProject queryProject(Long projectId) {
        return streamisProjectMapper.findProjectById(projectId);
    }
}
