package com.webank.wedatasphere.streamis.jobmanager.manager.entity;

import java.util.List;

public class StreamJobRunRelation {
    private Long id;
    private Long jobId;
    private Long jobVersionId;
    private Long parentId;
    private Long level;

    private List<StreamJobRunRelation> childs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Long getJobVersionId() {
        return jobVersionId;
    }

    public void setJobVersionId(Long jobVersionId) {
        this.jobVersionId = jobVersionId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public List<StreamJobRunRelation> getChilds() {
        return childs;
    }

    public void setChilds(List<StreamJobRunRelation> childs) {
        this.childs = childs;
    }
}
