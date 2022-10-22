package com.webank.wedatasphere.streamis.jobmanager.log.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class StreamisLogEvents implements LogElement, Serializable {

    /**
     * Application name
     */
    private String appName;
    /**
     * Log time
     */
    private final long logTimeInMills;

    private final StreamisLogEvent[] events;

    public StreamisLogEvents(String applicationName, StreamisLogEvent[] events){
        this.appName = applicationName;
        this.events = events;
        long maxTime = -1;
        StreamisLogEvent lastEvent = events[events.length - 1];
        if (null == lastEvent) {
            for (StreamisLogEvent event : events) {
                long time = event.getLogTimeStamp();
                if (time > maxTime) {
                    maxTime = time;
                }
            }
            this.logTimeInMills = maxTime;
        }else {
            this.logTimeInMills = lastEvent.getLogTimeStamp();
        }

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
        String[] contents = new String[events.length];
        for(int i = 0 ; i < contents.length; i++){
            contents[i] = events[i].getContent();
        }
        return contents;
    }

    @Override
    public int mark() {
        return 1;
    }

    public String getAppName() {
        return appName;
    }

    public StreamisLogEvent[] getEvents() {
        return events;
    }
}
