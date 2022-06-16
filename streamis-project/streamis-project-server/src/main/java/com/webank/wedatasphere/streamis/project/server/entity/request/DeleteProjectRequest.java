package com.webank.wedatasphere.streamis.project.server.entity.request;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class DeleteProjectRequest {

    private Long projectId;

    /**
     * streamis工程是全局唯一的
     */
    @NotNull(message = "streamis projetName can not null")
    private String projectName;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
