package com.webank.wedatasphere.streamis.jobmanager.log.server;

import com.webank.wedatasphere.streamis.jobmanager.log.server.config.StreamJobLogConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.server.exception.StreamJobLogException;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.JobLogStorage;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.StreamisJobLogStorage;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucket;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucketConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.StorageThresholdDriftPolicy;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.context.JobLogStorageContext;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.loadbalancer.RoundRobinLoadBalancer;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.loadbalancer.SimpleLoadBalancer;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.utils.MemUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.common.conf.BDPConfiguration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/*
public class JobLogStorageTest {

    private static final Logger logger = LoggerFactory.getLogger(JobLogStorageTest.class);
    @Test
    public void storageContext() throws IOException {
        URL url = JobLogStorageTest.class.getResource("/");
        if (null != url){
            JobLogStorageContext context = new JobLogStorageContext(url.getPath(), 1.0d);
            logger.info("disk total(bytes):  " + context.getTotalSpace());
            logger.info("disk total(gb):     " + MemUtils.convertToGB(context.getTotalSpace(), "B"));
            logger.info("disk usable(bytes): " + context.getUsableSpace());
            logger.info("disk usable(gb):    " + MemUtils.convertToGB(context.getUsableSpace(), "B"));
        }
    }
    @Test
    public void calculateWeight() throws IOException {
        JobLogStorageContext candidate1 = new JobLogStorageContext(Objects.requireNonNull(JobLogStorage.class.getResource("/"))
                .getPath(), 1.0d);
        JobLogStorageContext candidate2 = new JobLogStorageContext(Objects.requireNonNull(JobLogStorage.class.getResource("/"))
                .getPath(), 1.0d);
        JobLogStorageContext[] contexts = new JobLogStorageContext[]{candidate1, candidate2};
        double[] weights = new double[contexts.length];
        int maxNormalizeWt = StreamJobLogConfig.STORAGE_CONTEXT_MAX_WEIGHT.getValue();
        double storageThreshold = StreamJobLogConfig.STORAGE_THRESHOLD.getValue();
        if (maxNormalizeWt < 1){
            maxNormalizeWt = 1;
        }
        double maxWeight = Double.MIN_VALUE;
        double minWeight = Double.MAX_VALUE;
        int i = 0;
        for (; i < weights.length; i++) {
            JobLogStorageContext context = contexts[0];
            long usableSpace = context.getUsableSpace();
            long totalSpace = context.getTotalSpace();
            double usage = (double)(totalSpace - usableSpace) / (double)totalSpace;
            double weight = 0d;
            if (usage >= storageThreshold){
                logger.info("The usage of storage context:[{}] reach the threshold: {} > {}, set the weight of it to 0",
                        context.getStorePath(), usage, storageThreshold);
            } else {
                long freeSpaceInGB = MemUtils.convertToGB(usableSpace, "B");
                if (freeSpaceInGB <= 0) {
                    freeSpaceInGB = 1;
                }
                weight = context.getScore() * (double) freeSpaceInGB;
            }
            weights[i] = weight;
            if (weight > maxWeight){
                maxWeight = weight;
            }
            if (weight < minWeight){
                minWeight = weight;
            }
        }
        double sub = maxWeight - minWeight;
        i = i - 1;
        for (; i >= 0; i--){
            weights[i] = (sub > 0? (maxNormalizeWt - 1) * (weights[i] - minWeight) * sub : 0) + 1;
        }
        logger.info(StringUtils.join(weights, '|'));
    }

    @Test
    public void startLogStorage() throws InterruptedException, StreamJobLogException {
        BDPConfiguration.set("wds.stream.job.log.storage.context.paths", Objects.requireNonNull(JobLogStorage.class.getResource("/"))
                .getPath());
        JobLogStorage storage = createJobLogStorage();
        storage.init();
        JobLogBucket bucket = storage.getOrCreateBucket("hadoop", "test-app", new JobLogBucketConfig());
        bucket.getBucketStorageWriter().write("Hello world");
        Thread.sleep(1000);
        storage.destroy();
    }
    private JobLogStorage createJobLogStorage(){
        StreamisJobLogStorage jobLogStorage = new StreamisJobLogStorage();
        jobLogStorage.addLoadBalancer(new RoundRobinLoadBalancer());
        jobLogStorage.addLoadBalancer(new SimpleLoadBalancer());
        jobLogStorage.setBucketDriftPolicy(new StorageThresholdDriftPolicy());
        return jobLogStorage;
    }
}
*/
