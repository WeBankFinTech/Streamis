package com.webank.wedatasphere.streamis.jobmanager.log.server.storage;

import com.webank.wedatasphere.streamis.jobmanager.launcher.dao.StreamJobConfMapper;
import com.webank.wedatasphere.streamis.jobmanager.log.server.exception.StreamJobLogException;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucket;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucketConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucketDriftPolicy;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.context.JobLogStorageContextListener;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.loadbalancer.JobLogStorageLoadBalancer;
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.StreamJobMapper;

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
    JobLogBucket getOrCreateBucket(String userName, String appName, JobLogBucketConfig bucketConfig,String productName);

    /**
     * Set bucket drift policy
     * @param bucketDriftPolicy bucket drift policy
     */
    void setBucketDriftPolicy(JobLogBucketDriftPolicy bucketDriftPolicy);

    /**
     * Add context listener
     * @param listener listener
     */
    void addContextListener(JobLogStorageContextListener listener);

    /**
     * Add load balancer
     * @param loadBalancer load balancer
     */
    void addLoadBalancer(JobLogStorageLoadBalancer loadBalancer);
    /**
     * Init method
     */
    void init() throws StreamJobLogException;

    /**
     * Destroy method
     */
    void destroy();

    void setStreamJobConfMapper(StreamJobConfMapper streamJobConfMapper);

    StreamJobConfMapper getStreamJobConfMapper();

    void setStreamJobMapper(StreamJobMapper streamJobMapper);

    StreamJobMapper getStreamJobMapper();

}
