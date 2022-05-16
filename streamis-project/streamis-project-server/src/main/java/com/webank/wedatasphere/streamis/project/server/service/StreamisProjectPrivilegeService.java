package com.webank.wedatasphere.streamis.project.server.service;

import com.webank.wedatasphere.streamis.project.server.entity.StreamisProjectPrivilege;
import com.webank.wedatasphere.streamis.project.server.entity.request.CreateProjectRequest;
import com.webank.wedatasphere.streamis.project.server.entity.request.UpdateProjectRequest;

import java.util.List;

public interface StreamisProjectPrivilegeService {

    void addProjectPrivilege(List<StreamisProjectPrivilege> streamisProjectPrivilegeList);

    void updateProjectPrivilege(List<StreamisProjectPrivilege> streamisProjectPrivilegeList);

    void deleteProjectPrivilegeByProjectId(Long projectId);

}
