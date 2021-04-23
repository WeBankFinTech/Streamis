package com.webank.wedatasphere.streamis.project.server.entity.request;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * created by yangzhiyue on 2021/4/20
 * Description:
 */
@XmlRootElement
public class UpdateProjectRequest {


    private Long id;


    @NotNull(message = "projectName can not be null")
    private String projectName;

    private String description;

    private String creator;



}
