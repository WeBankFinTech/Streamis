package com.webank.wedatasphere.streamis.jobmanager.log.server.storage;

import com.webank.wedatasphere.streamis.jobmanager.launcher.dao.StreamJobConfMapper;
import com.webank.wedatasphere.streamis.jobmanager.log.server.config.StreamJobLogConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.server.exception.StreamJobLogException;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucket;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucketConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucketDriftPolicy;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucketState;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.context.*;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.loadbalancer.JobLogStorageLoadBalancer;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.utils.MemUtils;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.utils.RegularUtils;
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.StreamJobMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.webank.wedatasphere.streamis.jobmanager.log.server.config.StreamJobLogConfig.BUCKET_MONITOR_INTERVAL;

/**
 * Job log storage
 */
public class StreamisJobLogStorage implements JobLogStorage{

    private static final Logger logger = LoggerFactory.getLogger(StreamisJobLogStorage.class);

    /**
     * Storage context
     */
    private final List<JobLogStorageContext> storageContexts = new CopyOnWriteArrayList<>();

    /**
     * Drift policy
     */
    private JobLogBucketDriftPolicy bucketDriftPolicy;
    /**
     * Buckets
     */
    private final Map<String, JobLogBucket> buckets = new ConcurrentHashMap<>();

    /**
     * Context listeners
     */
    private final List<JobLogStorageContextListener> contextListeners = new ArrayList<>();

    /**
     * Load balancer
     */
    private final List<JobLogStorageLoadBalancer> loadBalancers = new ArrayList<>();

    /**
     * Constructor cache
     */
    private final Map<String, Constructor<?>> bucketConstructors = new ConcurrentHashMap<>();

    /**
     * To monitor the status of buckets
     */
    private Future<?> monitorThread;

    private StreamJobConfMapper streamJobConfMapper;
    private StreamJobMapper streamJobMapper;

    @Override
    public JobLogBucket getOrCreateBucket(String userName, String appName, JobLogBucketConfig bucketConfig,String productName) {
        String bucketName = toBucketName(userName, appName,productName);
        return buckets.computeIfAbsent(bucketName, name -> {
            // First to choose context
            JobLogStorageContext context = chooseStorageContext(bucketName, bucketConfig);
            if (null != context){
                Class<? extends JobLogBucket> bucketClass = bucketConfig.getBucketClass();
                if (Objects.nonNull(bucketClass)) {
                    Constructor<?> constructor = bucketConstructors.computeIfAbsent(bucketClass.getName(), className -> {
                        Constructor<?>[] constructors = bucketClass.getConstructors();
                        Constructor<?> matchConstructor = null;
                        for (Constructor<?> constructor1 : constructors) {
                            Class<?>[] inputParams = constructor1.getParameterTypes();
                            if (inputParams.length >= 3 && inputParams[0].equals(String.class)
                                    && inputParams[1].equals(JobLogStorageContext.class) && inputParams[2].equals(JobLogBucketConfig.class)) {
                                matchConstructor = constructor1;
                                break;
                            }
                        }
                        return matchConstructor;
                    });
                    if (Objects.nonNull(constructor)) {
                        try {
                            return (JobLogBucket) constructor.newInstance(bucketName, context, bucketConfig);
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            logger.warn("Cannot create storage log bucket from [{}]", bucketClass.getName(), e);
                        }
                    }
                }
            }
            return null;
        });
    }

    @Override
    public void setBucketDriftPolicy(JobLogBucketDriftPolicy bucketDriftPolicy) {
        this.bucketDriftPolicy = bucketDriftPolicy;
    }

    public void setStreamJobConfMapper(StreamJobConfMapper streamJobConfMapper){
        this.streamJobConfMapper = streamJobConfMapper;
    }

    public StreamJobConfMapper getStreamJobConfMapper(){
        return this.streamJobConfMapper;
    }

    public StreamJobMapper getStreamJobMapper(){
        return this.streamJobMapper;
    }

    public void setStreamJobMapper(StreamJobMapper streamJobMapper){
        this.streamJobMapper = streamJobMapper;
    }
    @Override
    public void addContextListener(JobLogStorageContextListener listener) {
        this.contextListeners.add(listener);
    }

    @Override
    public void addLoadBalancer(JobLogStorageLoadBalancer loadBalancer) {
        this.loadBalancers.add(loadBalancer);
        if (loadBalancer instanceof JobLogStorageContextListener){
            addContextListener((JobLogStorageContextListener) loadBalancer);
        }
    }

