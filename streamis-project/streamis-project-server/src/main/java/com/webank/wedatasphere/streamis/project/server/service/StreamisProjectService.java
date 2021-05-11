package com.webank.wedatasphere.streamis.project.server.service;


import com.webank.wedatasphere.streamis.project.common.CreateStreamProjectRequest;
import com.webank.wedatasphere.streamis.project.common.DeleteStreamProjectRequest;
import com.webank.wedatasphere.streamis.project.common.UpdateStreamProjectRequest;
import com.webank.wedatasphere.streamis.project.server.entity.StreamisProject;
import com.webank.wedatasphere.streamis.project.server.entity.request.CreateProjectRequest;
import com.webank.wedatasphere.streamis.project.server.exception.StreamisProjectErrorException;

/**
 * created by yangzhiyue on 2021/4/6
 * Description:
 */
public interface StreamisProjectService {



     StreamisProject createProject(String username, CreateProjectRequest createProjectRequest) throws StreamisProjectErrorException;

     StreamisProject createProject(CreateStreamProjectRequest createStreamProjectRequest) throws StreamisProjectErrorException;


     void updateProject(UpdateStreamProjectRequest updateStreamProjectRequest) throws StreamisProjectErrorException;


     void deleteProject(DeleteStreamProjectRequest deleteStreamProjectRequest) throws StreamisProjectErrorException;


}
