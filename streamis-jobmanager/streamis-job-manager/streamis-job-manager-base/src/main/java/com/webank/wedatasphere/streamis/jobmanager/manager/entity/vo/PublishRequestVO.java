package com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo;

import java.util.List;

public class PublishRequestVO {

    private Long projectId;
    /**
     * sql或者scala等
     */
    private String type = "sql";

    /**
     * 提交到jobmanager的执行代码
     */
    private String executionCode;

    /**
     * job的名字
     */
    private String streamisJobName;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 任务的描述信息
     */
    private String description;


    /**
     * 标签
     */
    private List<String> tags;


    /**
     * 发布用户
     */
    private String publishUser;

    /**
     * 是用来进行判断是新建一个jobmanager 的任务
     * 传v0001 v0002
     */
    private String version;


    /**
     * 工程名字，必须是要传到的streamis jobmanager
     */
    private String projectName;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExecutionCode() {
        return executionCode;
    }

    public void setExecutionCode(String executionCode) {
        this.executionCode = executionCode;
    }

    public String getStreamisJobName() {
        return streamisJobName;
    }

    public void setStreamisJobName(String streamisJobName) {
        this.streamisJobName = streamisJobName;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getPublishUser() {
        return publishUser;
    }

    public void setPublishUser(String publishUser) {
        this.publishUser = publishUser;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
