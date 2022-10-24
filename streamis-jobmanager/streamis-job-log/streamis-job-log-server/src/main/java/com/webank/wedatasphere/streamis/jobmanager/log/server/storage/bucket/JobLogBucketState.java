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

    /**
     * Write rate
     * @return rate
     */
    double getBucketWriteRate();

    /**
     * Bucket parts
     * @return number
     */
    int getBucketParts();

    /**
     * Last rite time
     * @return time
     */
    long getBucketWriteTime();
}
