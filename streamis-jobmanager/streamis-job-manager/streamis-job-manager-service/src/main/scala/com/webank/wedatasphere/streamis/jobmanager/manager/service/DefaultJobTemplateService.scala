package com.webank.wedatasphere.streamis.jobmanager.manager.service

import com.google.gson.JsonParser
import com.webank.wedatasphere.streamis.jobmanager.manager.constrants.JobConstrants
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.JobTemplateFiles
import org.springframework.stereotype.Service

@Service
class DefaultJobTemplateService extends StreamJobTemplateService {

  override def generateJobTemplate(jobTemplate: JobTemplateFiles): String = {
    val jsonObj = new JsonParser().parse(jobTemplate.getMetaJson).getAsJsonObject
    if (jsonObj.has(JobConstrants.FIELD_METAINFO_NAME)) {
      jsonObj.remove(JobConstrants.FIELD_METAINFO_NAME)
    }
    val parsedConfigJson = jsonObj.toString
    parsedConfigJson
  }

}
