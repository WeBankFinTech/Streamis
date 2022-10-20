package com.webank.wedatasphere.streamis.jobmanager.log.entities;

public class StreamisLogEvents implements LogElement{
    @Override
    public int getSequenceId() {
        return 0;
    }

    @Override
    public long getLogTimeStamp() {
        return 0;
    }


    @Override
    public String[] getContents() {
        return new String[0];
    }

    @Override
    public int mark() {
        return 0;
    }
}
