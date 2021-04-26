package com.webank.wedatasphere.streamis.jobmanager.launcher.dao;

import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.ConfigKey;
import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.ConfigKeyValue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConfigMapper {

    List<ConfigKeyValue>  getConfigKeyValues(@Param("type") Integer type,@Param("jobId") Long jobId);

    ConfigKeyValue getConfigKeyValue(@Param("jobId") Long jobId,@Param("configkeyId") Long configkeyId);

    void deleteKeyValue(@Param("jobId") Long jobId,@Param("type") Integer type,@Param("configkeyId") Long configkeyId);

    List<ConfigKey>  getConfigKey();

    void insertValue(ConfigKeyValue configValue);


    void updateUserValue(ConfigKeyValue configValue);
}
