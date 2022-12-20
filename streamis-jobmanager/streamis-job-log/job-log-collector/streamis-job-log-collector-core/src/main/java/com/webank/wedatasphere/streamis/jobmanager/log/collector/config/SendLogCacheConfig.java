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

    /**
     * The switch to discard log
     */
    private boolean discard = true;

    /**
     * Discard window in second
     */
    private int discardWindow = 2;

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

    public boolean isDiscard() {
        return discard;
    }

    public void setDiscard(boolean discard) {
        this.discard = discard;
    }

    public int getDiscardWindow() {
        return discardWindow;
    }

    public void setDiscardWindow(int discardWindow) {
        this.discardWindow = discardWindow;
    }

    @Override
    public String toString() {
        return "SendLogCacheConfig{" +
                "size=" + size +
                ", maxConsumeThread=" + maxConsumeThread +
                ", discard=" + discard +
                ", discardWindow=" + discardWindow +
                '}';
    }



}
