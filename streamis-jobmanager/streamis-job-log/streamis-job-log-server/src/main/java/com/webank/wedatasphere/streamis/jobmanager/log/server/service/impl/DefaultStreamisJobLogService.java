package com.webank.wedatasphere.streamis.jobmanager.log.server.service.impl;

import com.webank.wedatasphere.streamis.jobmanager.log.entities.StreamisLogEvents;
import com.webank.wedatasphere.streamis.jobmanager.log.server.service.StreamisJobLogService;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.JobLogStorage;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucket;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucketConfig;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJob;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implement
 */
@Service
public class DefaultStreamisJobLogService implements StreamisJobLogService {

    @Resource
    private JobLogStorage jobLogStorage;

    private JobLogBucketConfig jobLogBucketConfig;

    private Map<Long,String> productNameCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
        jobLogBucketConfig = new JobLogBucketConfig();
        productNameCache = new ConcurrentHashMap<>();
    }
    @Override
    public void store(String user, StreamisLogEvents events,String productName) {
        JobLogBucket jobLogBucket = jobLogStorage.getOrCreateBucket(user, events.getAppName(), jobLogBucketConfig,productName);
        // If cannot get log bucket, drop the events
        if (null != jobLogBucket){
            jobLogBucket.getBucketStorageWriter().write(events);
        }
    }
    @Override
    public String getProductName(String projectName,String jobName,String value) {
        StreamJob job = jobLogStorage.getStreamJobMapper().getCurrentJob(projectName, jobName);
        if (job == null){
            return null;
        }
        Long jobId = job.getId();
        if (productNameCache.containsKey(jobId)){
            return productNameCache.get(jobId);
        }else{
            String productName = jobLogStorage.getStreamJobConfMapper().getRawConfValue(jobId,value);
            if(StringUtils.isBlank(productName)){
                return null;
            }
            productNameCache.put(jobId,productName);
            return productName;
        }
    }
}
