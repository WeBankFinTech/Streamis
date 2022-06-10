package com.webank.wedatasphere.streamis.project.server.dao;

import com.webank.wedatasphere.streamis.project.server.entity.StreamisProjectPrivilege;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StreamisProjectPrivilegeMapper {

    void addProjectPrivilege(@Param("list") List<StreamisProjectPrivilege> projectPrivilegeList);

    List<StreamisProjectPrivilege> findProjectPrivilegeByProjectId(Long projectId);

    void deleteProjectPrivilegeByProjectId(Long projectId);

    void deleteProjectPrivilegeById(@Param("list") List<StreamisProjectPrivilege> projectPrivilegeList);

    List<StreamisProjectPrivilege> getProjectPrivilege(Long projectId, String userName);

    int selectPrivilegeCount(StreamisProjectPrivilege projectPrivilege);

    List<StreamisProjectPrivilege> findProjectPrivilegeByProjectIds(List<Long> projectId);
}
