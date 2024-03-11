package com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.exception.JobExecuteErrorException
import org.apache.linkis.common.conf.{CommonVars, Configuration, TimeType}

object JobConf {

  val STREAMIS_DEVELOPER: CommonVars[String] = CommonVars("wds.streamis.developer", "enjoyyin,davidhua")

  val STREAMIS_DEFAULT_TENANT: CommonVars[String] = CommonVars("wds.streamis.job.tenant.default", "")

  val STREAMIS_JOB_MONITOR_ENABLE: CommonVars[Boolean] = CommonVars("wds.streamis.job.monitor.enable", true)

  val STREAMIS_JOB_RESET_ON_START_ENABLE: CommonVars[Boolean] = CommonVars("wds.streamis.job.reset_on_restart.enable", true)

  val STREAMIS_JOB_PARAM_BLANK_PLACEHOLDER: CommonVars[String] = CommonVars("wds.streamis.job.param.blank.placeholder", "\u0001")

  /**
   * Gateway for stream job log module
   */
  val STREAMIS_JOB_LOG_GATEWAY: CommonVars[String] = CommonVars("wds.streamis.job.log.gateway", Configuration.getGateWayURL())

  /**
   * Path for collecting stream job log
   */
  val STREAMIS_JOB_LOG_COLLECT_PATH: CommonVars[String] = CommonVars("wds.streamis.job.log.collect.path", "/api/rest_j/v1/streamis/streamJobManager/log/collect/events")

  val STREAMIS_JOB_LOG_HEARTBEAT_PATH: CommonVars[String] = CommonVars("wds.streamis.log.heartbeat.path", "/api/rest_j/v1/streamis/streamJobManager/log/heartbeat")

  val STREAMIS_JOB_LOG_HEARTBEAT_INTERVAL: CommonVars[Int] = CommonVars("wds.streamis.log.heartbeat.interval.mills", 30 * 60 * 1000)

  val STREAMIS_JOB_LOG_HEARTBEAT_ENABLE: CommonVars[Boolean] = CommonVars("wds.streamis.log.heartbeat.enable", true)

  /**
   * Enable to use material container
   */
  val STREAMIS_JOB_MATERIAL_CONTAINER_ENABLE: CommonVars[Boolean] = CommonVars("wds.streamis.job.material.container.enable", false)

  val FLINK_JOB_STATUS_NOT_STARTED: CommonVars[Int] = CommonVars("wds.streamis.job.status.not-started", 0, "Not Started")

  val FLINK_JOB_STATUS_COMPLETED: CommonVars[Int] = CommonVars("wds.streamis.job.status.completed", 1, "Completed")

  val FLINK_JOB_STATUS_WAIT_RESTART: CommonVars[Int] = CommonVars("wds.streamis.job.status.wait-restart", 2, "Wait for restart")

  val FLINK_JOB_STATUS_ALERT_RUNNING: CommonVars[Int] = CommonVars("wds.streamis.job.status.alert-running", 3, "Alert running")

  val FLINK_JOB_STATUS_SLOW_RUNNING: CommonVars[Int] = CommonVars("wds.streamis.job.status.slow-running", 4, "Slow running")

  val FLINK_JOB_STATUS_RUNNING: CommonVars[Int] = CommonVars("wds.streamis.job.status.running", 5, "running")

  val FLINK_JOB_STATUS_FAILED: CommonVars[Int] = CommonVars("wds.streamis.job.status.failed", 6, "Failed")

  val FLINK_JOB_STATUS_STOPPED: CommonVars[Int] = CommonVars("wds.streamis.job.status.stopped", 7, "Stopped")

  /**
   * Starting (middle status, before scheduling)
   */
  val FLINK_JOB_STATUS_STARTING: CommonVars[Int] = CommonVars("wds.streamis.job.status.starting", 8, "Starting")

  /**
   * Stopping (middle status, before scheduling)
   */
  val FLINK_JOB_STATUS_STOPPING: CommonVars[Int] = CommonVars("wds.streamis.job.status.stopping", 9, "Stopping")

  val STATUS_ARRAY: Array[CommonVars[Int]] = Array(FLINK_JOB_STATUS_COMPLETED, FLINK_JOB_STATUS_WAIT_RESTART, FLINK_JOB_STATUS_ALERT_RUNNING,
    FLINK_JOB_STATUS_SLOW_RUNNING, FLINK_JOB_STATUS_RUNNING, FLINK_JOB_STATUS_FAILED, FLINK_JOB_STATUS_STOPPED, FLINK_JOB_STATUS_STARTING, FLINK_JOB_STATUS_STOPPING)

  val NOT_COMPLETED_STATUS_ARRAY: Array[CommonVars[Int]] = Array(FLINK_JOB_STATUS_WAIT_RESTART, FLINK_JOB_STATUS_ALERT_RUNNING, FLINK_JOB_STATUS_SLOW_RUNNING,
    FLINK_JOB_STATUS_RUNNING)

  def isCompleted(status: Int): Boolean = status match {
    case 1 | 6 | 7 => true
    case _ => false
  }

  def isRunning(status: Int): Boolean = status match {
    case 2 | 3 | 4 | 5 => true
    case _ => false
  }

  def isFinished(status: Int): Boolean = status match {
    case 0 | 1 | 6 | 7 => true
    case _ => false
  }

  def linkisStatusToStreamisStatus(status: String): Int = status.toLowerCase match {
    case "starting" | "unlock" | "locked" | "idle" | "busy" | "running" => FLINK_JOB_STATUS_RUNNING.getValue
    case "success" => FLINK_JOB_STATUS_COMPLETED.getValue
    case "failed" | "shuttingdown" => FLINK_JOB_STATUS_FAILED.getValue
  }

