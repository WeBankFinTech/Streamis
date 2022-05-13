package com.webank.wedatasphere.streamis.project.server.entity;

import java.util.List;

public class CreateProjectPrivilege {
    private Long id;
    private Long projectId;
    private List<String> userNames;
    private int privilege;

    public CreateProjectPrivilege(Long projectId, List<String> userNames, int privilege) {
        this.projectId = projectId;
        this.userNames = userNames;
        this.privilege = privilege;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(List<String> userNames) {
        this.userNames = userNames;
    }

    public int getPrivilege() {
        return privilege;
    }

    public void setPrivilege(int privilege) {
        this.privilege = privilege;
    }
}
