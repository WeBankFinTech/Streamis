package com.webank.wedatasphere.streamis.jobmanager.vo;

import java.util.List;

public class UpdateContentRequest {
    private  Long jobId ;

    private  String version;

    private  List<String> args;

    private Boolean isHighAvailable;

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

    public Boolean getHighAvailable() {
        return isHighAvailable;
    }

    public void setHighAvailable(Boolean highAvailable) {
        isHighAvailable = highAvailable;
    }

    public String getHighAvailableMessage() {
        return highAvailableMessage;
    }

    public void setHighAvailableMessage(String highAvailableMessage) {
        this.highAvailableMessage = highAvailableMessage;
    }
}
