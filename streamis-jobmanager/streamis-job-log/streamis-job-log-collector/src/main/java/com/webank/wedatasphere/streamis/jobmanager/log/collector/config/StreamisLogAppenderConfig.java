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

    public static class Builder{
        private String applicationName;

        private Filter filter;

        private RpcLogSenderConfig rpcLogSenderConfig;

        public Builder(String applicationName, Filter filter,
                       RpcLogSenderConfig rpcLogSenderConfig){
            this.applicationName = applicationName;
            this.filter = filter;
            this.rpcLogSenderConfig = rpcLogSenderConfig;
        }

        StreamisLogAppenderConfig.Builder setAppName(){
            return null;
        }

        public StreamisLogAppenderConfig build(){
            return null;
        }
    }
    public String getApplicationName() {
        return applicationName;
    }

    public Filter getFilter() {
        return filter;
    }

    public RpcLogSenderConfig getSenderConfig() {
        return senderConfig;
    }

}
