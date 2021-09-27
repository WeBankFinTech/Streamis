package com.webank.wedatasphere.streamis.jobmanager.manager.dao;

import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJobRunRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StreamJobRunRelationMapper {

    List<StreamJobRunRelation> getJobRunRelationList(@Param("jobId") Long jobId);

    void insertJobRunRelation(StreamJobRunRelation streamJobRunRelation);

    void updateJobRunRelation(StreamJobRunRelation streamJobRunRelation);

    void deleteJobRunRelation(@Param("id") Long id);
}
