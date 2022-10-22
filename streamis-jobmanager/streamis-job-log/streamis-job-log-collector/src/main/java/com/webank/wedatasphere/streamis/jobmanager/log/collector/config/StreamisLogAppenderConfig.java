package com.webank.wedatasphere.streamis.jobmanager.log.collector.config;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.filter.CompositeFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Appender config
 */
public class StreamisLogAppenderConfig {

    private final String applicationName;

    /**
     * Filter in log4j
     */
    private final Filter filter;

    private final RpcLogSenderConfig senderConfig;

    StreamisLogAppenderConfig(String applicationName, Filter filter,
                                     RpcLogSenderConfig rpcLogSenderConfig){
        this.applicationName = applicationName;
        this.filter = filter;
        this.senderConfig = null != rpcLogSenderConfig? rpcLogSenderConfig : new RpcLogSenderConfig();
    }

    public static class Builder{
        /**
         * Application name
         */
        private String applicationName;

        /**
         * Filter rules
         */
        private final List<Filter> filters = new ArrayList<>();

        /**
         * Sender config
         */
        private final RpcLogSenderConfig rpcLogSenderConfig;

        public Builder(String applicationName, Filter filter,
                       RpcLogSenderConfig rpcLogSenderConfig){
            this.applicationName = applicationName;
            this.filters.add(filter);
            this.rpcLogSenderConfig = Optional.ofNullable(rpcLogSenderConfig).orElse(new RpcLogSenderConfig());
        }

        /**
         * Set application name
         * @param applicationName application name
         * @return builder
         */
        StreamisLogAppenderConfig.Builder setAppName(String applicationName){
            this.applicationName = applicationName;
            return this;
        }

        /**
         * Set filter
         * @param filter filter
         * @return builder
         */
        StreamisLogAppenderConfig.Builder setFilter(Filter filter){
            this.filters.clear();
            this.filters.add(filter);
            return this;
        }

        /**
         * Append filter
         * @param filter filter
         * @return builder
         */
        StreamisLogAppenderConfig.Builder withFilter(Filter filter){
            filters.add(filter);
            return this;
        }

        /**
         * Rpc connect timeout
         * @param connectionTimeout connection timeout
         * @return builder
         */
        StreamisLogAppenderConfig.Builder setRpcConnTimeout(int connectionTimeout){
            this.rpcLogSenderConfig.setConnectionTimeout(connectionTimeout);
            return this;
        }

        /**
         * Rpc socket timeout
         * @param socketTimeout socket timeout
         * @return builder
         */
        StreamisLogAppenderConfig.Builder setRpcSocketTimeout(int socketTimeout){
            this.rpcLogSenderConfig.setSocketTimeout(socketTimeout);
            return this;
        }

        /**
         * Rpc send retry count
         * @param sendRetryCnt send retry count
         * @return builder
         */
        StreamisLogAppenderConfig.Builder setRpcSendRetryCnt(int sendRetryCnt){
            this.rpcLogSenderConfig.setSendRetryCnt(sendRetryCnt);
            return this;
        }

        /**
         * Rpc server recovery time in seconds
         * @param serverRecoveryTimeInSec server recovery time
         * @return builder
         */
        StreamisLogAppenderConfig.Builder setRpcServerRecoveryTimeInSec(int serverRecoveryTimeInSec){
            this.rpcLogSenderConfig.setServerRecoveryTimeInSec(serverRecoveryTimeInSec);
            return this;
        }

        /**
         * Rpc max delay time in seconds
         * @param maxDelayTimeInSec max delay time in seconds
         * @return builder
         */
        StreamisLogAppenderConfig.Builder setRpcMaxDelayTimeInSec(int maxDelayTimeInSec){
            this.rpcLogSenderConfig.setMaxDelayTimeInSec(maxDelayTimeInSec);
            return this;
        }

        /**
         * Rpc auth token code key
         * @param tokenCodeKey key of token code
         * @return builder
         */
        StreamisLogAppenderConfig.Builder setRpcAuthTokenCodeKey(String tokenCodeKey){
            this.rpcLogSenderConfig.getAuthConfig().setTokenCodeKey(tokenCodeKey);
            return this;
        }

        /**
         * Rpc auth token user key
         * @param tokenUserKey key of token user
         * @return builder
         */
        StreamisLogAppenderConfig.Builder setRpcAuthTokenUserKey(String tokenUserKey){
            this.rpcLogSenderConfig.getAuthConfig().setTokenUserKey(tokenUserKey);
            return this;
        }

        /**
         * Rpc auth token user
         * @param tokenUser token user
         * @return builder
         */
        StreamisLogAppenderConfig.Builder setRpcAuthTokenUser(String tokenUser){
            this.rpcLogSenderConfig.getAuthConfig().setTokenUser(tokenUser);
            return this;
        }

        /**
         * Rpc auth token code
         * @param tokenCode token code
         * @return builder
         */
        StreamisLogAppenderConfig.Builder setRpcAuthTokenCode(String tokenCode){
            this.rpcLogSenderConfig.getAuthConfig().setTokenCode(tokenCode);
            return this;
        }

        /**
         * Rpc cache size
         * @param cacheSize cache size
         * @return builder
         */
        StreamisLogAppenderConfig.Builder setRpcCacheSize(int cacheSize){
            this.rpcLogSenderConfig.getCacheConfig().setSize(cacheSize);
            return this;
        }

        /**
         * Rpc cache max consume thread
         * @param maxConsumeThread max consume thread
         * @return builder
         */
        StreamisLogAppenderConfig.Builder setRpcCacheMaxConsumeThread(int maxConsumeThread){
            this.rpcLogSenderConfig.getCacheConfig().setMaxConsumeThread(maxConsumeThread);
            return this;
        }

        /**
         * Rpc buffer size
         * @param bufferSize buffer size
         * @return builder
         */
        StreamisLogAppenderConfig.Builder setRpcBufferSize(int bufferSize){
            this.rpcLogSenderConfig.getBufferConfig().setSize(bufferSize);
            return this;
        }

        /**
         * Rpc buffer expire time in seconds
         * @param expireTimeInSec expire time
         * @return builder
         */
        StreamisLogAppenderConfig.Builder setRpcBufferExpireTimeInSec(int expireTimeInSec){
            this.rpcLogSenderConfig.getBufferConfig().setExpireTimeInSec(expireTimeInSec);
            return this;
        }
        /**
         * Build method
         * @return config
         */
        public StreamisLogAppenderConfig build(){
            Filter logFilter = null;
            if (filters.size() > 1){
                logFilter = CompositeFilter.createFilters(filters.toArray(new Filter[0]));
            } else if (!filters.isEmpty()){
                logFilter = filters.get(0);
            }
            return new StreamisLogAppenderConfig(applicationName, logFilter, rpcLogSenderConfig);
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
