package com.webank.wedatasphere.streamis.jobmanager.log.entities;

import com.webank.wedatasphere.streamis.jobmanager.log.json.JsonTool;

import java.io.Serializable;
import java.util.Objects;

public class StreamisLogEvents implements LogElement, Serializable {

    /**
     * Application name
     */
    private String appName;
    /**
     * Log time
     */
    private long logTimeInMills;

    private StreamisLogEvent[] events;
    public StreamisLogEvents(){

    }
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

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setLogTimeStamp(long logTimeInMills) {
        this.logTimeInMills = logTimeInMills;
    }

    public void setEvents(StreamisLogEvent[] events) {
        this.events = events;
    }

    public void setSequenceId(int sequenceId){
        // Ignore
    }

    public String toJson(){
        return "{" +
                "\"logTimeStamp\":" + logTimeInMills +
                ",\"appName\":" + (Objects.isNull(appName)? null : "\"" + JsonTool.encodeStrValue(appName) + "\"")  +
                ",\"events\":[" +
                (Objects.isNull(events) || events.length <=0 ? "" : joinEvents(events, ",") ) + "]" +
                ",\"sequenceId\":0"
                + "}";
    }

    private String joinEvents(StreamisLogEvent[] events, String separator){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < events.length; i ++){
            builder.append(events[i].toJson());
            if (i < events.length - 1){
                builder.append(separator);
            }
        }
        return builder.toString();
    }
}
