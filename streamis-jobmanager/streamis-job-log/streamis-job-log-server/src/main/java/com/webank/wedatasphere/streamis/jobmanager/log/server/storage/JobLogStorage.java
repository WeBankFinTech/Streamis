package com.webank.wedatasphere.streamis.jobmanager.log.server.storage;

import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucket;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucketConfig;

/**
 * Storage of job log
 */
public interface JobLogStorage {

    /**
     * Create buckets
     * @param userName user own
     * @param appName application name
     * @param bucketConfig bucket config
     * @return config
     */
    JobLogBucket getOrCreateBucket(String userName, String appName, JobLogBucketConfig bucketConfig);

    /**
     * Init method
     */
    void init();

    /**
     * Destroy method
     */
    void destroy();
}
