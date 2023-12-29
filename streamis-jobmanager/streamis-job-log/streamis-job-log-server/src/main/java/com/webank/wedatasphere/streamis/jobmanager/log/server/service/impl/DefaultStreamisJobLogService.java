package com.webank.wedatasphere.streamis.jobmanager.log.server.service.impl;

import com.webank.wedatasphere.streamis.jobmanager.log.entities.StreamisLogEvents;
import com.webank.wedatasphere.streamis.jobmanager.log.server.service.StreamisJobLogService;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.JobLogStorage;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucket;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucketConfig;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Default implement
 */
@Service
public class DefaultStreamisJobLogService implements StreamisJobLogService {

    @Resource
    private JobLogStorage jobLogStorage;

    private JobLogBucketConfig jobLogBucketConfig;

    @PostConstruct
    public void init(){
        jobLogBucketConfig = new JobLogBucketConfig();
    }
    @Override
    public void store(String user, StreamisLogEvents events) {
        JobLogBucket jobLogBucket = jobLogStorage.getOrCreateBucket(user, events.getAppName(), jobLogBucketConfig);
        // If cannot get log bucket, drop the events
        if (null != jobLogBucket){
            jobLogBucket.getBucketStorageWriter().write(events);
        }
    }
}
