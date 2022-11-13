package com.webank.wedatasphere.streamis.jobmanager.log.collector;

import com.webank.wedatasphere.streamis.jobmanager.log.collector.cache.LogCache;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.RpcLogSenderConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.log4j1.StreamisLog4jAppenderConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.message.filters.LogMessageFilter;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.StreamisRpcLogSender;
import com.webank.wedatasphere.streamis.jobmanager.log.entities.StreamisLogEvent;
import com.webank.wedatasphere.streamis.jobmanager.plugin.StreamisConfigAutowired;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Rpc appender for log4j1
 */
public class StreamisRpcLogAppender extends AppenderSkeleton {

    /**
     * Application name
     */
    private String applicationName;

    private String filterEnable = "true";
    /**
     * Appender config
     */
    private StreamisLog4jAppenderConfig appenderConfig;

    /**
     * Rpc log sender
     */
    private StreamisRpcLogSender rpcLogSender;

    /**
     * Rpc log sender config
     */
    private RpcLogSenderConfig rpcLogSenderConfig = new RpcLogSenderConfig();


    /**
     * Cache
     */
    private LogCache<StreamisLogEvent> logCache;

    /**
     * Filter function
     */
    private BiFunction<String, String, Boolean> messageFilterFunction = (logger, message) -> false;

    @Override
    protected void append(LoggingEvent loggingEvent) {
        String content = super.getLayout().format(loggingEvent);
        if (messageFilterFunction.apply(loggingEvent.getLoggerName(), content)) {
            // Transform to stream log event;
            // System.currentTimeMills() -> loggingEvent.getTimeStamp()
            StreamisLogEvent logEvent = new StreamisLogEvent(content, loggingEvent.getTimeStamp());
            if (Objects.nonNull(logCache)) {
                try {
                    this.logCache.cacheLog(logEvent);
                } catch (InterruptedException e) {
                    LogLog.error("StreamisRpcLogAppender: " + this.getName() +
                            " interrupted when cache the log into the RPC sender, message: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void close() {
        if (Objects.nonNull(this.rpcLogSender)){
            this.rpcLogSender.close();
        }
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

    @Override
    public void activateOptions() {
        if (Objects.nonNull(this.logCache)){
            return;
        }
        if (Objects.isNull(getLayout())){
            setLayout(new SimpleLayout());
        }
        if (System.getProperty("filter.enable") == null){
            System.setProperty("filter.enable", filterEnable);
        }
        // Search the config autowired class
        List<StreamisConfigAutowired> configAutowiredEntities = new ArrayList<>();
        StreamisLog4jAppenderConfig logAppenderConfig = null;
        ServiceLoader.load(StreamisConfigAutowired.class,
                StreamisRpcLogAppender.class.getClassLoader()).iterator().forEachRemaining(configAutowiredEntities::add);
        StreamisLog4jAppenderConfig.Builder builder = new StreamisLog4jAppenderConfig.Builder(this.applicationName,
                getThreshold(), getFilter(), rpcLogSenderConfig);
        for (StreamisConfigAutowired autowired : configAutowiredEntities){
            try {
                logAppenderConfig = (StreamisLog4jAppenderConfig) autowired.logAppenderConfig(builder);
            } catch (Exception e) {
                LogLog.warn("Unable to autowired the config from: " +autowired.getClass().getName(), e);
            }
        }
        if (Objects.isNull(logAppenderConfig)){
            logAppenderConfig =  builder.build();
        }
        this.applicationName = logAppenderConfig.getApplicationName();
        if (null == applicationName || applicationName.trim().equals("")){
            throw new IllegalArgumentException("Application name cannot be empty");
        }
        this.appenderConfig = logAppenderConfig;
        // Set the threshold to error default
        setThreshold(Optional.ofNullable(logAppenderConfig.getThreshold()).orElse(Level.ERROR));
        // First to clear the filters
        clearFilters();
        // Then to add filter
        logAppenderConfig.getFilters().forEach(this::addFilter);
        System.out.println("StreamisRpcLogAppender: init with config => " + logAppenderConfig);
        this.rpcLogSender = new StreamisRpcLogSender(this.appenderConfig.getApplicationName(),
                this.appenderConfig.getSenderConfig());
        this.rpcLogSender.setExceptionListener((subject, t, message) ->
                LogLog.error((null != subject? subject.getClass().getSimpleName() : "") + ": " + message, t));
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
    }


    public String getAppName() {
        return applicationName;
    }

    /**
     * Application name
     * @param applicationName name
     */
    public void setAppName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getFilterEnable() {
        return filterEnable;
    }

    public void setFilterEnable(String filterEnable) {
        this.filterEnable = filterEnable;
    }

    public void setRpcAddress(String address){
        this.rpcLogSenderConfig.setAddress(address);
    }

    public void setRpcConnTimeout(int connectionTimeout){
        this.rpcLogSenderConfig.setConnectionTimeout(connectionTimeout);
    }

    public void setRpcSocketTimeout(int socketTimeout){
        this.rpcLogSenderConfig.setSocketTimeout(socketTimeout);
    }
    public void setRpcSendRetryCnt(int sendRetryCnt){
        this.rpcLogSenderConfig.setSendRetryCnt(sendRetryCnt);
    }

    public void setRpcServerRecoveryTimeInSec(int serverRecoveryTimeInSec){
        this.rpcLogSenderConfig.setServerRecoveryTimeInSec(serverRecoveryTimeInSec);
    }

    public void setRpcMaxDelayTimeInSec(int maxDelayTimeInSec){
        this.rpcLogSenderConfig.setMaxDelayTimeInSec(maxDelayTimeInSec);
    }
    // Authentication
    public void setRpcAuthTokenCodeKey(String tokenCodeKey){
        this.rpcLogSenderConfig.getAuthConfig().setTokenCodeKey(tokenCodeKey);
    }

    public void setRpcAuthTokenUserKey(String tokenUserKey){
        this.rpcLogSenderConfig.getAuthConfig().setTokenUserKey(tokenUserKey);
    }

    public void setRpcAuthTokenUser(String tokenUser){
        this.rpcLogSenderConfig.getAuthConfig().setTokenUser(tokenUser);
    }

    public void setRpcAuthTokenCode(String tokenCode){
        this.rpcLogSenderConfig.getAuthConfig().setTokenCode(tokenCode);
    }

    // Cache configuration
    public void setRpcCacheSize(int cacheSize){
        this.rpcLogSenderConfig.getCacheConfig().setSize(cacheSize);
    }

    public void setRpcCacheMaxConsumeThread(int maxConsumeThread){
        this.rpcLogSenderConfig.getCacheConfig().setMaxConsumeThread(maxConsumeThread);
    }

    // Buffer configuration
    public void setRpcBufferSize(int bufferSize){
        this.rpcLogSenderConfig.getBufferConfig().setSize(bufferSize);
    }

    public void setRpcBufferExpireTimeInSec(int expireTimeInSec){
        this.rpcLogSenderConfig.getBufferConfig().setExpireTimeInSec(expireTimeInSec);
    }

    public void setDebugMode(boolean debugMode){
        this.rpcLogSenderConfig.setDebugMode(debugMode);
    }
}
