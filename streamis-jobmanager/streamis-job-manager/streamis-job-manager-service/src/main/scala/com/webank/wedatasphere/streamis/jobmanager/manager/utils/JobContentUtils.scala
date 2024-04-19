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

  def getJobTemplateContent(jobTemplate: String): JavaMap[String, Object] ={
    val metaJsonTemplate = JsonUtils.jackson.readValue(jobTemplate, classOf[util.Map[String, Object]])
    val jobContentTemplate = metaJsonTemplate.get("jobContent").asInstanceOf[JavaMap[String, Object]]
    jobContentTemplate
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
}
