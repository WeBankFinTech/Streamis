package com.webank.wedatasphere.streamis.jobmanager.log.collector.config;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Integers;

import java.util.Objects;

/**
 * Rpc sender configuration
 */
@Plugin(
   name = "RpcLogSender",
   category = "Core",
   printObject = true
)
public class RpcLogSenderConfig {

    /**
     * Send address
     */
    private String address;

    /**
     * Timeout of connecting
     */
    private int connectionTimeout = 3000;

    /**
     * Timeout of reading from socket
     */
    private int socketTimeout = 15000;

    /**
     * Retry count of sending
     */
    private int sendRetryCnt = 3;

    /**
     * The time for server recovery
     */
    private int serverRecoveryTimeInSec = 5;

    /**
     * Retry max delay time of sender
     */
    private int maxDelayTimeInSec = 60;

    /**
     * Auth config
     */
    private RpcAuthConfig authConfig = new RpcAuthConfig();

    /**
     * Cache config
     */
    private SendLogCacheConfig cacheConfig = new SendLogCacheConfig();

    /**
     * Buffer config
     */
    private SendBufferConfig bufferConfig = new SendBufferConfig();

    public RpcLogSenderConfig(){

    }

    public RpcLogSenderConfig(String address, int sendRetryCnt, int connectionTimeout, int socketTimeout,
                              int serverRecoveryTimeInSec, int maxDelayTimeInSec,
                              RpcAuthConfig authConfig, SendLogCacheConfig cacheConfig, SendBufferConfig bufferConfig){
        this.address = address;
        this.sendRetryCnt = sendRetryCnt;
        this.connectionTimeout = connectionTimeout;
        this.socketTimeout = socketTimeout;
        this.serverRecoveryTimeInSec = serverRecoveryTimeInSec;
        this.maxDelayTimeInSec = maxDelayTimeInSec;
        if (Objects.nonNull(authConfig)){
            this.authConfig = authConfig;
        }
        if (Objects.nonNull(cacheConfig)){
            this.cacheConfig = cacheConfig;
        }
        if (Objects.nonNull(bufferConfig)){
            this.bufferConfig = bufferConfig;
        }
    }
    @PluginFactory
    public static RpcLogSenderConfig createConfig(
            @PluginAttribute("address") String address, @PluginAttribute("sendRetryCnt") String sendRetryCnt,
            @PluginAttribute("connectionTimeout") String connectionTimeout, @PluginAttribute("socketTimeout") String socketTimeout,
            @PluginAttribute("serverRecoveryTimeInSec") String serverRecoveryTimeInSec, @PluginAttribute("maxDelayTimeInSec") String maxDelayTimeInSec,
            @PluginElement("AuthConfig")RpcAuthConfig authConfig, @PluginElement("SendLogCache") SendLogCacheConfig cacheConfig,
            @PluginElement("SendBuffer")SendBufferConfig bufferConfig){
        return new RpcLogSenderConfig(address, Integers.parseInt(sendRetryCnt, 3),
                Integers.parseInt(connectionTimeout, 3000), Integers.parseInt(socketTimeout, 15000),
                Integers.parseInt(serverRecoveryTimeInSec, 5), Integers.parseInt(maxDelayTimeInSec, 60),
                authConfig, cacheConfig, bufferConfig);
    }
    public RpcAuthConfig getAuthConfig() {
        return authConfig;
    }

    public void setAuthConfig(RpcAuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    public SendLogCacheConfig getCacheConfig() {
        return cacheConfig;
    }

    public void setCacheConfig(SendLogCacheConfig cacheConfig) {
        this.cacheConfig = cacheConfig;
    }

    public SendBufferConfig getBufferConfig() {
        return bufferConfig;
    }

    public void setBufferConfig(SendBufferConfig bufferConfig) {
        this.bufferConfig = bufferConfig;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getSendRetryCnt() {
        return sendRetryCnt;
    }

    public void setSendRetryCnt(int sendRetryCnt) {
        this.sendRetryCnt = sendRetryCnt;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getMaxDelayTimeInSec() {
        return maxDelayTimeInSec;
    }

    public void setMaxDelayTimeInSec(int maxDelayTimeInSec) {
        this.maxDelayTimeInSec = maxDelayTimeInSec;
    }

    public int getServerRecoveryTimeInSec() {
        return serverRecoveryTimeInSec;
    }

    public void setServerRecoveryTimeInSec(int serverRecoveryTimeInSec) {
        this.serverRecoveryTimeInSec = serverRecoveryTimeInSec;
    }
}
