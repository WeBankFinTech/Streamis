package com.webank.wedatasphere.streamis.jobmanager.log.server.storage;

import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucket;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucketConfig;

/**
 * Storage of job log
 */
public interface JobLogStorage {

    /**
     * Create buckets
     * @param appName application name
     * @param bucketConfig bucket config
     * @return config
     */
    JobLogBucket getOrCreateBucket(String appName, JobLogBucketConfig bucketConfig);
}
