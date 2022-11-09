package com.webank.wedatasphere.streamis.jobmanager.log.collector.log4j2.filters;

import com.webank.wedatasphere.streamis.jobmanager.log.collector.message.filters.KeywordMessageFilter;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.message.filters.LogMessageFilter;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.message.filters.LogMessageFilterAdapter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

import java.util.Optional;

/**
 * Threshold filter with keyword
 */
public class KeywordThresholdFilter extends AbstractFilter implements LogMessageFilterAdapter {

    /**
     * Level
     */
    private final Level level;

    /**
     * Message filter
     */
    private final KeywordMessageFilter messageFilter;
    public KeywordThresholdFilter(String[] acceptKeywords, String[] excludeKeywords){
        // Use accept and deny match
        super(Filter.Result.ACCEPT, Filter.Result.DENY);
        // If accept keywords is empty, set the log level to warn
        if (null == acceptKeywords || acceptKeywords.length <= 0){
            this.level = Level.WARN;
            System.out.println("The keywords is empty, set the log threshold level >= " + this.level);
        } else {
            this.level = Level.ALL;
        }
        this.messageFilter = new KeywordMessageFilter(acceptKeywords, excludeKeywords);
    }

    @Override
    public Result filter(LogEvent event) {
        return filter(event.getLevel());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
        return filter(level);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
        return filter(level);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
        return filter(level);
    }

    private Result filter(final Level level){
        return level.isMoreSpecificThan(this.level) ? onMatch : onMismatch;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return level.toString() +
                "|acceptKeywords:[" +
                Optional.ofNullable(this.messageFilter.getAcceptKeywords()).orElse(new String[]{}).length +
                "]|excludeKeywords:[" +
                Optional.ofNullable(this.messageFilter.getExcludeKeywords()).orElse(new String[]{}).length + "]" ;
    }

    @Override
    public LogMessageFilter getLogMessageFilter() {
        return this.messageFilter;
    }
}
