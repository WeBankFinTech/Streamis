package com.webank.wedatasphere.streamis.jobmanager.vo;

import java.util.List;

public class UpdateContentRequest {
    private  Long jobId ;

    private  String version;

    private  List<String> args;

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
}
