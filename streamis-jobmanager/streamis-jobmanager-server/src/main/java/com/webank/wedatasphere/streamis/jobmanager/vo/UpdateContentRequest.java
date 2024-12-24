package com.webank.wedatasphere.streamis.jobmanager.vo;

import java.util.List;

public class UpdateContentRequest {
    private  Long jobId ;

    private  String version;

    private  List<String> args;

    private boolean highAvailable;

    private String highAvailableMessage;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public boolean isHighAvailable() {
        return highAvailable;
    }

    public void setHighAvailable(boolean highAvailable) {
        this.highAvailable = highAvailable;
    }

    public String getHighAvailableMessage() {
        return highAvailableMessage;
    }

    public void setHighAvailableMessage(String highAvailableMessage) {
        this.highAvailableMessage = highAvailableMessage;
    }
}
