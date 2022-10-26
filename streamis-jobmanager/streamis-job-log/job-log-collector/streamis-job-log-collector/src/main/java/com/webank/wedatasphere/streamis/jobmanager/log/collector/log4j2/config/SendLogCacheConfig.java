package com.webank.wedatasphere.streamis.jobmanager.log.collector.log4j2.config;

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
public class SendLogCacheConfig extends com.webank.wedatasphere.streamis.jobmanager.log.collector.config.SendLogCacheConfig {

    public SendLogCacheConfig(int size, int maxConsumeThread) {
        super(size, maxConsumeThread);
    }

    @PluginFactory
    public static SendLogCacheConfig createCacheConfig(
            @PluginAttribute("size") String size, @PluginAttribute("maxConsumeThread") String maxConsumeThread){
        return new SendLogCacheConfig(Integers.parseInt(size, 150), Integers.parseInt(maxConsumeThread, 10));
    }
}
