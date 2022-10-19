package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender;

/**
 * Rpc sender configuration
 */
public class RpcSenderConfig {

    /**
     * Send address
     */
    private String sendAddress;
    /**
     * Size of send cache
     */
    private int sendCacheSize = 150;

    /**
     * Size of send buffer
     */
    private int sendBufferSize = 50;

    /**
     * Expire time of send buffer
     */
    private long sendBufferExpireTimeInSec = -1;

    public long getSendBufferExpireTimeInSec() {
        return sendBufferExpireTimeInSec;
    }

    public void setSendBufferExpireTimeInSec(long sendBufferExpireTimeInSec) {
        this.sendBufferExpireTimeInSec = sendBufferExpireTimeInSec;
    }
}
