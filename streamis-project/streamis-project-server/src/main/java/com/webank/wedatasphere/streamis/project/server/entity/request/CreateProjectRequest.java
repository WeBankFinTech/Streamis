package com.webank.wedatasphere.streamis.project.server.entity.request;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

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


    private String tags;

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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }



}
