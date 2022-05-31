package com.webank.wedatasphere.streamis.project.server.service.impl;

import com.webank.wedatasphere.streamis.project.server.constant.ProjectUserPrivilegeEnum;
import com.webank.wedatasphere.streamis.project.server.dao.StreamisProjectPrivilegeMapper;
import com.webank.wedatasphere.streamis.project.server.entity.StreamisProjectPrivilege;
import com.webank.wedatasphere.streamis.project.server.service.StreamisProjectPrivilegeService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class StreamisProjectPrivilegeServiceImpl implements StreamisProjectPrivilegeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisProjectPrivilegeServiceImpl.class);

    @Autowired
    private StreamisProjectPrivilegeMapper streamisProjectPrivilegeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProjectPrivilege(List<StreamisProjectPrivilege> projectPrivilegeList) {
        if(CollectionUtils.isEmpty(projectPrivilegeList)) {
            return;
        }
        streamisProjectPrivilegeMapper.addProjectPrivilege(projectPrivilegeList);
        LOGGER.info("project ends to add privilege");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProjectPrivilege(List<StreamisProjectPrivilege> dssPrivilegeList) {
        if(CollectionUtils.isEmpty(dssPrivilegeList)) {
            return;
        }
        List<StreamisProjectPrivilege> streamisAllPrivilegeList = streamisProjectPrivilegeMapper.findProjectPrivilegeByProjectId(dssPrivilegeList.get(0).getProjectId());
        List<StreamisProjectPrivilege> addPrivilegeList = (ArrayList<StreamisProjectPrivilege>)(CollectionUtils.subtract(dssPrivilegeList, streamisAllPrivilegeList));
        List<StreamisProjectPrivilege> delPrivilegeList = (ArrayList<StreamisProjectPrivilege>) CollectionUtils.subtract(streamisAllPrivilegeList, dssPrivilegeList);
        if(!CollectionUtils.isEmpty(addPrivilegeList)) {
            streamisProjectPrivilegeMapper.addProjectPrivilege(addPrivilegeList);
        }
        if(!CollectionUtils.isEmpty(delPrivilegeList)) {
            streamisProjectPrivilegeMapper.deleteProjectPrivilege(delPrivilegeList);
        }
        LOGGER.info("projectId {} ends to update privilege", dssPrivilegeList.get(0).getProjectId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProjectPrivilegeByProjectId(Long projectId) {
        streamisProjectPrivilegeMapper.deleteProjectPrivilegeByProjectId(projectId);
        LOGGER.info("projectId {} ends to delete privilege", projectId );
    }

    @Override
    public List<StreamisProjectPrivilege> getProjectPrivilege(Long projectId, String username) {
        return streamisProjectPrivilegeMapper.getProjectPrivilege(projectId, username);
    }

    @Override
    public boolean hasReleaseProjectPrivilege(Long projectId, String username) {
        StreamisProjectPrivilege streamisProjectPrivilege = new StreamisProjectPrivilege(projectId, username, ProjectUserPrivilegeEnum.RELEASE.getRank());
        long count = streamisProjectPrivilegeMapper.selectPrivilegeCount(streamisProjectPrivilege);
        return count != 0;
    }

    @Override
    public boolean hasEditProjectPrivilege(Long projectId, String username) {
        StreamisProjectPrivilege streamisProjectPrivilege = new StreamisProjectPrivilege(projectId, username, ProjectUserPrivilegeEnum.EDIT.getRank());
        long count = streamisProjectPrivilegeMapper.selectPrivilegeCount(streamisProjectPrivilege);
        return count != 0;
    }

    @Override
    public boolean hasAccessProjectPrivilege(Long projectId, String username) {
        StreamisProjectPrivilege streamisProjectPrivilege = new StreamisProjectPrivilege(projectId, username, ProjectUserPrivilegeEnum.ACCESS.getRank());
        long count = streamisProjectPrivilegeMapper.selectPrivilegeCount(streamisProjectPrivilege);
        return count != 0;
    }
}
