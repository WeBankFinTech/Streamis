package com.webank.wedatasphere.streamis.jobmanager.manager.utils

import com.google.gson.{GsonBuilder, JsonElement, JsonPrimitive, JsonSerializationContext, JsonSerializer}
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJobVersion
import org.apache.commons.lang.StringUtils
import org.apache.linkis.common.utils.JsonUtils

import java.lang.reflect.Type
import java.{lang, util}
import java.util.{Map => JavaMap}

object JobContentUtils {

  def isAnyVal[T](x: T)(implicit m: Manifest[T]) = m <:< manifest[AnyVal]

  def getFinalJobContent(jobVersion: StreamJobVersion,jobTemplate: String): JavaMap[String, Object] ={
    val jobContent = JsonUtils.jackson.readValue(jobVersion.getJobContent, classOf[util.Map[String, Object]])
    if (StringUtils.isNotBlank(jobTemplate)){
      val metaJsonTemplate = JsonUtils.jackson.readValue(jobTemplate, classOf[util.Map[String, Object]])
      val jobContentTemplate = metaJsonTemplate.get("jobContent").asInstanceOf[JavaMap[String, Object]]
      val finalJobContent: JavaMap[String, Object] = new java.util.HashMap[String, Object](jobContentTemplate)
      finalJobContent.putAll(jobContent)
      finalJobContent
    }else{
      jobContent
    }
  }

  def getFinalJobContent(jobContent: String,jobTemplate: String): JavaMap[String, Object] ={
    val jobContentMap = JsonUtils.jackson.readValue(jobContent, classOf[util.Map[String, Object]])
    if (StringUtils.isNotBlank(jobTemplate)){
      val metaJsonTemplate = JsonUtils.jackson.readValue(jobTemplate, classOf[util.Map[String, Object]])
//      val jobContentTemplate = metaJsonTemplate.get("jobContent").asInstanceOf[JavaMap[String, Object]]
      val finalJobContent: JavaMap[String, Object] = new java.util.HashMap[String, Object](metaJsonTemplate)
      finalJobContent.putAll(jobContentMap)
      finalJobContent
    }else{
      jobContentMap
    }
  }

  def getJobTemplateContent(jobTemplate: String): String ={
    val metaJsonTemplate = JsonUtils.jackson.readValue(jobTemplate, classOf[util.Map[String, Object]])
    val jobContentTemplate = metaJsonTemplate.get("jobContent").asInstanceOf[JavaMap[String, Object]]
    JobContentUtils.gson.toJson(jobContentTemplate)
  }

  def getJobTemplateConfig(jobTemplate: String): String = {
    val metaJsonTemplate = JsonUtils.jackson.readValue(jobTemplate, classOf[util.Map[String, Object]])
    val jobConfigTemplate = metaJsonTemplate.get("jobConfig").asInstanceOf[JavaMap[String, Object]]
    JobContentUtils.gson.toJson(jobConfigTemplate)
  }

  def getFinalJobConfig(jobConfigMap: JavaMap[String, Object], jobTemplate: String): JavaMap[String, Object] = {
//    val jobConfigMap = JsonUtils.jackson.readValue(jobConfig, classOf[util.Map[String, Object]])
    if (StringUtils.isNotBlank(jobTemplate)) {
      val metaJsonTemplate = JsonUtils.jackson.readValue(jobTemplate, classOf[util.Map[String, Object]])
//      val jobConfigTemplate = metaJsonTemplate.get("jobContent").asInstanceOf[JavaMap[String, Object]]
      val finalJobConfig: JavaMap[String, Object] = new java.util.HashMap[String, Object](metaJsonTemplate)
      MergeUtils.merge(finalJobConfig,jobConfigMap)
      finalJobConfig
    } else {
      jobConfigMap
    }
  }

  val gson = new GsonBuilder()
    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    .serializeNulls
    .registerTypeAdapter(
      classOf[java.lang.Double],
      new JsonSerializer[java.lang.Double] {

        override def serialize(
                                t: lang.Double,
                                `type`: Type,
                                jsonSerializationContext: JsonSerializationContext
                              ): JsonElement =
          if (t == t.longValue()) new JsonPrimitive(t.longValue()) else new JsonPrimitive(t)

      }
    )
    .create

  def getMap(json: String): JavaMap[String, Object] = {
    val map = JsonUtils.jackson.readValue(json, classOf[util.Map[String, Object]])
    map
  }
}
