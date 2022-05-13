package com.webank.wedatasphere.streamis.project.server.service.impl;

import com.webank.wedatasphere.streamis.project.server.dao.StreamisProjectPrivilegeMapper;
import com.webank.wedatasphere.streamis.project.server.entity.CreateProjectPrivilege;
import com.webank.wedatasphere.streamis.project.server.entity.request.CreateProjectRequest;
import com.webank.wedatasphere.streamis.project.server.entity.request.UpdateProjectRequest;
import com.webank.wedatasphere.streamis.project.server.service.StreamisProjectPrivilegeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StreamisProjectPrivilegeServiceImpl implements StreamisProjectPrivilegeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisProjectPrivilegeServiceImpl.class);

    @Autowired
    private StreamisProjectPrivilegeMapper streamisProjectPrivilegeMapper;

    // TODO wrap as privilege list to batch insert
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreateProjectPrivilege addProjectPrivilege(Long projectId, CreateProjectRequest createProjectRequest) {
        CreateProjectPrivilege createProjectPrivilege = new CreateProjectPrivilege(projectId,createProjectRequest.getReleaseUsers(),1);
        streamisProjectPrivilegeMapper.addProjectPrivilege(createProjectPrivilege);
        createProjectPrivilege = new CreateProjectPrivilege(projectId,createProjectRequest.getEditUsers(),2);
        streamisProjectPrivilegeMapper.addProjectPrivilege(createProjectPrivilege);
        createProjectPrivilege = new CreateProjectPrivilege(projectId,createProjectRequest.getAccessUsers(),3);
        streamisProjectPrivilegeMapper.addProjectPrivilege(createProjectPrivilege);
        LOGGER.info("project {} ends to add privilege", createProjectRequest.getProjectName());
        return createProjectPrivilege;
    }

    // TODO
    //  1) Select all project privileges 查询到改项目下所有的权限，用DSS传递过来的项目权限集减去该项目已有的权限集，得到需要插入的权限集。
    //  2）反过来用该项目已有的权限集减去DSS传递过来的项目权限集，得到需要删除的权限集。
    //  3）批量删除，批量插入。
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProjectPrivilege(Long projectId, UpdateProjectRequest updateProjectRequest) {
        deleteProjectPrivilege(projectId);
        CreateProjectPrivilege createProjectPrivilege = new CreateProjectPrivilege(projectId,updateProjectRequest.getReleaseUsers(),1);
        streamisProjectPrivilegeMapper.addProjectPrivilege(createProjectPrivilege);
        createProjectPrivilege = new CreateProjectPrivilege(projectId,updateProjectRequest.getEditUsers(),2);
        streamisProjectPrivilegeMapper.addProjectPrivilege(createProjectPrivilege);
        createProjectPrivilege = new CreateProjectPrivilege(projectId,updateProjectRequest.getAccessUsers(),3);
        streamisProjectPrivilegeMapper.addProjectPrivilege(createProjectPrivilege);
        LOGGER.info("project {} ends to update privilege", updateProjectRequest.getProjectName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProjectPrivilege(Long projectId) {
        streamisProjectPrivilegeMapper.deleteProjectPrivilegeByProjectId(projectId);
        LOGGER.info("projectId {} ends to delete privilege", projectId );
    }
}
