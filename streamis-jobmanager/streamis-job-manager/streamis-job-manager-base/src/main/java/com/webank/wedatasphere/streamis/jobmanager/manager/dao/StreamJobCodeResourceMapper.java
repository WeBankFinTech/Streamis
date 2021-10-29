package com.webank.wedatasphere.streamis.jobmanager.manager.dao;

import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJobCodeResource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StreamJobCodeResourceMapper {

    List<StreamJobCodeResource> getJobCodeList(@Param("jobVersionId") Long jobVersionId,@Param("bmlId") Long bmlId);

    void insert(StreamJobCodeResource streamJobCodeResource);

    void update(StreamJobCodeResource streamJobCodeResource);
}
