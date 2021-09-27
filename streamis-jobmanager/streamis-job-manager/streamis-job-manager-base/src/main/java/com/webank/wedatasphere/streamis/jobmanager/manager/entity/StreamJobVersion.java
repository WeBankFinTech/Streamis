package com.webank.wedatasphere.streamis.jobmanager.manager.entity;

/**
 * @author limeng
 */
public class StreamJobVersion {
    private Long id;
    private Long jobId;
    private String version;
    private String programArguments;
    private String bmlVersion;
    private String bmlId;

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProgramArguments() {
        return programArguments;
    }

    public void setProgramArguments(String programArguments) {
        this.programArguments = programArguments;
    }

    public String getBmlVersion() {
        return bmlVersion;
    }

    public void setBmlVersion(String bmlVersion) {
        this.bmlVersion = bmlVersion;
    }

    public String getBmlId() {
        return bmlId;
    }

    public void setBmlId(String bmlId) {
        this.bmlId = bmlId;
    }
}
