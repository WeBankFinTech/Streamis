package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.conf

import org.apache.linkis.common.conf.CommonVars
import org.apache.linkis.governance.common.conf.GovernanceCommonConf

/**
 * Job Launcher configuration
 */
object JobLauncherConfiguration {


  val FLINK_FETCH_APPLICATION_INFO_MAX_TIMES: CommonVars[Int] = CommonVars("wds.streamis.application.info.fetch.max", 6)

  val FLINK_STATE_DEFAULT_SCHEME: CommonVars[String] = CommonVars("wds.streamis.launch.flink.state.default.scheme", "hdfs")
  /**
   * Support schema protocols to store flink job states
   */
  val FLINK_STATE_SUPPORT_SCHEMES: CommonVars[String] = CommonVars("wds.streamis.launch.flink.state.support.schemas", "hdfs,file,viewfs,s3")

  /**
   * Authority(host) value to store flink job states
   */
  val FLINK_STATE_DEFAULT_AUTHORITY: CommonVars[String] = CommonVars("wds.streamis.launch.flink.state.authority", "")
  /**
   * Savepoint mode
   */
  val FLINK_TRIGGER_SAVEPOINT_MODE: CommonVars[String] = CommonVars("wds.streamis.launch.flink.savepoint.mode", "trigger")

  /**
   * Savepoint dir
   */
  val FLINK_SAVEPOINT_PATH: CommonVars[String] = CommonVars("wds.streamis.launch.flink.savepoint.dir", "/flink/flink-savepoints")

  /**
   * Checkpoint dir
   */
  val FLINK_CHECKPOINT_PATH: CommonVars[String] = CommonVars("wds.streamis.launch.flink.checkpoint.dir", "/flink/flink-checkpoints")

  /**
   * Linkis release version
   */
  val FLINK_LINKIS_RELEASE_VERSION: CommonVars[String] = CommonVars("wds.streamis.launch.flink.linkis.release.version", "")
  /**
   * Variable: savepoint path
   */
  val VAR_FLINK_SAVEPOINT_PATH: CommonVars[String] = CommonVars("wds.streamis.launch.variable.flink.savepoint.path", "flink.app.savePointPath")

  /**
   * Variable: flink app
   */
  val VAR_FLINK_APP_NAME: CommonVars[String] = CommonVars("wds.streamis.launch.variable.flink.app.name", "flink.app.name")

  val FLINK_MANAGER_EC_SUBMIT_USER: CommonVars[String] = CommonVars("wds.streamis.launch.manager.ec.submit.user", "hadoop")

  val FLINK_MANAGER_EC_SUBMIT_CREATOR : CommonVars[String] = CommonVars("wds.streamis.launch.manager.ec.submit.creator", "Streamis")

  val FLINK_MANAGER_EC_TENANT: CommonVars[String] = CommonVars("wds.streamis.launch.manager.ec.tenant", null)

  val FLINK_ONCE_JOB_MAX_SUBMIT_TIME_MILLS: CommonVars[Long] = CommonVars("wds.streamis.launch.oncejob.max_submit_time.mills", 300000)

  val FLINK_MANAGER_EC_REFRESH_INTERVAL: CommonVars[Long] = CommonVars("wds.streamis.launch.manager.ec.refresh.interval.mills", 10 * 60 * 1000)

  val MANAGER_MODE_KEY: CommonVars[String] = CommonVars("wds.streamis.job.manager.mode.key", GovernanceCommonConf.EC_APP_MANAGE_MODE.key)

  val FLINK_MANAGER_EC_KEY: CommonVars[String] = CommonVars("linkis.flink.manager.ec.key", "linkis.flink.manager.mode.on")

  val ENABLE_FLINK_MANAGER_EC_ENABLE: CommonVars[Boolean] = CommonVars("wds.streamis.flink.manager.ec.enable", true)

  val FLINKK_MANAGER_EXIT_TIME: CommonVars[Long] = CommonVars("wds.streamis.flink.manager.ec.expire.time.mills", 3600 * 1000)

  val LINKIS_EC_EXPIRE_TIME_KEY: CommonVars[String] = CommonVars("linkis.ec.expire.key", "wds.linkis.engineconn.max.free.time")

  val ENABLE_FLINK_LIST_INSPECT: CommonVars[Boolean] = CommonVars("wds.streamis.job.inspact.list.enable", true)
}
