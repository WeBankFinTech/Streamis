package com.webank.wedatasphere.streamis.project.server.dao;

import com.webank.wedatasphere.streamis.project.server.entity.StreamisProject;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Description:
 */
@Mapper
public interface StreamisProjectMapper {

    void createProject(StreamisProject streamisProject);

    List<Long> findProjectByName(String projectName);

    void deleteProjectByName(String projectName);

    void updateProject(StreamisProject streamisProject);
}
