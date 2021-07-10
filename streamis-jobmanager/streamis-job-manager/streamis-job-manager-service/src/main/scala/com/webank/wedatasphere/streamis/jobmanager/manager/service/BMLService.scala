package com.webank.wedatasphere.streamis.jobmanager.manager.service

import com.webank.wedatasphere.linkis.bml.client.{BmlClient, BmlClientFactory}
import com.webank.wedatasphere.linkis.bml.protocol.{BmlDownloadResponse, BmlUpdateResponse, BmlUploadResponse}
import com.webank.wedatasphere.linkis.common.utils.{JavaLog, Utils}
import com.webank.wedatasphere.streamis.jobmanager.manager.exception.JobManagerErrorException
import com.webank.wedatasphere.streamis.jobmanager.manager.util.IoUtils
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Service

import java.io.{ByteArrayInputStream, File, InputStream}
import java.util
import java.util.UUID
import scala.collection.JavaConversions._

@Service
class BMLService extends JavaLog{
  private def createBMLClient(userName:String):BmlClient = if(userName == null) BmlClientFactory.createBmlClient() else BmlClientFactory.createBmlClient(userName)

  def update(userName: String, resourceId: String, inputStream: InputStream): util.Map[String, Object] = {
    val client: BmlClient = createBMLClient(userName)
    val resource: BmlUpdateResponse = client.updateShareResource(userName, resourceId, "", inputStream)
    if (!resource.isSuccess) throw new JobManagerErrorException(30505, "更新失败")
    val map = new util.HashMap[String, Object]
    map += "resourceId" -> resource.resourceId
    map += "version" -> resource.version
  }

  def update(userName: String, resourceId: String, content: String): util.Map[String, Object] = {
    val inputStream = new ByteArrayInputStream(content.getBytes("utf-8"))
    val client: BmlClient = createBMLClient(userName)
    val resource: BmlUpdateResponse = client.updateShareResource(userName, resourceId, UUID.randomUUID().toString+".json", inputStream)
    if (!resource.isSuccess) throw new JobManagerErrorException(30505, "更新失败")
    val map = new util.HashMap[String, Object]
    map += "resourceId" -> resource.resourceId
    map += "version" -> resource.version
  }

  def query(userName: String, resourceId: String, version: String): util.Map[String, Object] = {
    val client: BmlClient = createBMLClient(userName)
    var resource: BmlDownloadResponse = null
    if (version == null) {
      resource = client.downloadShareResource(userName, resourceId)
    } else {
      resource = client.downloadShareResource(userName, resourceId, version)
    }
    if (!resource.isSuccess) throw new JobManagerErrorException(30506, "下载失败")
    val map = new util.HashMap[String, Object]
    map += "path" -> resource.fullFilePath
    map += "fileName" -> pathToName(resource.fullFilePath)
  }

  def download(userName: String, resourceId: String, version: String): util.Map[String, Object] = {
    val client: BmlClient = createBMLClient(userName)
    var resource: BmlDownloadResponse = null
    if (version == null) {
      resource = client.downloadShareResource(userName, resourceId)
    } else {
      resource = client.downloadShareResource(userName, resourceId, version)
    }
    if (!resource.isSuccess) throw new JobManagerErrorException(30506, "下载失败")
    val map = new util.HashMap[String, Object]
    map += "path" -> resource.fullFilePath
    map += "is" -> resource.inputStream
  }

  def downloadToLocalPath(userName: String, resourceId: String, version: String, path: String): String = {
    val result = download(userName,resourceId,version)
    val is = result.get("is").asInstanceOf[InputStream]
    val os = IoUtils.generateExportOutputStream(path)
    Utils.tryFinally(IOUtils.copy(is,os)){
      IOUtils.closeQuietly(os)
      IOUtils.closeQuietly(is)
    }
    path
  }

  def getFiles(path:String,fileName:String): List[String] ={
    import scala.io.Source
    val source = Source.fromFile(path+fileName,"UTF-8")
    val list = source.getLines().toList
    source.close()
    list
  }

  private def inputstremToString(inputStream: InputStream): String = {
    scala.io.Source.fromInputStream(inputStream).mkString
  }
  private def pathToName(filePath:String):String = new File(filePath).getName
}
