package com.webank.wedatasphere.streamis.project.server.service;


import com.webank.wedatasphere.streamis.project.server.entity.StreamisProject;
import com.webank.wedatasphere.streamis.project.server.entity.request.CreateProjectRequest;
import com.webank.wedatasphere.streamis.project.server.exception.StreamisProjectErrorException;

/**
 * created by yangzhiyue on 2021/4/6
 * Description:
 */
public interface ProjectService {

     StreamisProject createProject(String username, CreateProjectRequest createProjectRequest) throws StreamisProjectErrorException;



     void updateProject() throws StreamisProjectErrorException;


     void deleteProject() throws StreamisProjectErrorException;


}
