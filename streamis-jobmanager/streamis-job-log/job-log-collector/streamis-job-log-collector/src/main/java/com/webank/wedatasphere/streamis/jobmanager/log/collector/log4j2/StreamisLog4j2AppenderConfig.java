package com.webank.wedatasphere.streamis.jobmanager.log.collector.log4j2;

import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.RpcLogSenderConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.StreamisLogAppenderConfig;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.filter.CompositeFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Appender config for log4j2
 */
public class StreamisLog4j2AppenderConfig extends StreamisLogAppenderConfig {
    /**
     * Filter in log4j2
     */
    private final Filter filter;

    public StreamisLog4j2AppenderConfig(String applicationName, Filter filter,
                                        RpcLogSenderConfig rpcLogSenderConfig){
        super(applicationName, rpcLogSenderConfig);
        this.filter = filter;
    }

    public static class Builder extends StreamisLogAppenderConfig.Builder {

        /**
         * Filter rules
         */
        private final List<Filter> filters = new ArrayList<>();

        public Builder(String applicationName, Filter filter, RpcLogSenderConfig rpcLogSenderConfig) {
            super(applicationName, rpcLogSenderConfig);
            if (Objects.nonNull(filter)) {
                this.filters.add(filter);
            }
        }

        /**
         * Set filter
         * @param filter filter
         * @return builder
         */
        public StreamisLog4j2AppenderConfig.Builder setFilter(Filter filter){
            this.filters.clear();
            this.filters.add(filter);
            return this;
        }

        /**
         * Append filter
         * @param filter filter
         * @return builder
         */
        public StreamisLog4j2AppenderConfig.Builder withFilter(Filter filter){
            filters.add(filter);
            return this;
        }

        /**
         * Build method
         * @return config
         */
        public StreamisLog4j2AppenderConfig build(){
            Filter logFilter = null;
            if (filters.size() > 1){
                logFilter = CompositeFilter.createFilters(filters.toArray(new Filter[0]));
            } else if (!filters.isEmpty()){
                logFilter = filters.get(0);
            }
            return new StreamisLog4j2AppenderConfig(applicationName, logFilter, rpcLogSenderConfig);
        }
    }
    public Filter getFilter() {
        return filter;
    }

    @Override
    public String toString() {
        return "StreamisLog4j2AppenderConfig{" +
                "applicationName='" + applicationName + '\'' +
                ", senderConfig=" + senderConfig +
                ", filter=" + filter +
                '}';
    }
}
