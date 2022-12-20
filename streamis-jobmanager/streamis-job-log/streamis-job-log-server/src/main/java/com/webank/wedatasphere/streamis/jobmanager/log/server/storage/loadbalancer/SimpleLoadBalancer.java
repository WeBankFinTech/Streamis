package com.webank.wedatasphere.streamis.jobmanager.log.server.storage.loadbalancer;

import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucketConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.context.ContextDownEvent;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.context.ContextLaunchEvent;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.context.JobLogStorageContext;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.context.JobLogStorageContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple load balancer
 */
public class SimpleLoadBalancer implements JobLogStorageLoadBalancer, JobLogStorageContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleLoadBalancer.class);

    private final List<JobLogStorageContext> contexts = new ArrayList<>();

    private final SecureRandom random = new SecureRandom();
    @Override
    public void onContextEvent(ContextEvent event) {
        if (event instanceof ContextLaunchEvent){
            contexts.addAll(((ContextLaunchEvent) event).getContextList());
        } else if (event instanceof ContextDownEvent){
            contexts.removeIf(context -> context.getId().equals(((ContextDownEvent) event).getContextId()));
        }
    }

    @Override
    public void init() {

    }

    @Override
    public JobLogStorageContext chooseContext(String bucketName, JobLogBucketConfig config) {
        JobLogStorageContext context = randomSelectContext(this.contexts);
        if (null != context){
            LOG.info("Random chosen context: {} for bucket: {}", context.getStorePath(), bucketName);
        }
        return context;
    }

    private JobLogStorageContext randomSelectContext(List<JobLogStorageContext> candidates){
        return candidates.get(random.nextInt(candidates.size()));
    }
}
