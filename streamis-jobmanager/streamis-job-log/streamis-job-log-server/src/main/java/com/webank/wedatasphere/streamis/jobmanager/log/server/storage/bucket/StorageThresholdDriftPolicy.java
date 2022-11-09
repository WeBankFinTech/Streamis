package com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket;

import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.context.JobLogStorageContext;

public class StorageThresholdDriftPolicy implements JobLogBucketDriftPolicy{
    @Override
    public boolean onPolicy(JobLogBucket bucket, JobLogStorageContext[] contexts) {
        JobLogStorageContext bucketContext = bucket.getStorageContext();
        // Means that the storage context is not healthy
        if (bucketContext.getStoreWeight() <= 0){
            // Find the available context
            boolean hasRest = false;
            for(JobLogStorageContext context : contexts){
                if (context.getStoreWeight() > 0){
                    hasRest = true;
                    break;
                }
            }
            return hasRest;
        }
        return false;
    }
}
