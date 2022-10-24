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
    private long logTimeInMills;

    /**
     * Log content
     */
    private String content;

    /**
     * Mark
     */
    private int mark;

    public StreamisLogEvent(){

    }
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

    public void setLogTimeStamp(long logTimeInMills) {
        this.logTimeInMills = logTimeInMills;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public void setSequenceId(int sequenceId){
        // Ignore
    }
}
