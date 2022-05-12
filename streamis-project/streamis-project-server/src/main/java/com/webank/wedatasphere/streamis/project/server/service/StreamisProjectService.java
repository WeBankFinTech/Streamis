package com.webank.wedatasphere.streamis.project.server.service;


import com.webank.wedatasphere.streamis.project.common.CreateStreamProjectRequest;
import com.webank.wedatasphere.streamis.project.server.entity.StreamisProject;
import com.webank.wedatasphere.streamis.project.server.entity.request.CreateProjectRequest;
import com.webank.wedatasphere.streamis.project.server.entity.request.DeleteProjectRequest;
import com.webank.wedatasphere.streamis.project.server.entity.request.UpdateProjectRequest;
import com.webank.wedatasphere.streamis.project.server.exception.StreamisProjectErrorException;

/**
 * Description:
 */
public interface StreamisProjectService {

     StreamisProject createProject(String username, CreateProjectRequest createProjectRequest) throws StreamisProjectErrorException;

     StreamisProject createProject(CreateStreamProjectRequest createStreamProjectRequest) throws StreamisProjectErrorException;

     void updateProject(String username, UpdateProjectRequest updateProjectRequest) throws StreamisProjectErrorException;

     void deleteProject(DeleteProjectRequest deleteProjectRequest) throws StreamisProjectErrorException;

}
