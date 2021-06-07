package com.webank.wedatasphere.streamis.jobmanager.manager.dao;

import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author limeng
 */
public interface StreamTaskMapper {

    void insertTask(StreamTask streamTask);

    void updateTask(StreamTask streamTask);

    List<StreamTask> getByJobIds(@Param("jobId") Long jobId,@Param("version") String version);

    StreamTask getTaskById(@Param("id") Long id);

    List<StreamTask> getTasksByStatus(List<Integer> status);
}
