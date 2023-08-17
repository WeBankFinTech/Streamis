package com.webank.wedatasphere.streamis.jobmanager.log.collector.config;


public class SendBufferConfig {
    /**
     * Size of send buffer
     */
    private int size = 50;

    /**
     * Expire time of send buffer
     */
    private long expireTimeInSec = 2;

    public SendBufferConfig(){

    }

    public SendBufferConfig(int size, long expireTimeInSec){
        this.size = size;
        this.expireTimeInSec = expireTimeInSec;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getExpireTimeInSec() {
        return expireTimeInSec;
    }

    public void setExpireTimeInSec(long expireTimeInSec) {
        this.expireTimeInSec = expireTimeInSec;
    }

    @Override
    public String toString() {
        return "SendBufferConfig{" +
                "size=" + size +
                ", expireTimeInSec=" + expireTimeInSec +
                '}';
    }
}
