package com.webank.wedatasphere.streamis.jobmanager.log.collector.message.filters;

/**
 * Log message filter, filter the message content (layout formatted)
 */
public interface LogMessageFilter {
    /**
     * Filter formatted message
     * @param logger logger name
     * @param message message content
     * @return if match the filter
     */
    boolean doFilter(String logger, String message);

}
