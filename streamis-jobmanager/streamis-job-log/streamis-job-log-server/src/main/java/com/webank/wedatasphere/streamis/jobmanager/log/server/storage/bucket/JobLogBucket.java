package com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket;

/**
 * Job log bucket for streamis
 */
public interface JobLogBucket {

    /**
     * Bucket state
     * @return state
     */
    JobLogBucketState getBucketState();

    /**
     * Storage writer
     * @return storage writer
     */
    JobLogStorageWriter getBucketStorageWriter();

    /**
     * Close the bucket
     */
    void close();
}
