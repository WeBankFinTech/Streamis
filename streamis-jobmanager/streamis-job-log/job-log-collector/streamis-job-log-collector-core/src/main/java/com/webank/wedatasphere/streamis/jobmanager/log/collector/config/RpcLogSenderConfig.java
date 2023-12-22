package com.webank.wedatasphere.streamis.jobmanager.log.collector.config;


import java.util.Objects;

/**
 * Rpc sender configuration
 */
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
     * If open debug mode
     */
    private boolean debugMode = false;
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

    private RpcHeartbeatConfig heartbeatConfig =new RpcHeartbeatConfig();

    public RpcLogSenderConfig(){

    }

    public RpcLogSenderConfig(String address, int sendRetryCnt, int connectionTimeout, int socketTimeout,
                              int serverRecoveryTimeInSec, int maxDelayTimeInSec,
                              RpcAuthConfig authConfig, SendLogCacheConfig cacheConfig, SendBufferConfig bufferConfig,RpcHeartbeatConfig heartbeatConfig){
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
        if (Objects.nonNull(heartbeatConfig)){
            this.heartbeatConfig = heartbeatConfig;
        }
    }

    public RpcHeartbeatConfig getHeartbeatConfig() {
        return heartbeatConfig;
    }

    public void setHeartbeatConfig(RpcHeartbeatConfig heartbeatConfig) {
        this.heartbeatConfig = heartbeatConfig;
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

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    @Override
    public String toString() {
        return "RpcLogSenderConfig{" +
                "address='" + address + '\'' +
                ", connectionTimeout=" + connectionTimeout +
                ", socketTimeout=" + socketTimeout +
                ", sendRetryCnt=" + sendRetryCnt +
                ", serverRecoveryTimeInSec=" + serverRecoveryTimeInSec +
                ", maxDelayTimeInSec=" + maxDelayTimeInSec +
                ", authConfig=" + authConfig +
                ", cacheConfig=" + cacheConfig +
                ", bufferConfig=" + bufferConfig +
                ", heartbeatConfig=" + heartbeatConfig +
                ", debug=" + debugMode +
                '}';
    }

}
