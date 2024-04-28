package com.webank.wedatasphere.streamis.projectmanager.utils

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.exception.JobCreateErrorException
import com.webank.wedatasphere.streamis.jobmanager.launcher.service.StreamJobConfService
import com.webank.wedatasphere.streamis.jobmanager.manager.service.StreamJobService
import com.webank.wedatasphere.streamis.jobmanager.manager.utils.JobContentUtils
import org.apache.commons.lang.StringUtils

import java.util
import java.util.HashMap

object JobRefreshUtils {

  def refreshJobConfig(streamJobService: StreamJobService,streamJobConfService: StreamJobConfService,projectName: String,jobId: Long): util.Map[String, Object] = {
    val jobTemplateConfig: Option[String] = {
      val jobTemplate = streamJobService.getLatestJobTemplate(projectName)
      if (StringUtils.isNotBlank(jobTemplate)) {
        Some(JobContentUtils.getJobTemplateConfig(jobTemplate))
      } else {
        None
      }
    }
    val jobConfigMap: util.HashMap[String, AnyRef] = new util.HashMap[String, AnyRef](streamJobConfService.getJobConfig(jobId))
    var finaljobTemplateConfig = jobTemplateConfig.orNull
    if (jobConfigMap.isEmpty && StringUtils.isBlank(jobTemplateConfig.orNull)) {
      throw new JobCreateErrorException(30030, s"jobConfig is needed, both jobConfig and jobTemplateConfig are empty.")
    } else if (StringUtils.isNotBlank(jobTemplateConfig.orNull) && jobConfigMap.isEmpty) {
      val jobConfig = JobContentUtils.getFinalJobConfig(jobConfigMap, jobTemplateConfig.orNull)
      finaljobTemplateConfig = JobContentUtils.gson.toJson(jobConfig)
    } else if (StringUtils.isBlank(jobTemplateConfig.orNull) && !jobConfigMap.isEmpty ) {
      finaljobTemplateConfig = JobContentUtils.gson.toJson(jobConfigMap)
    }
    val finalJobConfig = JobContentUtils.getMap(finaljobTemplateConfig)
    finalJobConfig
  }
}
