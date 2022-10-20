package com.webank.wedatasphere.streamis.jobmanager.log.collector.config;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Integers;

@Plugin(
        name = "SendBuffer",
        category = "Core",
        printObject = true
)
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

    @PluginFactory
    public static SendBufferConfig createBufferConfig(
            @PluginAttribute("size") String size, @PluginAttribute("expireTimeInSec") String expireTimeInSec){
        return new SendBufferConfig(Integers.parseInt(size, 50), Integers.parseInt(expireTimeInSec, 2));
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
}
