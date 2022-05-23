package com.webank.wedatasphere.streamis.project.server.entity;

import java.util.List;

public class StreamisProjectPrivilege {
    private Long id;
    private Long projectId;
    private String userName;
    private int privilege;

    public StreamisProjectPrivilege(Long projectId, String userName, int privilege) {
        this.projectId = projectId;
        this.userName = userName;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPrivilege() {
        return privilege;
    }

    public void setPrivilege(int privilege) {
        this.privilege = privilege;
    }
}
