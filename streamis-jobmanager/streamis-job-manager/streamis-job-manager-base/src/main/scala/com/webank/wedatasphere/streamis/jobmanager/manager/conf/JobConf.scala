package com.webank.wedatasphere.streamis.jobmanager.manager.conf

import com.webank.wedatasphere.linkis.common.conf.{CommonVars, TimeType}
import com.webank.wedatasphere.streamis.jobmanager.manager.exception.JobExecuteFailedErrorException

object JobConf {
  val JOBMANAGER_FLINK_JOB_STATUS_ONE = CommonVars("wds.streamis.job.status.one", 1,"Completed")

  val JOBMANAGER_FLINK_JOB_STATUS_TWO = CommonVars("wds.streamis.job.status.two", 2,"Wait for restart")

  val JOBMANAGER_FLINK_JOB_STATUS_THREE = CommonVars("wds.streamis.job.status.three", 3,"Alert running")

  val JOBMANAGER_FLINK_JOB_STATUS_FOUR = CommonVars("wds.streamis.job.status.four", 4,"Slow running")

  val JOBMANAGER_FLINK_JOB_STATUS_FIVE = CommonVars("wds.streamis.job.status.five", 5,"running")

  val JOBMANAGER_FLINK_JOB_STATUS_SIX = CommonVars("wds.streamis.job.status.six", 6,"Failed")

  val JOBMANAGER_FLINK_JOB_STATUS_SEVEN = CommonVars("wds.streamis.job.status.seven", 7,"Stopped")

  val STATUS_ARRAY = Array(JOBMANAGER_FLINK_JOB_STATUS_ONE, JOBMANAGER_FLINK_JOB_STATUS_TWO, JOBMANAGER_FLINK_JOB_STATUS_THREE,
    JOBMANAGER_FLINK_JOB_STATUS_FOUR, JOBMANAGER_FLINK_JOB_STATUS_FIVE, JOBMANAGER_FLINK_JOB_STATUS_SIX, JOBMANAGER_FLINK_JOB_STATUS_SEVEN)

  val NOT_COMPLETED_STATUS_ARRAY = Array(JOBMANAGER_FLINK_JOB_STATUS_TWO, JOBMANAGER_FLINK_JOB_STATUS_THREE, JOBMANAGER_FLINK_JOB_STATUS_FOUR,
    JOBMANAGER_FLINK_JOB_STATUS_FIVE)

  def isCompleted(status: Int): Boolean = status match {
    case 1 | 6 | 7 => true
    case _ => false
  }

  def linkisStatusToStreamisStatus(status: String): Int = status.toLowerCase match {
    case "starting" | "unlock" | "locked" | "idle" | "busy" | "running" => JOBMANAGER_FLINK_JOB_STATUS_FIVE.getValue
    case "success" => JOBMANAGER_FLINK_JOB_STATUS_ONE.getValue
    case "failed" | "shuttingdown" => JOBMANAGER_FLINK_JOB_STATUS_SIX.getValue
  }

  def getStatusString(status: Int): String = STATUS_ARRAY.find(_.getValue == status).map(_.description)
    .getOrElse(throw new JobExecuteFailedErrorException(30351, s"Unknown status $status."))

  val JOBMANAGER_FLINK_SQL = CommonVars("wds.streamis.job.type.sql", 1,"flink type sql")
  val JOBMANAGER_FLINK_JAR = CommonVars("wds.streamis.job.type.jar", 2,"flink type jar")

  val TASK_MONITOR_INTERVAL = CommonVars("wds.streamis.task.monitor.interval", new TimeType("1m"))

  /**
   * zip template
   *      ->conf->linkis.properties
   * zip->
   *      ->lib->
   *
   * wds.streamis.job.jar.program.arguement=--hostname localhost  --port 12345
   * wds.streamis.job.jar.main=[{"id":"1","codeName":"flink-mian.jar","codeVersion":"v1","description":"测试","entryClass":"main","versionTime":"2020"},{"id":"2","codeName":"flink-mian2.jar","codeVersion":"v1","description":"测试2","entryClass":"main2","versionTime":"2020"}]
   * wds.streamis.job.jar.depend=[{"id":"1","codeName":"flink-mian.jar","codeVersion":"v1","description":"测试","entryClass":"main","versionTime":"2020"},{"id":"2","codeName":"flink-mian2.jar","codeVersion":"v1","description":"测试2","entryClass":"main2","versionTime":"2020"}]
   * wds.streamis.job.jar.user.resources=[{"id":"1","codeName":"flink-mian.jar","codeVersion":"v1","description":"测试","entryClass":"main","versionTime":"2020"},{"id":"2","codeName":"flink-mian2.jar","codeVersion":"v1","description":"测试2","entryClass":"main2","versionTime":"2020"}]
   *
   */
  val JOBJAR_EXPORT_URL = CommonVars("wds.streamis.job.export.url","/opt/tmp/streamis/job")

  val JOBJAR_EXPORT_FILE_CONF = CommonVars("wds.streamis.job.export.conf","conf")
  val JOBJAR_EXPORT_FILE_LIB = CommonVars("wds.streamis.job.export.lib","lib")


  val JOBJAR_RESOURCE_FILENAME = CommonVars("wds.streamis.job.jar.resource.conf.name", "linkis.properties")
  val JOBJAR_MAIN_JARS =  CommonVars("wds.streamis.job.jar.main", "wds.streamis.job.jar.main")
  val JOBJAR_PROGRAM_ARGUEMENT =  CommonVars("wds.streamis.job.jar.program.arguement", "wds.streamis.job.jar.program.arguement")
  val JOBJAR_DEPEND_JARS =  CommonVars("wds.streamis.job.jar.depend", "wds.streamis.job.jar.depend")
  val JOBJAR_USER_RESOURCES =  CommonVars("wds.streamis.job.jar.user.resources", "wds.streamis.job.jar.user.resources")

}
