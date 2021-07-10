package com.webank.wedatasphere.streamis.jobmanager.manager.entity;

public class StreamJobCodeResource {
    private Long id;
    private Long jobVersionId;
    private Long bmlId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobVersionId() {
        return jobVersionId;
    }

    public void setJobVersionId(Long jobVersionId) {
        this.jobVersionId = jobVersionId;
    }


    public Long getBmlId() {
        return bmlId;
    }

    public void setBmlId(Long bmlId) {
        this.bmlId = bmlId;
    }
}
