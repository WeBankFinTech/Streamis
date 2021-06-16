package com.webank.wedatasphere.streamis.workflow.server.entity;

/**
 * created by yangzhiyue on 2021/4/26
 * Description:
 */
public class ExportResponse {

    private String resourceId;

    private String version;

    public ExportResponse(String resourceId, String version) {
        this.resourceId = resourceId;
        this.version = version;
    }


    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
