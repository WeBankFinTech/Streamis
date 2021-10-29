package com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo;

/**
 * 工作流程
 */
public class JobFlowVO {
    private Long jobId;
    private String name;
    private Boolean state;
    private String source;
    private String description;
    private String createTime;
    private String creator;
    private Long projectID;
    private String dssLabels;
    private String resourceId;
    private String bmlVersion;
    private String flowJson;
    private Boolean rootFlow;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Long getProjectID() {
        return projectID;
    }

    public void setProjectID(Long projectID) {
        this.projectID = projectID;
    }

    public String getDssLabels() {
        return dssLabels;
    }

    public void setDssLabels(String dssLabels) {
        this.dssLabels = dssLabels;
    }



    public String getBmlVersion() {
        return bmlVersion;
    }

    public void setBmlVersion(String bmlVersion) {
        this.bmlVersion = bmlVersion;
    }

    public String getFlowJson() {
        return flowJson;
    }

    public void setFlowJson(String flowJson) {
        this.flowJson = flowJson;
    }

    public Boolean getRootFlow() {
        return rootFlow;
    }

    public void setRootFlow(Boolean rootFlow) {
        this.rootFlow = rootFlow;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
