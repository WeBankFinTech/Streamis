package com.webank.wedatasphere.streamis.jobmanager.manager.hook

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.utils.JobUtils
import org.apache.linkis.common.utils.Logging

import java.util

class DefaultEmptyJobShutdownHook extends StreamisJobShutdowHook with Logging {
  override def doBeforeJobShutdown(jobId: String, projectName: String, jobName: String, timeoutMills: Int, params: util.Map[String, AnyRef]): Unit = {
    Thread.sleep(1000L)
    logger.info(s"hook : ${getName} called doBeforeJobShutdown(${jobId}, ${projectName}, ${jobName}, ${timeoutMills}), params : " +
      JobUtils.gson.toJson(params))
  }

  override def doAfterJobShutdown(jobId: String, projectName: String, jobName: String, timeoutMills: Int, params: util.Map[String, AnyRef]): Unit = {
    Thread.sleep(1000L)
    logger.info(s"hook : ${getName} called doAfterJobShutdown(${jobId}, ${projectName}, ${jobName}, ${timeoutMills}), params : " +
      JobUtils.gson.toJson(params))
  }

  override def getName: String = "emptyJobShutdownHook"

}
