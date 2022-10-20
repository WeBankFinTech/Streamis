package com.webank.wedatasphere.streamis.jobmanager.log.collector.config;

import org.apache.logging.log4j.core.Filter;

/**
 * Appender config
 */
public class StreamisLogAppenderConfig {

    private String applicationName;

    /**
     * Filter in log4j
     */
    private Filter filter;

    private RpcLogSenderConfig senderConfig;

    public StreamisLogAppenderConfig(String applicationName, Filter filter,
                                     RpcLogSenderConfig rpcLogSenderConfig){
        this.applicationName = applicationName;
        this.filter = filter;
        this.senderConfig = null != rpcLogSenderConfig? rpcLogSenderConfig : new RpcLogSenderConfig();
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public RpcLogSenderConfig getSenderConfig() {
        return senderConfig;
    }

    public void setSenderConfig(RpcLogSenderConfig senderConfig) {
        this.senderConfig = senderConfig;
    }
}
