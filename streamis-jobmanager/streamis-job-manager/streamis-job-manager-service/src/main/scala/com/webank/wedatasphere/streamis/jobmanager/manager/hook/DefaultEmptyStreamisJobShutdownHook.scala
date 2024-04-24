package com.webank.wedatasphere.streamis.jobmanager.manager.hook

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.utils.JobUtils
import org.apache.linkis.common.utils.Logging
import org.springframework.stereotype.Component

import java.util
import javax.annotation.PostConstruct

@Component
class DefaultEmptyStreamisJobShutdownHook extends StreamisJobShutdownHook with Logging {

  @PostConstruct
  private def init(): Unit = {
    StreamisJobShutdownHookFactory.registerJobShutdownHook(this)
  }

  override def doBeforeJobShutdown(taskId: String, projectName: String, jobName: String, timeoutMills: Long, params: util.Map[String, AnyRef]): Unit = {
    Thread.sleep(1000L)
    logger.info(s"hook : ${getName} called doBeforeJobShutdown(${taskId}, ${projectName}, ${jobName}, ${timeoutMills}), params : " +
      JobUtils.gson.toJson(params))
  }

  override def doAfterJobShutdown(taskId: String, projectName: String, jobName: String, timeoutMills: Long, params: util.Map[String, AnyRef]): Unit = {
    Thread.sleep(1000L)
    logger.info(s"hook : ${getName} called doAfterJobShutdown(${taskId}, ${projectName}, ${jobName}, ${timeoutMills}), params : " +
      JobUtils.gson.toJson(params))
  }

  override def getName: String = "emptyJobShutdownHook"

}
