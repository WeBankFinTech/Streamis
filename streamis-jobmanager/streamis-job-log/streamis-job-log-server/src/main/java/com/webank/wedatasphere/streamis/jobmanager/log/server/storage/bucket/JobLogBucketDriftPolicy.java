package com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket;

import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.context.JobLogStorageContext;

/**
 * Drift policy
 */
public interface JobLogBucketDriftPolicy {
    /**
     * Decide whether you should drift the bucket
     * @param bucket bucket
     * @return
     */
    boolean onPolicy(JobLogBucket bucket, JobLogStorageContext[] contexts);
}