    @Override
    @PostConstruct
    public synchronized void init() throws StreamJobLogException {
        initStorageContexts(StringUtils.split(StreamJobLogConfig.STORAGE_CONTEXT_PATHS.getValue(), ","));
        onContextEvent(new ContextLaunchEvent(new ArrayList<>(this.storageContexts)));
        // Init load balancer
        initLoadBalancers();
        if (Objects.isNull(monitorThread)){
            monitorThread = Utils.defaultScheduler().scheduleAtFixedRate(() -> {
                String threadName = Thread.currentThread().getName();
                try {
                    Thread.currentThread().setName(StreamJobLogConfig.BUCKET_MONITOR_NAME.getValue());
                    long maxIdleTime = StreamJobLogConfig.BUCKET_MAX_IDLE_TIME.getValue().toLong();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    // Update the storage context
                    JobLogStorageContext[] contexts = this.storageContexts.toArray(new JobLogStorageContext[0]);
                    try {
                        updateContextWeight(contexts);
                        onContextEvent(new ContextRefreshAllEvent());
                    } catch (IOException e) {
                        logger.warn("Unable to calculate weight array of storage context list", e);
                    }
                    if (buckets.size() > 0) {
                        StringBuilder builder = new StringBuilder("Buckets(").append(buckets.size()).append(") in LogStorage: [\n");
                        buckets.forEach((bucketName, bucket) -> {
                            JobLogBucketState bucketState = bucket.getBucketState();
                            builder.append("bucket: [ name: ")
                                    .append(bucketName)
                                    .append(", path: ").append(bucketState.getBucketPath())
                                    .append(", parts: ").append(bucketState.getBucketParts())
                                    .append(", write-rate: ").append(bucketState.getBucketWriteRate()).append("/s")
                                    .append(", last-write-time: ").append(dateFormat.format(bucketState.getBucketWriteTime()))
                                    .append(" ]\n");
                            boolean closeBucket = false;
                            if (bucketState.getBucketWriteTime() + maxIdleTime <= System.currentTimeMillis()) {
                                logger.info("Close the idle bucket: [ name: {}, last-write-time: {} ]",
                                        bucketName, dateFormat.format(bucketState.getBucketWriteTime()));
                                closeBucket = true;
                            }
                            if (Objects.nonNull(bucketDriftPolicy) && bucketDriftPolicy.onPolicy(bucket, contexts)){
                                logger.info("Drift the bucket: [ name: {}, last-write-time: {} ]", bucketName,
                                        dateFormat.format(bucketState.getBucketWriteTime()));
                                closeBucket = true;
                            }
                            if (closeBucket) {
                                // Delete the bucket
                                // First to move the bucket from map, then close it
                                buckets.remove(bucketName);
                                bucket.close();
                            }
                        });
                        logger.info(builder.toString());
                    }
                } catch (Throwable e){
                    assert logger != null;
                    logger.warn("Some exception happened in monitor thread", e);
                  //Ignore
                } finally {
                    Thread.currentThread().setName(threadName);
                }

            },BUCKET_MONITOR_INTERVAL.getValue().toLong(), BUCKET_MONITOR_INTERVAL.getValue().toLong(), TimeUnit.MILLISECONDS);
        }
    }


    @Override
    @PreDestroy
    public void destroy() {
        // Fist to close all the bucket
        buckets.forEach((bucketName, bucket) -> bucket.close());
        if (null != monitorThread){
            monitorThread.cancel(true);
        }
    }

    /**
     * Choose storage context
     * @param bucketName bucket name
     * @param jobLogBucketConfig bucket config
     * @return storage context
     */
    private JobLogStorageContext chooseStorageContext(String bucketName, JobLogBucketConfig jobLogBucketConfig){
        JobLogStorageContext context;
        for(JobLogStorageLoadBalancer balancer : loadBalancers){
            context = balancer.chooseContext(bucketName, jobLogBucketConfig);
            if (null !=  context){
                return context;
            }
        }
        return null;
    }

    /**
     * Init load balancers
     */
    private void initLoadBalancers(){
        for(JobLogStorageLoadBalancer loadBalancer : this.loadBalancers){
            loadBalancer.init();
        }
        // Sort the load balancer
        this.loadBalancers.sort(Comparator.comparingInt(JobLogStorageLoadBalancer::priority).reversed());
    }
    /**
     * Init the storage context
     * @param storagePaths storage paths
     */
    private void initStorageContexts(String[] storagePaths) throws StreamJobLogException {
        logger.info("Init the storage context: [" + StringUtils.join(storagePaths, ",") + "]");
        for(String storagePath : storagePaths){
            if (StringUtils.isNotBlank(storagePath)) {
                this.storageContexts.add(new JobLogStorageContext(storagePath, 1.0));
            }
        }
        if (!this.storageContexts.isEmpty()) {
            int size = this.storageContexts.size();
            try {
                updateContextWeight(storageContexts.toArray(new JobLogStorageContext[size]));
            } catch (IOException e) {
                throw new StreamJobLogException(-1, "Unable to calculate weight array of storage context list", e);
            }
        }
    }

    private void updateContextWeight(JobLogStorageContext[] contexts) throws IOException {
        double[] weights = calculateContextWeight(contexts);
        StringBuilder builder = new StringBuilder("Update storage context weights:[\n");
        for(int i = 0 ; i < weights.length; i ++){
            JobLogStorageContext context = contexts[i];
            builder.append(context.getStorePath()).append(" => ").append(weights[i]);
            if (i != weights.length - 1){
                builder.append(", ");
            }
            context.setStoreWeight(weights[i]);
        }
        builder.append("\n]");
        logger.info(builder.toString());
    }
    /**
     * Calculate the base weight of storage context
     * @param contexts context array
     */
    private double[] calculateContextWeight(JobLogStorageContext[] contexts) throws IOException {
        double[] weights = new double[contexts.length];
        if (contexts.length > 0) {
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
                    logger.warn("The usage of storage context:[{}] reach the threshold: {} > {}, set the weight of it to 0",
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
        }
        return weights;
    }

    /**
     * Produce context event
     * @param event event
     */
    private void onContextEvent(JobLogStorageContextListener.ContextEvent event){
        for(JobLogStorageContextListener listener : contextListeners){
            listener.onContextEvent(event);
        }
    }
    /**
     * Bucket name
     * @param userName username
     * @param appName app name
     * @return bucket name
     */
    private String toBucketName(String userName, String appName,String productName){
        if (StringUtils.isBlank(productName)){
            return userName + "." + appName;
        } else {
            String[] arrs = RegularUtils.split(appName);
            return arrs[0] + ".product." + arrs[1];
        }
    }
}
