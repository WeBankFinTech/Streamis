package com.webank.wedatasphere.streamis.jobmanager.log.collector.config;

import org.apache.logging.log4j.core.Filter;

import java.util.Map;

/**
 * Config builder for appender
 */
public abstract class StreamisLogAppenderConfigBuilder {


    /**
     * Build method
     * @param applicationName application name
     * @param filter filter
     * @param senderConfig sender config
     * @return
     */
    public abstract StreamisLogAppenderConfig build(String applicationName,
                                             Filter filter, RpcLogSenderConfig senderConfig);

   abstract Map<String, String> loadConfigProps();
}
