package com.webank.wedatasphere.streamis.jobmanager.log.collector.log4j1;

import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.RpcLogSenderConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.StreamisLogAppenderConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.message.filters.LogMessageFilter;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.message.filters.LogMessageFilterAdapter;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Appender config for log4j1
 */
public class StreamisLog4jAppenderConfig extends StreamisLogAppenderConfig {

    /**
     * Filter in log4j1
     */
    private final List<Filter> filters = new ArrayList<>();
    /**
     *
     */
    private final Priority threshold;

    protected StreamisLog4jAppenderConfig(String applicationName, Priority threshold, List<Filter> filters,
                                          RpcLogSenderConfig rpcLogSenderConfig, List<LogMessageFilter> messageFilters) {
        super(applicationName, rpcLogSenderConfig, messageFilters);
        this.threshold = threshold;
        this.filters.addAll(filters);
    }

    public static class Builder extends StreamisLogAppenderConfig.Builder{

        /**
         * Filter rules
         */
        private final List<Filter> filters = new ArrayList<>();

        /**
         * Threshold
         */
        private Priority threshold;

        public Builder(String applicationName, Priority threshold, Filter filter,RpcLogSenderConfig rpcLogSenderConfig) {
            super(applicationName, rpcLogSenderConfig);
            this.threshold = threshold;
            if (Objects.nonNull(filter)) {
                this.filters.add(filter);
            }
        }

        public  StreamisLog4jAppenderConfig.Builder setFilter(Filter filter){
            this.filters.clear();
            this.messageFilters.clear();
            this.filters.add(filter);
            if (filter instanceof LogMessageFilterAdapter){
                this.messageFilters.add(((LogMessageFilterAdapter) filter).getLogMessageFilter());
            }
            return this;
        }

        public StreamisLog4jAppenderConfig.Builder withFilter(Filter filter){
            filters.add(filter);
            if (filter instanceof LogMessageFilterAdapter){
                this.messageFilters.add(((LogMessageFilterAdapter) filter).getLogMessageFilter());
            }
            return this;
        }

        /**
         * Set threshold
         * @param threshold threshold
         * @return builder
         */
        public StreamisLog4jAppenderConfig.Builder threshold(Priority threshold, boolean needMoreSpecific){
            if (needMoreSpecific){
                if (this.threshold == null || threshold.isGreaterOrEqual(this.threshold)){
                    this.threshold = threshold;
                }
            }else {
                this.threshold = threshold;
            }
            return this;
        }
        public StreamisLog4jAppenderConfig build(){
            return new StreamisLog4jAppenderConfig(applicationName, threshold, filters, rpcLogSenderConfig, messageFilters);
        }
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public Priority getThreshold() {
        return threshold;
    }

    @Override
    public String toString() {
        return "StreamisLog4jAppenderConfig{" +
                "applicationName='" + applicationName + '\'' +
                ", senderConfig=" + senderConfig +
                ", filters=" + filters +
                ", threshold=" + threshold +
                '}';
    }
}
