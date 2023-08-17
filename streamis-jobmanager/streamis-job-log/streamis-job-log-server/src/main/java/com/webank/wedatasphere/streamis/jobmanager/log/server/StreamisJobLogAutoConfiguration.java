package com.webank.wedatasphere.streamis.jobmanager.log.server;

import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.JobLogStorage;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.StreamisJobLogStorage;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.StorageThresholdDriftPolicy;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.loadbalancer.RoundRobinLoadBalancer;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.loadbalancer.SimpleLoadBalancer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StreamisJobLogAutoConfiguration {

    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean(JobLogStorage.class)
    public JobLogStorage streamisJobLogStorage(){
        StreamisJobLogStorage jobLogStorage = new StreamisJobLogStorage();
        jobLogStorage.addLoadBalancer(new RoundRobinLoadBalancer());
        jobLogStorage.addLoadBalancer(new SimpleLoadBalancer());
        jobLogStorage.setBucketDriftPolicy(new StorageThresholdDriftPolicy());
        return jobLogStorage;
    }
}
