package com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo;

import java.util.List;

/**
 * jar 基本信息
 */
public class JobUploadDetailsVO {

    private List<JarDependentDTO> mainJars;
    private List<JarDependentDTO> dependentList;
    private List<JarDependentDTO> userList;
    private String programArguement;

    public List<JarDependentDTO> getMainJars() {
        return mainJars;
    }

    public void setMainJars(List<JarDependentDTO> mainJars) {
        this.mainJars = mainJars;
    }

    public List<JarDependentDTO> getDependentList() {
        return dependentList;
    }

    public void setDependentList(List<JarDependentDTO> dependentList) {
        this.dependentList = dependentList;
    }


    public String getProgramArguement() {
        return programArguement;
    }

    public void setProgramArguement(String programArguement) {
        this.programArguement = programArguement;
    }

    public List<JarDependentDTO> getUserList() {
        return userList;
    }

    public void setUserList(List<JarDependentDTO> userList) {
        this.userList = userList;
    }

    public static class JarDependentDTO{
        private Long id;
        private String name;
        private String version;
        private String description;
        private String entryClass;
        private String updateTime;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
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

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }


}
