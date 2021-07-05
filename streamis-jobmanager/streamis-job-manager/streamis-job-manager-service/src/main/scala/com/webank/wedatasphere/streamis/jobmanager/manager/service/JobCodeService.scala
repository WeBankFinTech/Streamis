package com.webank.wedatasphere.streamis.jobmanager.manager.service

import com.webank.wedatasphere.linkis.common.utils.Logging
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.{StreamBmlMapper, StreamJobMapper, StreamJobRunRelationMapper, StreamProjectMapper}
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.util.UUID
import scala.tools.nsc.interpreter.InputStream
import scala.collection.JavaConverters._
@Service
class JobCodeService extends Logging{
  @Autowired private var streamJobMapper:StreamJobMapper=_
  @Autowired private var streamBmlMapper:StreamBmlMapper=_
  @Autowired private var streamProjectMapper:StreamProjectMapper=_
  @Autowired private var bmlService:BMLService=_
  @Autowired private var streamJobRunRelationMapper:StreamJobRunRelationMapper=_


  def addJarBml(userName:String,inputStream:InputStream,projectName:String,jobId:Long,version:String): Unit ={
    info("add zip resource")
    val rid = UUID.randomUUID().toString
    info(s"resourceId ${rid}")
    val projects = streamProjectMapper.getByProjects(null, null, projectName).asScala.head

    if(jobId>0L && StringUtils.isNotBlank(version)){
      val jobVersionList = streamJobMapper.getJobVersionsById(jobId,version)
      if(jobVersionList == null || jobVersionList.isEmpty){
        info("get job version is null")
        return
      }
    }else{

    }

    val resourceMap = bmlService.update(userName, rid, inputStream)


  }

  private def addJarBml(userName:String,inputStream:InputStream,projectName:String): Unit ={

  }

  private def updateJarBml(userName:String,inputStream:InputStream,jobId:Long,version:String): Unit ={

  }
}
