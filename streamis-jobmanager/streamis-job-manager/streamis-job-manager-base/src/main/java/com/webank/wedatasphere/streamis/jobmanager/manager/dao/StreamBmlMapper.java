package com.webank.wedatasphere.streamis.jobmanager.manager.dao;

import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamBml;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamBmlVersion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StreamBmlMapper {

    void insertBml(StreamBml streamBml);

    void updateBml(StreamBml streamBml);

    List<StreamBml> getStreamBmlById(@Param("id") Long id);

    void insertBmlVersion(StreamBmlVersion streamBmlVersion);

    void updateBmlVersion(StreamBmlVersion streamBmlVersion);

    List<StreamBmlVersion> getStreamBmlVersionById(@Param("bmlId") Long bmlId,@Param("version") String version);

}
