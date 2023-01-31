package com.webank.wedatasphere.streamis.jobmanager.entrypoint.entrypoint;

import com.webank.wedatasphere.streamis.jobmanager.entrypoint.config.StreamJobConfig;
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.exception.JobHeartbeatException;
import org.apache.commons.lang3.StringUtils;

public abstract class StreamJobEntrypoint {

    /**
     * @param config
     * @return
     */
    void register(StreamJobConfig config) throws JobHeartbeatException {
        throw new JobHeartbeatException(-1, "This method cannot be called, call the method of the subclass");
    }

    Boolean checkConfig(StreamJobConfig config) {
        if (StringUtils.isNoneBlank(config.getApplicationId()) &&
                StringUtils.isNoneBlank(config.getResourceManagerAddress()) &&
                StringUtils.isNoneBlank(config.getJobStatus()) &&
                StringUtils.isNoneBlank(config.getJobName()) &&
                StringUtils.isNoneBlank(config.getProjectName())) {
            return true;
        }
        return false;
    }
}
