package com.webank.wedatasphere.streamis.jobmanager.log.collector;

import com.webank.wedatasphere.streamis.jobmanager.log.collector.cache.LogCache;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.RpcLogSenderConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.StreamisLogAppenderConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.StreamisRpcLogSender;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.http.Json;
import com.webank.wedatasphere.streamis.jobmanager.log.entities.StreamisLogEvent;
import com.webank.wedatasphere.streamis.jobmanager.plugin.StreamisConfigAutowired;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * Streamis rpc log appender
 */
@Plugin(name = "StreamRpcLog", category = "Core", elementType = "appender", printObject = true)
public class StreamisRpcLogAppender extends AbstractAppender {
    private static final String DEFAULT_APPENDER_NAME = "StreamRpcLog";
    /**
     * Appender config
     */
    private final StreamisLogAppenderConfig appenderConfig;

    /**
     * Rpc log sender
     */
    private final StreamisRpcLogSender rpcLogSender;

    /**
     * Cache
     */
    private final LogCache<StreamisLogEvent> logCache;
    protected StreamisRpcLogAppender(String name, Filter filter,
                                     Layout<? extends Serializable> layout,
                                     boolean ignoreExceptions, Property[] properties,
                                     StreamisLogAppenderConfig appenderConfig) {
        super(name, filter, layout, ignoreExceptions, properties);
        this.appenderConfig = appenderConfig;
        this.rpcLogSender = new StreamisRpcLogSender(this.appenderConfig.getApplicationName(),
                this.appenderConfig.getSenderConfig());
        this.rpcLogSender.setExceptionListener((subject, t, message) ->
                LOGGER.error((null != subject? subject.getClass().getSimpleName() : "") + ": " + message, t));
        this.logCache = this.rpcLogSender.getOrCreateLogCache();
        Runtime.getRuntime().addShutdownHook(new Thread(this.rpcLogSender::close));
    }

    @Override
    public void append(LogEvent event) {
        String content = new String(getLayout().toByteArray(event));
        // Transform to stream log event;
        StreamisLogEvent logEvent = new StreamisLogEvent(content, System.currentTimeMillis());
        try {
            this.logCache.cacheLog(logEvent);
        } catch (InterruptedException e) {
            LOGGER.error("StreamisRpcLogAppender: {} interrupted when cache the log into the RPC sender, message: {}", this.getName(), e.getMessage());
        }
    }

    @PluginFactory
    public static StreamisRpcLogAppender createAppender(@PluginAttribute("name") String name,
                                                        @PluginAttribute("appName") String applicationName,
                                                        @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
                                                        @PluginElement("Filter") final Filter filter,
                                                        @PluginElement("Layout") Layout<? extends Serializable> layout,
                                                         @PluginElement("RpcLogSender")RpcLogSenderConfig rpcLogSenderConfig) throws Exception{
        if (null == name || name.trim().equals("")){
            name = DEFAULT_APPENDER_NAME;
        }
        if (Objects.isNull(layout)){
            layout = PatternLayout.createDefaultLayout();
        }
        // Search the config autowired class
        List<StreamisConfigAutowired> configAutowiredEntities = new ArrayList<>();
        StreamisLogAppenderConfig logAppenderConfig = null;
        ServiceLoader.load(StreamisConfigAutowired.class,
                StreamisRpcLogAppender.class.getClassLoader()).iterator().forEachRemaining(configAutowiredEntities::add);
        StreamisLogAppenderConfig.Builder builder = new StreamisLogAppenderConfig.Builder(applicationName, filter, rpcLogSenderConfig);
        for (StreamisConfigAutowired autowired : configAutowiredEntities){
            logAppenderConfig = autowired.logAppenderConfig(builder);
        }
        if (Objects.isNull(logAppenderConfig)){
            logAppenderConfig = builder.build();
        }
        applicationName = logAppenderConfig.getApplicationName();
        if (null == applicationName || applicationName.trim().equals("")){
            throw new IllegalArgumentException("Application name cannot be empty");
        }
        LOGGER.info("StreamisRpcLogAppender: init with config {}", Json.toJson(logAppenderConfig, null));
        return new StreamisRpcLogAppender(name, filter, layout, ignoreExceptions, Property.EMPTY_ARRAY, logAppenderConfig);
    }

}
