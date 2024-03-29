package com.webank.wedatasphere.streamis.jobmanager.entrypoint;

import com.webank.wedatasphere.streamis.jobmanager.entrypoint.config.StreamJobConfig;
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.exception.JobHeartbeatException;
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.producer.StreamJobHeartbeatProducer;
import org.apache.commons.lang3.StringUtils;

public  interface  StreamJobEntrypoint {

    /**
     * @param config
     * @return
     */
    default void register(StreamJobConfig config, StreamJobHeartbeatProducer producer) throws JobHeartbeatException {
        throw new JobHeartbeatException(-1, "This method cannot be called, call the method of the subclass");
    }

    default Boolean checkConfig(StreamJobConfig config) {
       return  (StringUtils.isNoneBlank(config.getApplicationId()) &&
                StringUtils.isNoneBlank(config.getResourceManagerAddress()) &&
                StringUtils.isNoneBlank(config.getJobStatus()) &&
                StringUtils.isNoneBlank(config.getJobName()) &&
                StringUtils.isNoneBlank(config.getProjectName()));
    }
}
