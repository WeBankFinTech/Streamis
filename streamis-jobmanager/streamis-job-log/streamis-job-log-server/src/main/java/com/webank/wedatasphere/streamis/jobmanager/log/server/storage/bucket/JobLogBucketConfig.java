package com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for job log bucket
 */
public class JobLogBucketConfig {

    /**
     * Bucket class
     */
    private Class<? extends JobLogBucket> bucketClass;

    /**
     * Attribute
     */
    protected Map<String, Object> attributes = new HashMap<>();

    /**
     * Max size of bucket part
     */
    private int maxBucketPartSize;

    /**
     * Layout pattern
     */
    private String LayOutPattern;

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public int getMaxBucketPartSize() {
        return maxBucketPartSize;
    }

    public void setMaxBucketPartSize(int maxBucketPartSize) {
        this.maxBucketPartSize = maxBucketPartSize;
    }

    public String getLayOutPattern() {
        return LayOutPattern;
    }

    public void setLayOutPattern(String layOutPattern) {
        LayOutPattern = layOutPattern;
    }

    public static final class Define{

    }
}
