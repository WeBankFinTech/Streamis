package com.webank.wedatasphere.streamis.jobmanager.manager.entity;

/**
 * @author limeng
 */
public class StreamJobSqlResource {
    private Long id;
    private Long jobVersionId;
    private String executeSql;

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

    public String getExecuteSql() {
        return executeSql;
    }

    public void setExecuteSql(String executeSql) {
        this.executeSql = executeSql;
    }
}
