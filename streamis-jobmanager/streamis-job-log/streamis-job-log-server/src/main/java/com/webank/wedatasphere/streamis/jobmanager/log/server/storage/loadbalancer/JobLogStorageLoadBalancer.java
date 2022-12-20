package com.webank.wedatasphere.streamis.jobmanager.log.server.storage.loadbalancer;

import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucketConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.context.JobLogStorageContext;

public interface JobLogStorageLoadBalancer {
    /**
     * Init method
     */
    void init();

    /**
     * The order
     * @return priority value
     */
    default int priority(){
        return -1;
    }

    /**
     * Choose storage context
     * @param bucketName bucket name
     * @param config bucket config
     * @return
     */
    JobLogStorageContext chooseContext(String bucketName, JobLogBucketConfig config);
}
