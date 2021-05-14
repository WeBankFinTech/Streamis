package com.webank.wedatasphere.streamis.jobmanager.manager.conf

import com.webank.wedatasphere.linkis.common.conf.CommonVars

/**
 * @author limeng
 */
object JobConf {
  val JOBMANAGER_FLINK_JOB_STATUS_ONE = CommonVars("wds.linkis.flink.job.status.one", 1,"complete")

  val JOBMANAGER_FLINK_JOB_STATUS_TWO = CommonVars("wds.linkis.flink.job.status.two", 2,"Wait for restart")

  val JOBMANAGER_FLINK_JOB_STATUS_THREE = CommonVars("wds.linkis.flink.job.status.three", 3,"Alert")

  val JOBMANAGER_FLINK_JOB_STATUS_FOUR = CommonVars("wds.linkis.flink.job.status.four", 4,"Slow task")

  val JOBMANAGER_FLINK_JOB_STATUS_FIVE = CommonVars("wds.linkis.flink.job.status.five", 5,"run")

  val JOBMANAGER_FLINK_JOB_STATUS_SIX = CommonVars("wds.linkis.flink.job.status.six", 6,"Failed task")

  val JOBMANAGER_FLINK_JOB_STATUS_SEVEN = CommonVars("wds.linkis.flink.job.status.seven", 7,"Stop task")

  val JOBMANAGER_FLINK_SQL = CommonVars("wds.linkis.flink.job.sql", 1,"flink type sql")
}
