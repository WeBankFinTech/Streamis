package com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket;

import com.webank.wedatasphere.streamis.jobmanager.log.server.config.StreamJobLogConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.server.exception.StreamJobLogException;
import org.apache.linkis.common.conf.CommonVars;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for job log bucket
 */
public class JobLogBucketConfig {

    @SuppressWarnings("unchecked")
    public JobLogBucketConfig(){
        try {
            Class<?> defaultBucketClass = Class.forName(Define.JOB_LOG_BUCKET_CLASS.getValue());
            if (JobLogBucket.class.isAssignableFrom(defaultBucketClass)){
                this.bucketClass = (Class<? extends JobLogBucket>) defaultBucketClass;
            }
        } catch (ClassNotFoundException e) {
            throw new StreamJobLogException.Runtime(-1, "Cannot find the bucket class, message: " + e.getMessage());
        }
    }

    /**
     * Bucket class
     */
    private Class<? extends JobLogBucket> bucketClass;

    /**
     * Attribute
     */
    protected Map<String, Object> attributes = new HashMap<>();

    /**
     * Max size of bucket active part (MB)
     */
    private long maxBucketActivePartSize = StreamJobLogConfig.BUCKET_MAX_ACTIVE_PART_SIZE.getValue();

    /**
     * The compress format used for bucket parts
     */
    private String bucketPartCompress = StreamJobLogConfig.BUCKET_PART_COMPRESS.getValue();

    /**
     * Max hold time in days for bucket part
     */
    private int bucketPartHoldTimeInDay = StreamJobLogConfig.BUCKET_PART_HOLD_DAY.getValue();

    /**
     * Layout pattern
     */
    private String LogLayOutPattern = StreamJobLogConfig.BUCKET_LAYOUT.getValue();

    public Class<? extends JobLogBucket> getBucketClass() {
        return bucketClass;
    }

    public void setBucketClass(Class<? extends JobLogBucket> bucketClass) {
        this.bucketClass = bucketClass;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public long getMaxBucketActivePartSize() {
        return maxBucketActivePartSize;
    }

    public void setMaxBucketActivePartSize(long maxBucketActivePartSize) {
        this.maxBucketActivePartSize = maxBucketActivePartSize;
    }

    public String getBucketPartCompress() {
        return bucketPartCompress;
    }

    public void setBucketPartCompress(String bucketPartCompress) {
        this.bucketPartCompress = bucketPartCompress;
    }

    public int getBucketPartHoldTimeInDay() {
        return bucketPartHoldTimeInDay;
    }

    public void setBucketPartHoldTimeInDay(int bucketPartHoldTimeInDay) {
        this.bucketPartHoldTimeInDay = bucketPartHoldTimeInDay;
    }

    public String getLogLayOutPattern() {
        return LogLayOutPattern;
    }

    public void setLogLayOutPattern(String logLayOutPattern) {
        LogLayOutPattern = logLayOutPattern;
    }


    public static final class Define{
        /**
         * Default bucket class
         */
        public static final CommonVars<String> JOB_LOG_BUCKET_CLASS = CommonVars.apply("wds.streamis.job.log.bucket.class", "com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.Log4j2JobLogBucket");
    }
}
