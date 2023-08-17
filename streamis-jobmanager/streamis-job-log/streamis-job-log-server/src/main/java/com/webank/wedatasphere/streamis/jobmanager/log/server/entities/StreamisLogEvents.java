package com.webank.wedatasphere.streamis.jobmanager.log.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class StreamisLogEvents extends com.webank.wedatasphere.streamis.jobmanager.log.entities.StreamisLogEvents {

    @Override
    @JsonIgnore
    public String[] getContents() {
        return super.getContents();
    }
}
