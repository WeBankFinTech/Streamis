package com.webank.wedatasphere.streamis.jobmanager.manager.service

import com.webank.wedatasphere.linkis.common.utils.{Logging, Utils}
import com.webank.wedatasphere.streamis.jobmanager.manager.conf.JobConf
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.{StreamBmlMapper, StreamJobCodeResourceMapper, StreamJobMapper, StreamJobRunRelationMapper, StreamProjectMapper}
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.CodeResourceDetailsVO
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.{StreamBml, StreamBmlVersion, StreamJob, StreamJobCodeResource, StreamJobRunRelation, StreamJobVersion}
import com.webank.wedatasphere.streamis.jobmanager.manager.util.{IoUtils, ZipUtils}
import org.apache.commons.lang.StringUtils
import org.codehaus.jackson.`type`.TypeReference
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.UUID
import org.codehaus.jackson.map.ObjectMapper

import java.io.File
import java.util
import scala.tools.nsc.interpreter.InputStream
import scala.collection.JavaConverters._

@Service
class JobCodeService extends Logging{
  @Autowired private var streamJobMapper:StreamJobMapper=_
  @Autowired private var streamBmlMapper:StreamBmlMapper=_
  @Autowired private var streamProjectMapper:StreamProjectMapper=_
  @Autowired private var bmlService:BMLService=_
  @Autowired private var streamJobRunRelationMapper:StreamJobRunRelationMapper=_
  @Autowired private var streamJobCodeResourceMapper:StreamJobCodeResourceMapper=_
  @Autowired private var jobService:JobService=_

  val mapper = new ObjectMapper()

  def addJarBml(userName:String,fileName:String,inputStream:InputStream,projectName:String,jobId:Long,version:String): Unit ={
    info("msg:[add zip resource]")

    val project = streamProjectMapper.getByProjects(null, null, projectName).asScala.head

    var job = new StreamJob()
    val jobVersion = new StreamJobVersion()
    var jobCodeResource = new StreamJobCodeResource()
    var bml = new StreamBml()
    var bmlVersion = new StreamBmlVersion()

    if(jobId>0L && StringUtils.isNotBlank(version)){
      job = streamJobMapper.getJobById(jobId)
      val jobVersionList = streamJobMapper.getJobVersionsById(jobId,version)
      if(jobVersionList == null || jobVersionList.isEmpty){
        info(s"job:${job},version:$version,msg:[job version is null]")
        return
      }
      if(job == null){
        info(s"job:${job},msg:[job is null]")
        return
      }

      job = streamJobMapper.getJobById(jobId)
      val oldJobVersion = jobVersionList.asScala.head

      val newVersion = "v"+jobVersion.getVersion.split("v").last.toInt+1
      job.setCurrentVersion(newVersion)
      streamJobMapper.updateJob(job)

      jobVersion.setJobId(job.getId)
      jobVersion.setVersion(newVersion)
      streamJobMapper.insertJobVersion(jobVersion)

      val jobCodeList = streamJobCodeResourceMapper.getJobCodeList(oldJobVersion.getId, null)
      if(jobCodeList == null || jobCodeList.isEmpty){
        info(s"jobVersionId:${oldJobVersion.getId},msg:[JobCode is null]")
        return
      }

      jobCodeResource = jobCodeList.asScala.head
      val bmlId = jobCodeResource.getBmlId

      jobCodeResource.setJobVersionId(jobVersion.getId)
      streamJobCodeResourceMapper.insert(jobCodeResource)

      bml = streamBmlMapper.getStreamBmlById(bmlId).asScala.head

      bmlVersion = streamBmlMapper.getStreamBmlVersionById(bmlId,bml.getLatestVersion).asScala.head

      val resourceMap = bmlService.update(userName, bmlVersion.getResourceId, inputStream)
      val resourceId = resourceMap.get("resourceId").toString
      val resourceVersion = resourceMap.get("version").toString

      var codeResourceDetailsVO = analyzeResource(userName, resourceId, resourceVersion, JobConf.JOBJAR_EXPORT_URL.getValue, fileName)
      if(codeResourceDetailsVO == null) codeResourceDetailsVO = new CodeResourceDetailsVO
      addBmlVersion(bml.getId,resourceId,resourceVersion,job.getId,fileName,newVersion,codeResourceDetailsVO)

      bml.setName(fileName)
      bml.setLatestVersion(resourceVersion)
      streamBmlMapper.updateBml(bml)

      val preVersionList = streamJobMapper.getJobVersionsById(job.getId,oldJobVersion.getVersion)
      val jobRunRelation = new StreamJobRunRelation()
      jobRunRelation.setJobId(job.getId)
      jobRunRelation.setParentId(preVersionList.asScala.head.getId)
      jobRunRelation.setJobVersionId(jobVersion.getId)

      jobService.addJobRunRelation(job.getStatus.toInt,jobRunRelation)

    }else{
      val rid = UUID.randomUUID().toString
      val resourceMap = bmlService.update(userName, rid, inputStream)
      val resourceId = resourceMap.get("resourceId").toString
      val resourceVersion = resourceMap.get("version").toString
      val jobVersionStr="v1"

      job.setProjectId(project.getId)
      job.setName(fileName)
      job.setCurrentVersion(jobVersionStr)
      job.setCreateBy(userName)
      job.setSubmitUser(userName)
      job.setJobType(JobConf.JOBMANAGER_FLINK_JAR.getValue)
      streamJobMapper.insertJob(job)

      jobVersion.setJobId(job.getId)
      jobVersion.setVersion(jobVersionStr)
      streamJobMapper.insertJobVersion(jobVersion)

      bml.setName(fileName)
      bml.setLatestVersion(resourceVersion)
      streamBmlMapper.insertBml(bml)


      var codeResourceDetailsVO = analyzeResource(userName, resourceId, resourceVersion, JobConf.JOBJAR_EXPORT_URL.getValue, fileName)
      if(codeResourceDetailsVO == null) codeResourceDetailsVO = new CodeResourceDetailsVO
      addBmlVersion(bml.getId,resourceId,resourceVersion,job.getId,fileName,jobVersionStr,codeResourceDetailsVO)

      jobCodeResource.setJobVersionId(jobVersion.getId)
      jobCodeResource.setBmlId(bml.getId)
      streamJobCodeResourceMapper.insert(jobCodeResource)
    }

  }
  @Transactional(rollbackFor = Array(classOf[Exception]))
  private def analyzeResource(userName: String, resourceId: String, version: String,path:String,fileName:String): CodeResourceDetailsVO ={
    info(s"resourceId:${resourceId},version:${version},msg:[start analyze zip resource]")

    val localPath = bmlService.downloadToLocalPath(userName,resourceId,version,path)
    val fullPath = s"$localPath/$fileName"
    ZipUtils.unzip(fullPath+".zip")

    val codeResourceDetailsVO = new CodeResourceDetailsVO()

    Utils.tryAndError {
      val properties = IoUtils.getProperties(s"${fullPath}/${JobConf.JOBJAR_EXPORT_FILE_CONF.getValue}/${JobConf.JOBJAR_RESOURCE_FILENAME.getValue}")

      val mainStr = properties.getProperty(JobConf.JOBJAR_MAIN_JARS.getValue)
      val arguementStr = properties.getProperty(JobConf.JOBJAR_PROGRAM_ARGUEMENT.getValue)
      val dependStr = properties.getProperty(JobConf.JOBJAR_DEPEND_JARS.getValue)
      val resourceStr = properties.getProperty(JobConf.JOBJAR_USER_RESOURCES.getValue)

      util.List[CodeResourceDetailsVO.CodeMain]
      if(StringUtils.isNotBlank(mainStr)) {
        val mainValue: util.List[CodeResourceDetailsVO.CodeMain] = mapper.readValue(mainStr, new TypeReference[util.List[CodeResourceDetailsVO.CodeMain]]() {})
        codeResourceDetailsVO.setMainJars(mainValue)
      }
      if(StringUtils.isNotBlank(arguementStr)) {
        codeResourceDetailsVO.setProgramArguements(mapper.readValue(arguementStr, classOf[String]))
      }

      if(StringUtils.isNotBlank(dependStr)) {
        val dependValue: util.List[CodeResourceDetailsVO.CodeMain] = mapper.readValue(dependStr, new TypeReference[util.List[CodeResourceDetailsVO.CodeMain]]() {})
        codeResourceDetailsVO.setDependJars(dependValue)
      }

      if(StringUtils.isNotBlank(resourceStr)) {
        val resourceValue: util.List[CodeResourceDetailsVO.CodeMain] = mapper.readValue(resourceStr, new TypeReference[util.List[CodeResourceDetailsVO.CodeMain]]() {})
        codeResourceDetailsVO.setUserResources(resourceValue)
      }

    }
    ZipUtils.deleteDir(new File(fullPath))
    ZipUtils.deleteDir(new File(fullPath+".zip"))

    info("msg:[end analyze zip resource]")
    codeResourceDetailsVO
  }

