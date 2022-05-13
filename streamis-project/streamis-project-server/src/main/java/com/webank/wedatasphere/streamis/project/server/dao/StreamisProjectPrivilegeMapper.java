package com.webank.wedatasphere.streamis.project.server.dao;

import com.webank.wedatasphere.streamis.project.server.entity.CreateProjectPrivilege;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StreamisProjectPrivilegeMapper {

    void addProjectPrivilege(CreateProjectPrivilege createProjectPrivilege);

    void deleteProjectPrivilegeByProjectId(Long projectId);

}
