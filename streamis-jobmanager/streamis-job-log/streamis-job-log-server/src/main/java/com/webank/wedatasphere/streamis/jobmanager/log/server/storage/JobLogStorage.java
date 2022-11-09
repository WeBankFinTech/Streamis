package com.webank.wedatasphere.streamis.jobmanager.log.server.storage;

import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucket;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucketConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucketDriftPolicy;

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
     * Set bucket drift policy
     * @param bucketDriftPolicy bucket drift policy
     */
    void setBucketDriftPolicy(JobLogBucketDriftPolicy bucketDriftPolicy);
    /**
     * Init method
     */
    void init();

    /**
     * Destroy method
     */
    void destroy();
}
