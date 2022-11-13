package com.webank.wedatasphere.streamis.jobmanager.log.collector.config;

/**
 * Cache config
 */

public class SendLogCacheConfig {
    /**
     * Size of send cache
     */
    private int size = 150;

    /**
     * Max number of consuming thread
     */
    private int maxConsumeThread = 2;

    private boolean discard = true;

    public SendLogCacheConfig(){

    }

    public SendLogCacheConfig(int size, int maxConsumeThread){
        this.size = size;
        this.maxConsumeThread = maxConsumeThread;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getMaxConsumeThread() {
        return maxConsumeThread;
    }

    public void setMaxConsumeThread(int maxConsumeThread) {
        this.maxConsumeThread = maxConsumeThread;
    }

    @Override
    public String toString() {
        return "SendLogCacheConfig{" +
                "size=" + size +
                ", maxConsumeThread=" + maxConsumeThread +
                '}';
    }

}
