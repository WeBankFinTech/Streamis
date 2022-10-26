package com.webank.wedatasphere.streamis.jobmanager.log.collector.config;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

/**
 * Appender config
 */
public class StreamisLogAppenderConfig {

    protected final String applicationName;


    protected final RpcLogSenderConfig senderConfig;

    protected StreamisLogAppenderConfig(String applicationName, RpcLogSenderConfig rpcLogSenderConfig){
        this.applicationName = applicationName;
        this.senderConfig = null != rpcLogSenderConfig? rpcLogSenderConfig : new RpcLogSenderConfig();
    }

    public static class Builder{
        /**
         * Application name
         */
        protected String applicationName;

        /**
         * Sender config
         */
        protected final RpcLogSenderConfig rpcLogSenderConfig;

        public Builder(String applicationName,
                       RpcLogSenderConfig rpcLogSenderConfig){
            this.applicationName = applicationName;

            this.rpcLogSenderConfig = Optional.ofNullable(rpcLogSenderConfig).orElse(new RpcLogSenderConfig());
        }

        /**
         * Set application name
         * @param applicationName application name
         * @return builder
         */
        public StreamisLogAppenderConfig.Builder setAppName(String applicationName){
            this.applicationName = applicationName;
            return this;
        }



        /**
         * Rpc address
         * @param address address
         * @return builder
         */
        public StreamisLogAppenderConfig.Builder setRpcAddress(String address){
            this.rpcLogSenderConfig.setAddress(address);
            return this;
        }

        /**
         * Rpc connect timeout
         * @param connectionTimeout connection timeout
         * @return builder
         */
        public StreamisLogAppenderConfig.Builder setRpcConnTimeout(int connectionTimeout){
            this.rpcLogSenderConfig.setConnectionTimeout(connectionTimeout);
            return this;
        }

        /**
         * Rpc socket timeout
         * @param socketTimeout socket timeout
         * @return builder
         */
        public StreamisLogAppenderConfig.Builder setRpcSocketTimeout(int socketTimeout){
            this.rpcLogSenderConfig.setSocketTimeout(socketTimeout);
            return this;
        }

        /**
         * Rpc send retry count
         * @param sendRetryCnt send retry count
         * @return builder
         */
        public StreamisLogAppenderConfig.Builder setRpcSendRetryCnt(int sendRetryCnt){
            this.rpcLogSenderConfig.setSendRetryCnt(sendRetryCnt);
            return this;
        }

        /**
         * Rpc server recovery time in seconds
         * @param serverRecoveryTimeInSec server recovery time
         * @return builder
         */
        public StreamisLogAppenderConfig.Builder setRpcServerRecoveryTimeInSec(int serverRecoveryTimeInSec){
            this.rpcLogSenderConfig.setServerRecoveryTimeInSec(serverRecoveryTimeInSec);
            return this;
        }

        /**
         * Rpc max delay time in seconds
         * @param maxDelayTimeInSec max delay time in seconds
         * @return builder
         */
        public StreamisLogAppenderConfig.Builder setRpcMaxDelayTimeInSec(int maxDelayTimeInSec){
            this.rpcLogSenderConfig.setMaxDelayTimeInSec(maxDelayTimeInSec);
            return this;
        }

        /**
         * Rpc auth token code key
         * @param tokenCodeKey key of token code
         * @return builder
         */
        public StreamisLogAppenderConfig.Builder setRpcAuthTokenCodeKey(String tokenCodeKey){
            this.rpcLogSenderConfig.getAuthConfig().setTokenCodeKey(tokenCodeKey);
            return this;
        }

        /**
         * Rpc auth token user key
         * @param tokenUserKey key of token user
         * @return builder
         */
        public StreamisLogAppenderConfig.Builder setRpcAuthTokenUserKey(String tokenUserKey){
            this.rpcLogSenderConfig.getAuthConfig().setTokenUserKey(tokenUserKey);
            return this;
        }

        /**
         * Rpc auth token user
         * @param tokenUser token user
         * @return builder
         */
        public StreamisLogAppenderConfig.Builder setRpcAuthTokenUser(String tokenUser){
            this.rpcLogSenderConfig.getAuthConfig().setTokenUser(tokenUser);
            return this;
        }

        /**
         * Rpc auth token code
         * @param tokenCode token code
         * @return builder
         */
        public StreamisLogAppenderConfig.Builder setRpcAuthTokenCode(String tokenCode){
            this.rpcLogSenderConfig.getAuthConfig().setTokenCode(tokenCode);
            return this;
        }

        /**
         * Rpc cache size
         * @param cacheSize cache size
         * @return builder
         */
        public StreamisLogAppenderConfig.Builder setRpcCacheSize(int cacheSize){
            this.rpcLogSenderConfig.getCacheConfig().setSize(cacheSize);
            return this;
        }

        /**
         * Rpc cache max consume thread
         * @param maxConsumeThread max consume thread
         * @return builder
         */
        public StreamisLogAppenderConfig.Builder setRpcCacheMaxConsumeThread(int maxConsumeThread){
            this.rpcLogSenderConfig.getCacheConfig().setMaxConsumeThread(maxConsumeThread);
            return this;
        }

        /**
         * Rpc buffer size
         * @param bufferSize buffer size
         * @return builder
         */
        public StreamisLogAppenderConfig.Builder setRpcBufferSize(int bufferSize){
            this.rpcLogSenderConfig.getBufferConfig().setSize(bufferSize);
            return this;
        }

        /**
         * Rpc buffer expire time in seconds
         * @param expireTimeInSec expire time
         * @return builder
         */
        public StreamisLogAppenderConfig.Builder setRpcBufferExpireTimeInSec(int expireTimeInSec){
            this.rpcLogSenderConfig.getBufferConfig().setExpireTimeInSec(expireTimeInSec);
            return this;
        }

        public StreamisLogAppenderConfig build(){
            return new StreamisLogAppenderConfig(applicationName, rpcLogSenderConfig);
        }
    }
    public String getApplicationName() {
        return applicationName;
    }


    public RpcLogSenderConfig getSenderConfig() {
        return senderConfig;
    }

}
