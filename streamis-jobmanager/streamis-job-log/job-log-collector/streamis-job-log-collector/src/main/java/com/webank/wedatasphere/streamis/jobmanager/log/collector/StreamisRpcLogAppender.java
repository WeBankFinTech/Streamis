package com.webank.wedatasphere.streamis.jobmanager.log.collector;

import com.webank.wedatasphere.streamis.jobmanager.log.collector.cache.LogCache;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.RpcLogSenderConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.StreamisLogAppenderConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.log4j2.StreamisLog4j2AppenderConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.message.filters.LogMessageFilter;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.RpcHeartbeatService;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.StreamisRpcLogSender;
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
import java.util.*;
import java.util.function.BiPredicate;

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

    /**
     * Filter function
     */
    private BiPredicate<String, String> messageFilterFunction = (logger, message) -> true;

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
        List<LogMessageFilter> messageFilters = appenderConfig.getMessageFilters();
        if (null != messageFilters && messageFilters.size() > 0){
            messageFilterFunction = (logger, message) ->{
                for(LogMessageFilter messageFilter : messageFilters){
                    if (!messageFilter.doFilter(logger, message)){
                        return false;
                    }
                }
               return true;
            };
        }
        Runtime.getRuntime().addShutdownHook(new Thread(this.rpcLogSender::close));
    }

    @Override
    public void append(LogEvent event) {
        String content = new String(getLayout().toByteArray(event));
        if (messageFilterFunction.test(event.getLoggerName(), content)) {
            StreamisLogEvent logEvent = new StreamisLogEvent(content, event.getTimeMillis());
            try {
                System.out.println(event.getLoggerName() + "will send event content: " + content);
                this.logCache.cacheLog(logEvent);
            } catch (InterruptedException e) {
                LOGGER.error("StreamisRpcLogAppender: {} interrupted when cache the log into the RPC sender, message: {}", this.getName(), e.getMessage());
            }
        } else {
            if (appenderConfig.getSenderConfig().isDebugMode()) {
                System.out.println("debug: event didn't pass messageFilterFunction, will ignore. content:\n" + content);
            }
        }
    }

    @PluginFactory
    public static StreamisRpcLogAppender createAppender(@PluginAttribute("name") String name,
                                                        @PluginAttribute("appName") String applicationName,
                                                        @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
                                                        @PluginElement("Filter") final Filter filter,
                                                        @PluginElement("Layout") Layout<? extends Serializable> layout,
                                                         @PluginElement("RpcLogSender")RpcLogSenderConfig rpcLogSenderConfig) throws IllegalAccessException {
        if (null == name || name.trim().equals("")){
            name = DEFAULT_APPENDER_NAME;
        }
        if (Objects.isNull(layout)){
            layout = PatternLayout.createDefaultLayout();
        }
        // Search the config autowired class
        List<StreamisConfigAutowired> configAutowiredEntities = new ArrayList<>();
        StreamisLog4j2AppenderConfig logAppenderConfig = null;
        ServiceLoader.load(StreamisConfigAutowired.class,
                StreamisRpcLogAppender.class.getClassLoader()).iterator().forEachRemaining(configAutowiredEntities::add);
        StreamisLog4j2AppenderConfig.Builder builder = new StreamisLog4j2AppenderConfig.Builder(applicationName, filter, rpcLogSenderConfig);
        for (StreamisConfigAutowired autowired : configAutowiredEntities){
            logAppenderConfig = (StreamisLog4j2AppenderConfig) autowired.logAppenderConfig(builder);
        }
        if (Objects.isNull(logAppenderConfig)){
            logAppenderConfig = builder.build();
        }
        applicationName = logAppenderConfig.getApplicationName();
        if (null == applicationName || applicationName.trim().equals("")){
            throw new IllegalArgumentException("Application name cannot be empty");
        }
        System.out.println("StreamisRpcLogAppender: init with config => " + logAppenderConfig);
        new RpcHeartbeatService(logAppenderConfig).startHeartbeat();
        return new StreamisRpcLogAppender(name, logAppenderConfig.getFilter(), layout, ignoreExceptions, Property.EMPTY_ARRAY, logAppenderConfig);
    }

}
