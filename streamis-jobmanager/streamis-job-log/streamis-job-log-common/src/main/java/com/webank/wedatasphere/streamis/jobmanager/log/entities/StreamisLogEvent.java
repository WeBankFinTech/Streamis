package com.webank.wedatasphere.streamis.jobmanager.log.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Log event for streamis
 */
public class StreamisLogEvent implements LogElement, Serializable {

    /**
     * Log time
     */
    private final long logTimeInMills;

    /**
     * Log content
     */
    private final String content;

    /**
     * Mark
     */
    private int mark;

    public StreamisLogEvent(String content, long logTimeInMills){
        this.content = content;
        this.logTimeInMills = logTimeInMills;
    }
    @Override
    public int getSequenceId() {
        return 0;
    }

    @Override
    public long getLogTimeStamp() {
        return this.logTimeInMills;
    }

    @Override
    @JsonIgnore
    public String[] getContents() {
        return new String[]{content};
    }

    public String getContent() {
        return content;
    }

    @Override
    public int mark() {
        return this.mark;
    }

    public void mark(int mark){
        this.mark = mark;
    }
}
