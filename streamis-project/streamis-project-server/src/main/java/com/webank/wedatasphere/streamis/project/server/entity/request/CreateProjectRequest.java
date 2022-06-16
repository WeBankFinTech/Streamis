package com.webank.wedatasphere.streamis.project.server.entity.request;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * created by yangzhiyue on 2021/4/20
 * Description:
 */
@XmlRootElement
public class CreateProjectRequest {

    @NotNull(message = "projectName can not be null")
    private String projectName;

    @NotNull(message = "description can not be null")
    private String description;

    @NotNull(message = "workspaceName can not be null")
    private String workspaceName;

    private String tags;

    private List<String> accessUsers;

    private List<String> editUsers;

    private List<String> releaseUsers;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<String> getAccessUsers() {
        return accessUsers;
    }

    public void setAccessUsers(List<String> accessUsers) {
        this.accessUsers = accessUsers;
    }

    public List<String> getEditUsers() {
        return editUsers;
    }

    public void setEditUsers(List<String> editUsers) {
        this.editUsers = editUsers;
    }

    public List<String> getReleaseUsers() {
        return releaseUsers;
    }

    public void setReleaseUsers(List<String> releaseUsers) {
        this.releaseUsers = releaseUsers;
    }
}
