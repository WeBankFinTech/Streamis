package com.webank.wedatasphere.streamis.jobmanager.launcher.conf

import com.webank.wedatasphere.linkis.common.conf.{CommonVars, TimeType}

/**
 * @author limeng
 */
object ConfigConf {

  val JOBMANAGER_FLINK_RESOURCE = CommonVars("wds.linkis.flink.resource", 1)

  val JOBMANAGER_FLINK_CUSTOM = CommonVars("wds.linkis.flink.custom", 2)

  val JOBMANAGER_FLINK_PRODUCE = CommonVars("wds.linkis.flink.produce", 3)

  val JOBMANAGER_FLINK_ALERT = CommonVars("wds.linkis.flink.alert", 4)

  val JOBMANAGER_FLINK_AUTHORITY = CommonVars("wds.linkis.flink.authority", 5)

  val JOBMANAGER_FLINK_CUSTOM_STATUS_ONE = CommonVars("wds.linkis.flink.custom.one", 1)

  val JOBMANAGER_FLINK_CUSTOM_STATUS_TWO = CommonVars("wds.linkis.flink.custom.two", 2)

  val JOBMANAGER_FLINK_AUTHORITY_VISIBLE = CommonVars("wds.linkis.flink.authority.visible", "wds.linkis.flink.authority.visible")

  val JOBMANAGER_FLINK_AUTHORITY_AUTHOR = CommonVars("wds.linkis.flink.authority.author", "wds.linkis.flink.authority.author")

  val JOBMANAGER_FLINK_ALERT_RULE = CommonVars("wds.linkis.flink.alert.rule", "wds.linkis.flink.alert.rule")

}
