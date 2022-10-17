package com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket;

/**
 * Factory of creating job log bucket
 */
public interface JobLogBucketFactory {

    /**
     * Create bucket
     * @param jobName job name
     * @param config bucket config
     * @return
     */
    JobLogBucket createBucket(String jobName, JobLogBucketConfig config);
}
