package com.webank.wedatasphere.streamis.jobmanager.log.collector.message.filters;

/**
 * Interface for adaptor
 */
public interface LogMessageFilterAdapter {

    /**
     * Message filter
     * @return filter
     */
    LogMessageFilter getLogMessageFilter();
}
