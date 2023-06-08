package com.webank.wedatasphere.streamis.jobmanager.launcher.entity.vo;

public class YarnAppVo {

    /*
    Be same with org.apache.linkis.governance.common.constant.ec.ECConstants
     */

    private String applicationId;

    private String applicationUrl;

    private String applicationState;

    private String appicationName;

    private String yarnAppType;

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

    public String getAppicationName() {
        return appicationName;
    }

    public void setAppicationName(String appicationName) {
        this.appicationName = appicationName;
    }

    public String getYarnAppType() {
        return yarnAppType;
    }

    public void setYarnAppType(String yarnAppType) {
        this.yarnAppType = yarnAppType;
    }
}