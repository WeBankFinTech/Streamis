package com.webank.wedatasphere.streamis.project.server.dao;

import com.webank.wedatasphere.streamis.project.server.entity.StreamisProjectPrivilege;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StreamisProjectPrivilegeMapper {

    void addProjectPrivilege(@Param("list") List<StreamisProjectPrivilege> projectPrivilegeList);

    List findProjectPrivilegeByProjectId(Long projectId);

    void deleteProjectPrivilegeByProjectId(Long projectId);

}