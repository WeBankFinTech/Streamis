package com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket;

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
//            throw new StreamJobLogException.Runtime(-1, "", e);
        }
    }

    /**
     * Bucket class
     */
    private Class<? extends JobLogBucket> bucketClass;

    /**
     * Root path for bucket
     */
    private String bucketRootPath;

    /**
     * Attribute
     */
    protected Map<String, Object> attributes = new HashMap<>();

    /**
     * Max size of bucket active part
     */
    private int maxBucketActivePartSize;

    /**
     * Max number of bucket part
     */
    private int maxBucketPartNum;

    /**
     * The compress format used for bucket parts
     */
    private String bucketPartCompress;

    /**
     * Max hold time in minutes for bucket part
     */
    private long bucketPartHoldTimeInMin;

    /**
     * Layout pattern
     */
    private String LogLayOutPattern = "%msg%n";

    public Class<? extends JobLogBucket> getBucketClass() {
        return bucketClass;
    }

    public void setBucketClass(Class<? extends JobLogBucket> bucketClass) {
        this.bucketClass = bucketClass;
    }

    public String getBucketRootPath() {
        return bucketRootPath;
    }

    public void setBucketRootPath(String bucketRootPath) {
        this.bucketRootPath = bucketRootPath;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public int getMaxBucketActivePartSize() {
        return maxBucketActivePartSize;
    }

    public void setMaxBucketActivePartSize(int maxBucketActivePartSize) {
        this.maxBucketActivePartSize = maxBucketActivePartSize;
    }

    public int getMaxBucketPartNum() {
        return maxBucketPartNum;
    }

    public void setMaxBucketPartNum(int maxBucketPartNum) {
        this.maxBucketPartNum = maxBucketPartNum;
    }

    public String getBucketPartCompress() {
        return bucketPartCompress;
    }

    public void setBucketPartCompress(String bucketPartCompress) {
        this.bucketPartCompress = bucketPartCompress;
    }

    public long getBucketPartHoldTimeInMin() {
        return bucketPartHoldTimeInMin;
    }

    public void setBucketPartHoldTimeInMin(long bucketPartHoldTimeInMin) {
        this.bucketPartHoldTimeInMin = bucketPartHoldTimeInMin;
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
