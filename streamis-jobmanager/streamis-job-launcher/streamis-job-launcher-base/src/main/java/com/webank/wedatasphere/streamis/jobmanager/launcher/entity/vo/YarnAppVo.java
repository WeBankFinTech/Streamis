package com.webank.wedatasphere.streamis.jobmanager.launcher.entity.vo;

public class YarnAppVo {

    private String applicationId;

    private String applicationUrl;

    private String applicationState;

    public YarnAppVo() {}

    public YarnAppVo(String appId, String appUrl, String appState) {
        this.setApplicationId(appId);
        this.setApplicationUrl(appUrl);
        this.setApplicationState(appState);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationUrl() {
        return applicationUrl;
    }

    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }

    public String getApplicationState() {
        return applicationState;
    }

    public void setApplicationState(String applicationState) {
        this.applicationState = applicationState;
    }
}