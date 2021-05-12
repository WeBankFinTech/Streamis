package com.webank.wedatasphere.streamis.jobmanager.manager.dao;

import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamProject;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author limeng
 */
public interface StreamProjectMapper {

    void instertProject(StreamProject streamProject);

    void updateProject(StreamProject streamProject);

    List<StreamProject> getByProjects(@Param("id") Long id,@Param("workspaceId") Long workspaceId,@Param("name") String name);

}
