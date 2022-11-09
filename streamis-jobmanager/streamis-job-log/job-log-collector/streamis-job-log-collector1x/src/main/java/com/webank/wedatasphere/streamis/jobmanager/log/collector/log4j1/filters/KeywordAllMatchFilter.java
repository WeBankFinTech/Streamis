package com.webank.wedatasphere.streamis.jobmanager.log.collector.log4j1.filters;

import com.webank.wedatasphere.streamis.jobmanager.log.collector.message.filters.KeywordMessageFilter;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.message.filters.LogMessageFilter;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.message.filters.LogMessageFilterAdapter;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 *  All match filter with keyword
 */
public class KeywordAllMatchFilter extends Filter implements LogMessageFilterAdapter {

    /**
     * Message filter
     */
    private final KeywordMessageFilter messageFilter;

    public KeywordAllMatchFilter(String[] acceptKeywords, String[] excludeKeywords){
        this.messageFilter = new KeywordMessageFilter(acceptKeywords, excludeKeywords);
    }
    @Override
    public int decide(LoggingEvent event) {
        return Filter.ACCEPT;
    }

    @Override
    public LogMessageFilter getLogMessageFilter() {
        return this.messageFilter;
    }
}