  def addBmlVersion(bmlId:Long,resourceId:String,resourceVersion:String,jobId:Long,jobName:String,jobVersion:String,codeResourceDetailsVO:CodeResourceDetailsVO): Unit ={
    val bmlVersion = new StreamBmlVersion()
    bmlVersion.setBmlId(bmlId)
    bmlVersion.setResourceId(resourceId)
    bmlVersion.setVersion(resourceVersion)

    codeResourceDetailsVO.setJobId(jobId)
    codeResourceDetailsVO.setJobName(jobName)
    codeResourceDetailsVO.setJobVersion(jobVersion)

    val codeResourceJson = mapper.writeValueAsString(codeResourceDetailsVO)

    bmlVersion.setAttribute(codeResourceJson)
    streamBmlMapper.insertBmlVersion(bmlVersion)
  }

  def getCodeDetails(jobId:Long,version:String): CodeResourceDetailsVO ={
    val jobVersionList = streamJobMapper.getJobVersionsById(jobId,version)
    if(jobVersionList == null || jobVersionList.isEmpty){
      info(s"jobId:${jobId},version:${version},msg:[jobVersion is null]")
      return null
    }

    val jobVersion = jobVersionList.asScala.head
    val jobCodeList = streamJobCodeResourceMapper.getJobCodeList(jobVersion.getId,null)
    if(jobVersionList == null || jobVersionList.isEmpty){
      info(s"jobVersionId:${jobVersion.getId},msg:[jobVersion is null]")
      return null
    }

    val jobCode = jobCodeList.asScala.head
    val bml = streamBmlMapper.getStreamBmlById(jobCode.getBmlId).asScala.head
    val bmlVersion = streamBmlMapper.getStreamBmlVersionById(bml.getId,bml.getLatestVersion).asScala.head
    val codeResourceJson = bmlVersion.getAttribute

    val result: CodeResourceDetailsVO = mapper.readValue(codeResourceJson, classOf[CodeResourceDetailsVO])
    result
  }

}
