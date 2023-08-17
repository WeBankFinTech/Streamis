package com.webank.wedatasphere.streamis.jobmanager.log.server.config;

import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.common.conf.TimeType;

/**
 * Store the configuration defined for job log
 */
public class StreamJobLogConfig {
    private StreamJobLogConfig(){}

    /**
     * Set the log restful api as no-auth
     */
    public static final CommonVars<Boolean> NO_AUTH_REST = CommonVars.apply("wds.stream.job.log.restful.no-auth", false);

    /**
     * The threshold of log storage
     */
    public static final CommonVars<Double> STORAGE_THRESHOLD = CommonVars.apply("wds.stream.job.log.storage.threshold", 0.9);

    /**
     * Max weight of storage context
     */
    public static final CommonVars<Integer> STORAGE_CONTEXT_MAX_WEIGHT = CommonVars.apply("wds.stream.job.log.storage.context.max-weight", 5);

    /**
     * Paths of storage context
     */
    public static final CommonVars<String> STORAGE_CONTEXT_PATHS = CommonVars.apply("wds.stream.job.log.storage.context.paths", "/data/stream/log");

    /**
     * Bucket monitor name
     */
    public static final CommonVars<String> BUCKET_MONITOR_NAME = CommonVars.apply("wds.stream.job.log.storage.bucket.monitor.name", "Log-Storage-Bucket-Monitor");

    /**
     * Bucket monitor interval
     */
    public static final CommonVars<TimeType> BUCKET_MONITOR_INTERVAL = CommonVars.apply("wds.stream.job.log.storage.bucket.monitor.interval", new TimeType("2m"));

    /**
     * Bucket max idle time
     */
    public static final CommonVars<TimeType> BUCKET_MAX_IDLE_TIME = CommonVars.apply("wds.stream.job.log.storage.bucket.max-idle-time", new TimeType("12h"));

    /**
     * Bucket root path
     */
    public static final CommonVars<String> BUCKET_ROOT_PATH = CommonVars.apply("wds.stream.job.log.storage.bucket.root-path", "/data/stream/log");
    /**
     * Max active part size in bucket
     */
    public static final CommonVars<Long> BUCKET_MAX_ACTIVE_PART_SIZE = CommonVars.apply("wds.stream.job.log.storage.bucket.max-active-part-size", 100L);

    /**
     * Compression of part in bucket
     */
    public static final CommonVars<String> BUCKET_PART_COMPRESS = CommonVars.apply("wds.stream.job.log.storage.bucket.part-compress", "gz");

    /**
     * Bucket layout
     */
    public static final CommonVars<String> BUCKET_LAYOUT = CommonVars.apply("wds.stream.job.log.storage.bucket.layout", "%msg");

    public static final CommonVars<Integer> BUCKET_PART_HOLD_DAY = CommonVars.apply("wds.stream.job.log.storage.bucket.part-hold-day", 30);
}
