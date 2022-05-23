package com.webank.wedatasphere.streamis.project.server.service.impl;

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
        List streamisAllPrivilegeList = streamisProjectPrivilegeMapper.findProjectPrivilegeByProjectId(dssPrivilegeList.get(0).getProjectId());
        List<StreamisProjectPrivilege> addPrivilegeList = (ArrayList)CollectionUtils.subtract(dssPrivilegeList, streamisAllPrivilegeList);
        List<StreamisProjectPrivilege> subPrivilegeList = (ArrayList)CollectionUtils.subtract(streamisAllPrivilegeList, dssPrivilegeList);
        addProjectPrivilege(addPrivilegeList);
        deleteProjectPrivilegeByProjectId(dssPrivilegeList.get(0).getProjectId());
        LOGGER.info("projectId {} ends to update privilege", dssPrivilegeList.get(0).getProjectId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProjectPrivilegeByProjectId(Long projectId) {
        streamisProjectPrivilegeMapper.deleteProjectPrivilegeByProjectId(projectId);
        LOGGER.info("projectId {} ends to delete privilege", projectId );
    }
}
