package com.webank.wedatasphere.streamis.project.server.service;

import com.webank.wedatasphere.streamis.project.server.entity.CreateProjectPrivilege;
import com.webank.wedatasphere.streamis.project.server.entity.request.CreateProjectRequest;
import com.webank.wedatasphere.streamis.project.server.entity.request.UpdateProjectRequest;

public interface StreamisProjectPrivilegeService {

    CreateProjectPrivilege addProjectPrivilege(Long projectId, CreateProjectRequest createProjectRequest);

    void updateProjectPrivilege(Long projectId, UpdateProjectRequest updateProjectRequest);

    void deleteProjectPrivilege(Long projectId);

}
