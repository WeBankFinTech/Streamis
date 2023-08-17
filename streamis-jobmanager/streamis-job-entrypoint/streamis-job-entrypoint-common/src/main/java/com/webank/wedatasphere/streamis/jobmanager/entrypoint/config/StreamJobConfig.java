package com.webank.wedatasphere.streamis.jobmanager.entrypoint.config;

public class StreamJobConfig {

    private String streamisServerUrl;

    private String applicationId;

    private String resourceManagerAddress;

    private String jobStatus;

    private String jobName;

    private String projectName;

    public String getStreamisServerUrl() {
        return streamisServerUrl;
    }

    public void setStreamisServerUrl(String streamisServerUrl) {
        this.streamisServerUrl = streamisServerUrl;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getResourceManagerAddress() {
        return resourceManagerAddress;
    }

    public void setResourceManagerAddress(String resourceManagerAddress) {
        this.resourceManagerAddress = resourceManagerAddress;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public String toString() {
        return "StreamJobConfig{" +
                "streamisServerUrl='" + streamisServerUrl + '\'' +
                ", applicationId='" + applicationId + '\'' +
                ", resourceManagerAddress='" + resourceManagerAddress + '\'' +
                ", jobStatus='" + jobStatus + '\'' +
                ", jobName='" + jobName + '\'' +
                ", projectName='" + projectName + '\'' +
                '}';
    }
}

