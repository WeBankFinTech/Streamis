package com.webank.wedatasphere.streamis.jobmanager.manager.service

import com.webank.wedatasphere.streamis.jobmanager.manager.entity.JobTemplateFiles

trait StreamJobTemplateService {


  def generateJobTemplate(jobTemplate: JobTemplateFiles): String


}
