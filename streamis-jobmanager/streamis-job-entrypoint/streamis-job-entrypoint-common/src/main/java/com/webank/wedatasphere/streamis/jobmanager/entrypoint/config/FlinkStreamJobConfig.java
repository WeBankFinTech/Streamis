package com.webank.wedatasphere.streamis.jobmanager.entrypoint.config;

public class FlinkStreamJobConfig extends StreamJobConfig {

    private String applicationUrl;

    private String jobId;

    public String getApplicationUrl() {
        return applicationUrl;
    }

    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
