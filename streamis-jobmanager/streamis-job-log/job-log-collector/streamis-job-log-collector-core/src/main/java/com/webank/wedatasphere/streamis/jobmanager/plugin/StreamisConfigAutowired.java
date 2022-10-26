package com.webank.wedatasphere.streamis.jobmanager.plugin;

import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.StreamisLogAppenderConfig;

/**
 * Streamis config autowired
 */
public interface StreamisConfigAutowired {

    /**
     * Log appender config
     * @param builder builder
     */
    StreamisLogAppenderConfig logAppenderConfig(StreamisLogAppenderConfig.Builder builder) throws Exception;
}
