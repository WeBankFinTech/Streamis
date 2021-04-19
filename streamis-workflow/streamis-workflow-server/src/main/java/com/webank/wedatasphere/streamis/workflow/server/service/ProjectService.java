package com.webank.wedatasphere.streamis.workflow.server.service;

import com.webank.wedatasphere.streamis.workflow.server.entity.StreamisProject;
import com.webank.wedatasphere.streamis.workflow.server.exception.StreamisErrorException;

/**
 * created by yangzhiyue on 2021/4/6
 * Description:
 */
public interface ProjectService {

     StreamisProject createProject() throws StreamisErrorException;



     void updateProject() throws StreamisErrorException;


}
