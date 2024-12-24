package com.webank.wedatasphere.streamis.jobmanager.log.server;

import com.webank.wedatasphere.streamis.jobmanager.launcher.dao.StreamJobConfMapper;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.JobLogStorage;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.StreamisJobLogStorage;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.StorageThresholdDriftPolicy;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.loadbalancer.RoundRobinLoadBalancer;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.loadbalancer.SimpleLoadBalancer;
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.StreamJobMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class StreamisJobLogAutoConfiguration {

    @Resource
    private StreamJobConfMapper streamJobConfMapper;

    @Resource
    private StreamJobMapper streamJobMapper;

    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean(JobLogStorage.class)
    public JobLogStorage streamisJobLogStorage(){
        StreamisJobLogStorage jobLogStorage = new StreamisJobLogStorage();
        jobLogStorage.addLoadBalancer(new RoundRobinLoadBalancer());
        jobLogStorage.addLoadBalancer(new SimpleLoadBalancer());
        jobLogStorage.setBucketDriftPolicy(new StorageThresholdDriftPolicy());
        jobLogStorage.setStreamJobConfMapper(streamJobConfMapper);
        jobLogStorage.setStreamJobMapper(streamJobMapper);
        return jobLogStorage;
    }
}
