package com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket;

/**
 * State of log bucket
 */
public interface JobLogBucketState {

    /**
     * Bucket path
     * @return path
     */
    String getBucketPath();

    double getBucketWriteRate();

    int getBucketParts();

    int getBucketWriteTime();
}
