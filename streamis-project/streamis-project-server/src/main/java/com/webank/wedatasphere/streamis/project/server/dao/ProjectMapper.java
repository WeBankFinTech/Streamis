package com.webank.wedatasphere.streamis.project.server.dao;

import com.webank.wedatasphere.streamis.project.server.entity.StreamisProject;
import org.apache.ibatis.annotations.Mapper;

/**
 * created by yangzhiyue on 2021/4/23
 * Description:
 */
@Mapper
public interface ProjectMapper {

    void insertProject(StreamisProject streamisProject);



}
