package com.webank.wedatasphere.streamis.jobmanager.log.collector.log4j2.config;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Integers;

@Plugin(
        name = "SendBuffer",
        category = "Core",
        printObject = true
)
public class SendBufferConfig extends com.webank.wedatasphere.streamis.jobmanager.log.collector.config.SendBufferConfig {

    public SendBufferConfig() {
    }

    public SendBufferConfig(int size, long expireTimeInSec) {
        super(size, expireTimeInSec);
    }

    @PluginFactory
    public static SendBufferConfig createBufferConfig(
            @PluginAttribute("size") String size, @PluginAttribute("expireTimeInSec") String expireTimeInSec){
        return new SendBufferConfig(Integers.parseInt(size, 50),
                Integers.parseInt(expireTimeInSec, 2));
    }
}
