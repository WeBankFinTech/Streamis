package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender;

/**
 * Rpc sender configuration
 */
public class RpcSenderConfig {

    /**
     * Send address
     */
    private String address;

    /**
     * Key of token-code
     */
    private String tokenCodeKey = "Token-Code";

    /**
     * Key of token-user
     */
    private String tokenUserKey = "Token-User";

    /**
     * Token user
     */
    private String tokenUser = System.getProperty("user.name");

    /**
     * Token code
     */
    private String tokenCode = "STREAM_LOG";

    /**
     * Timeout of connecting
     */
    private int connectionTimeout = 3000;

    /**
     * Timeout of reading from socket
     */
    private int socketTimeout = 15000;

    /**
     * Size of send cache
     */
    private int cacheSize = 150;

    /**
     * Size of send buffer
     */
    private int bufferSize = 50;

    /**
     * Expire time of send buffer
     */
    private long bufferExpireTimeInSec = -1;

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
    private int maxDelayTimeInSec;

    /**
     * Max number of consuming thread
     */
    private int maxConsumeThread = 10;

    public long getBufferExpireTimeInSec() {
        return bufferExpireTimeInSec;
    }

    public void setBufferExpireTimeInSec(long bufferExpireTimeInSec) {
        this.bufferExpireTimeInSec = bufferExpireTimeInSec;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
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

    public int getMaxConsumeThread() {
        return maxConsumeThread;
    }

    public void setMaxConsumeThread(int maxConsumeThread) {
        this.maxConsumeThread = maxConsumeThread;
    }

    public String getTokenCodeKey() {
        return tokenCodeKey;
    }

    public void setTokenCodeKey(String tokenCodeKey) {
        this.tokenCodeKey = tokenCodeKey;
    }

    public String getTokenCode() {
        return tokenCode;
    }

    public void setTokenCode(String tokenCode) {
        this.tokenCode = tokenCode;
    }

    public int getMaxDelayTimeInSec() {
        return maxDelayTimeInSec;
    }

    public void setMaxDelayTimeInSec(int maxDelayTimeInSec) {
        this.maxDelayTimeInSec = maxDelayTimeInSec;
    }

    public String getTokenUserKey() {
        return tokenUserKey;
    }

    public void setTokenUserKey(String tokenUserKey) {
        this.tokenUserKey = tokenUserKey;
    }

    public String getTokenUser() {
        return tokenUser;
    }

    public void setTokenUser(String tokenUser) {
        this.tokenUser = tokenUser;
    }

    public int getServerRecoveryTimeInSec() {
        return serverRecoveryTimeInSec;
    }

    public void setServerRecoveryTimeInSec(int serverRecoveryTimeInSec) {
        this.serverRecoveryTimeInSec = serverRecoveryTimeInSec;
    }
}
