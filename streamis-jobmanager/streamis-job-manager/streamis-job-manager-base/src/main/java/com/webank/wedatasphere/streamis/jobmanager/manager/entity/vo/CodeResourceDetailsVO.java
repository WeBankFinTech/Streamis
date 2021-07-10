package com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo;

import java.util.List;

public class CodeResourceDetailsVO {

    private Long jobId;
    private String jobVersion;
    private String jobName;

    private List<CodeMain> mainJars;
    private String programArguements;
    private List<CodeMain> dependJars;
    private List<CodeMain> userResources;

    public static class CodeMain{
        private String id;
        private String codeName;
        private String codeVersion;
        private String description;
        private String entryClass;
        private String versionTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCodeName() {
            return codeName;
        }

        public void setCodeName(String codeName) {
            this.codeName = codeName;
        }

        public String getCodeVersion() {
            return codeVersion;
        }

        public void setCodeVersion(String codeVersion) {
            this.codeVersion = codeVersion;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getEntryClass() {
            return entryClass;
        }

        public void setEntryClass(String entryClass) {
            this.entryClass = entryClass;
        }

        public String getVersionTime() {
            return versionTime;
        }

        public void setVersionTime(String versionTime) {
            this.versionTime = versionTime;
        }
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }


    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public List<CodeMain> getMainJars() {
        return mainJars;
    }

    public void setMainJars(List<CodeMain> mainJars) {
        this.mainJars = mainJars;
    }


    public List<CodeMain> getDependJars() {
        return dependJars;
    }

    public void setDependJars(List<CodeMain> dependJars) {
        this.dependJars = dependJars;
    }

    public List<CodeMain> getUserResources() {
        return userResources;
    }

    public void setUserResources(List<CodeMain> userResources) {
        this.userResources = userResources;
    }

    public String getProgramArguements() {
        return programArguements;
    }

    public void setProgramArguements(String programArguements) {
        this.programArguements = programArguements;
    }

    public String getJobVersion() {
        return jobVersion;
    }

    public void setJobVersion(String jobVersion) {
        this.jobVersion = jobVersion;
    }
}