  def getStatusString(status: Int): String = STATUS_ARRAY.find(_.getValue == status).map(_.description)
    .getOrElse(throw new JobExecuteErrorException(30351, s"Unknown status $status."))

  val TASK_MONITOR_INTERVAL: CommonVars[TimeType] = CommonVars("wds.streamis.task.monitor.interval", new TimeType("1m"))

  val TASK_SUBMIT_TIME_MAX: CommonVars[TimeType] = CommonVars("wds.streamis.task.submit.time.max", new TimeType("5m"))

  val SUPPORTED_JOB_TYPES: CommonVars[String] = CommonVars("wds.streamis.supported.job.types", "flink.jar,flink.sql,spark.jar")

  val SUPPORTED_MANAGEMENT_JOB_TYPES: CommonVars[String] = CommonVars("wds.streamis.management.supported.job.types", "flink.jar,flink.sql")

  val ERROR_CODE_MATCHING_YARN_TIME: CommonVars[Int] = CommonVars("wds.streamis.error.code.matching.yarn.time", 2)

  val DEFAULT_ERROR_MSG: CommonVars[String] = CommonVars("wds.streamis.default.error.msg", "正在分析日志，请稍后")

  val PROJECT_NAME_STRICT_CHECK_SWITCH: CommonVars[Boolean] = CommonVars("wds.streamis.project.name.strict.check.enable", false)

  val HIGHAVAILABLE_ENABLE: CommonVars[Boolean] = CommonVars("wds.streamis.app.highavailable.enable", true)

  val HIGHAVAILABLE_SOURCE: CommonVars[String] = CommonVars("wds.streamis.app.highavailable.source", "aomp")

//  val HIGHAVAILABLE_POLICY: CommonVars[String] = CommonVars("wds.streamis.app.highavailable.policy.double", "double")

  val HIGHAVAILABLE_POLICY_DOUBLE: CommonVars[String] = CommonVars("wds.streamis.app.highavailable.policy.double", "double")

  val HIGHAVAILABLE_POLICY_DOUBLE_BAK: CommonVars[String] = CommonVars("wds.streamis.app.highavailable.policy.doubleWithBak", "doubleWithBak")

  val HIGHAVAILABLE_POLICY_SINGLE_BAK: CommonVars[String] = CommonVars("wds.streamis.app.highavailable.policy.singleWithBak", "singleWithBak")

  val HIGHAVAILABLE_POLICY_MANAGERSLAVE: CommonVars[String] = CommonVars("wds.streamis.app.highavailable.policy.managerSlave", "managerSlave")

  val HIGHAVAILABLE_POLICY_MANAGERSLAVE_BAK: CommonVars[String] = CommonVars("wds.streamis.app.highavailable.policy.managerSlaveWithBak", "managerSlaveWithBak")

  val HIGHAVAILABLE_DEFAULT_POLICY: CommonVars[String] = CommonVars("wds.streamis.app.highavailable.default.policy", "single")

  val JOB_SCHEMA_SINGLE: CommonVars[String] = CommonVars("wds.streamis.app.job.schema.single", "single")

  val JOB_SCHEMA_DOUBLE: CommonVars[String] = CommonVars("wds.streamis.app.job.schema.double", "double")

  val JOB_SCHEMA_MANAGER_SLAVE: CommonVars[String] = CommonVars("wds.streamis.app.job.schema.managerSlave", "managerSlave")

  val HIGHAVAILABLE_POLICY_KEY: CommonVars[String] = CommonVars("wds.streamis.app.highavailable.policy.key", "wds.streamis.app.highavailable.policy")

  val STREAMIS_CHECK_FILE_FORMAT: CommonVars[String] = CommonVars("wds.streamis.check.file.format", "yaml|text|jar|properties|txt|pem")

  val STREAMIS_OLD_SNAPSHOT_PATH_ENABLE: CommonVars[Boolean] = CommonVars("wds.streamis.old snapshot path.enable", true)

  val DEFAULT_ARGS_LENGTH: CommonVars[Int] = CommonVars("wds.streamis.default.args.length", 2000)

  val AUTO_RESTART_JOB: CommonVars[Boolean] = CommonVars("wds.streamis.app.highavailable.auto.restart.job", true)

  val LOGS_HEARTBEAT_CHECK_ENABLE: CommonVars[Boolean] = CommonVars("wds.streamis.logs.heartbeat.enable", true)

  val LOGS_HEARTBEAT_ALARMS_ENABLE: CommonVars[Boolean] = CommonVars("wds.streamis.logs.heartbeat.alarms.enable", true)

  val LOGS_HEARTBEAT_REGISTER_ALARMS_ENABLE: CommonVars[Boolean] = CommonVars("wds.streamis.logs.heartbeat.register.alarms.enable", true)

  val LOGS_HEARTBEAT_CHECK_INTERVAL: CommonVars[Int] = CommonVars("wds.streamis.logs.check.heartbeat.interval.mills", 10 *60 * 1000)

  val LOGS_HEARTBEAT_INTERVAL_TIMEOUT: CommonVars[Int] = CommonVars("wds.streamis.logs.check.heartbeat.interval.timeout.mills", 30 * 60 * 1000)

  val JOB_NAME_LENGTH_MAX: CommonVars[Int] = CommonVars("wds.streamis.job.name.length.max", 2000)

  val PRODUCT_NAME_KEY: CommonVars[String] = CommonVars("wds.linkis.flink.product.key", "wds.linkis.flink.product")

  val PRODUCT_NAME_SWITCH: CommonVars[Boolean] = CommonVars("wds.linkis.flink.product.key.enable", true)
}
