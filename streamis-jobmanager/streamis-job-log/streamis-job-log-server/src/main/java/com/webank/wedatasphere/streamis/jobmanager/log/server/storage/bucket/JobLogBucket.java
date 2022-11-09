package com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket;

import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.context.JobLogStorageContext;

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
     * Get storage context
     * @return context
     */
    JobLogStorageContext getStorageContext();
    /**
     * Bucket name
     * @return bucket name
     */
    String getBucketName();
    /**
     * Close the bucket
     */
    void close();
}
