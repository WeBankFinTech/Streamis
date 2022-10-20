package com.webank.wedatasphere.streamis.jobmanager.log.collector.config;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Integers;

/**
 * Cache config
 */
@Plugin(
        name = "SendLogCache",
        category = "Core",
        printObject = true
)
public class SendLogCacheConfig {
    /**
     * Size of send cache
     */
    private int size = 150;

    /**
     * Max number of consuming thread
     */
    private int maxConsumeThread = 10;

    public SendLogCacheConfig(){

    }

    public SendLogCacheConfig(int size, int maxConsumeThread){
        this.size = size;
        this.maxConsumeThread = maxConsumeThread;
    }
    @PluginFactory
    public static SendLogCacheConfig createCacheConfig(
            @PluginAttribute("size") String size, @PluginAttribute("maxConsumeThread") String maxConsumeThread){
        return new SendLogCacheConfig(Integers.parseInt(size, 150), Integers.parseInt(maxConsumeThread, 10));
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
}
