package com.webank.wedatasphere.streamis.jobmanager.manager.service

import com.webank.wedatasphere.linkis.common.utils.Logging
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.StreamProjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @author limeng
 */
@Service
class ProjectService extends Logging{

  @Autowired private var streamProjectMapper:StreamProjectMapper=_

  def getProjects(projectId:Long,workspaceId:Long,projectName:String) ={
    streamProjectMapper.getByProjects(projectId,workspaceId,projectName)
  }

  def getProjects(projectName:String) ={
    streamProjectMapper.getByProjects(null,null,projectName)
  }

}
