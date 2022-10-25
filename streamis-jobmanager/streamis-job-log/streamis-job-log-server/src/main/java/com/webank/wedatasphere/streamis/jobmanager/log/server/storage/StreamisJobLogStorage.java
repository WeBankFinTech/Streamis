package com.webank.wedatasphere.streamis.jobmanager.log.server.storage;

import com.webank.wedatasphere.streamis.jobmanager.log.server.config.StreamJobLogConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucket;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucketConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucketState;
import org.apache.linkis.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.webank.wedatasphere.streamis.jobmanager.log.server.config.StreamJobLogConfig.BUCKET_MONITOR_INTERVAL;

/**
 * Job log storage
 */
@Component
public class StreamisJobLogStorage implements JobLogStorage{

    private static final Logger LOG = LoggerFactory.getLogger(StreamisJobLogStorage.class);
    /**
     * Buckets
     */
    private final Map<String, JobLogBucket> buckets = new ConcurrentHashMap<>();

    /**
     * Constructor cache
     */
    private final Map<String, Constructor<?>> bucketConstructors = new ConcurrentHashMap<>();
    /**
     * To monitor the status of buckets
     */
    private Future<?> monitorThread;

    @Override
    public JobLogBucket getOrCreateBucket(String userName, String appName, JobLogBucketConfig bucketConfig) {
        String bucketName = toBucketName(userName, appName);
        return buckets.computeIfAbsent(bucketName, name -> {
            Class<? extends JobLogBucket> bucketClass = bucketConfig.getBucketClass();
            if (Objects.nonNull(bucketClass)) {
                Constructor<?> constructor = bucketConstructors.computeIfAbsent(bucketClass.getName(), className -> {
                    Constructor<?>[] constructors = bucketClass.getConstructors();
                    Constructor<?> matchConstructor = null;
                    for (Constructor<?> constructor1 : constructors) {
                        Class<?>[] inputParams = constructor1.getParameterTypes();
                        if (inputParams.length >= 2 && inputParams[0].equals(String.class)
                                && inputParams[1].equals(JobLogBucketConfig.class)) {
                            matchConstructor = constructor1;
                            break;
                        }
                    }
                    return matchConstructor;
                });
                if (Objects.nonNull(constructor)) {
                    try {
                        return (JobLogBucket) constructor.newInstance(bucketName, bucketConfig);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        LOG.warn("Cannot create storage log bucket from [{}]", bucketClass.getName(), e);
                    }
                }
            }
            return null;
        });
    }

    @Override
    @PostConstruct
    public synchronized void init() {
        if (Objects.isNull(monitorThread)){
            monitorThread = Utils.defaultScheduler().scheduleAtFixedRate(() -> {
                Thread.currentThread().setName(StreamJobLogConfig.BUCKET_MONITOR_NAME.getValue());
                long maxIdleTime = StreamJobLogConfig.BUCKET_MAX_IDLE_TIME.getValue().toLong();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (buckets.size() > 0) {
                    StringBuilder builder = new StringBuilder("Buckets in LogStorage: [\n");
                    buckets.forEach((bucketName, bucket) -> {
                        JobLogBucketState bucketState = bucket.getBucketState();
                        builder.append("bucket: [ name: ")
                                .append(bucketName)
                                .append(", path: ").append(bucketState.getBucketPath())
                                .append(", parts: ").append(bucketState.getBucketParts())
                                .append(", write-rate: ").append(bucketState.getBucketWriteRate()).append("/s")
                                .append(", last-write-time: ").append(dateFormat.format(bucketState.getBucketWriteTime()))
                                .append(" ]\n");
                        if (bucketState.getBucketWriteTime() + maxIdleTime <= System.currentTimeMillis()){
                            LOG.info("Close the idle bucket: [ name: {}, last-write-time: {} ]",
                                    bucketName, dateFormat.format(bucketState.getBucketWriteTime()));
                            bucket.close();
                            // Delete the bucket
                            buckets.remove(bucketName);
                        }

                    });
                    LOG.info(builder.toString());
                }

            },BUCKET_MONITOR_INTERVAL.getValue().toLong(), BUCKET_MONITOR_INTERVAL.getValue().toLong(), TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Bucket name
     * @param userName username
     * @param appName app name
     * @return bucket name
     */
    private String toBucketName(String userName, String appName){
        return userName + "." + appName;
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
}
