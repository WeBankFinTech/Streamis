package com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf

import com.webank.wedatasphere.streamis.jobmanager.launcher.enums.FlinkManagerActionType
import org.apache.linkis.common.conf.CommonVars

object StreamJobLauncherConf {

  val FLINK_MANAGER_ACTION_BOUNDARY_PRIVATE_ACTIONS = CommonVars("wds.streamis.flink.manager.action.boundary.private.actions", FlinkManagerActionType.values().map(_.toString).mkString(","))

  def isPrivateAction(actionType: FlinkManagerActionType): Boolean = {
    FLINK_MANAGER_ACTION_BOUNDARY_PRIVATE_ACTIONS.getValue.split(",").contains(actionType.toString)
  }

  val HIGHAVAILABLE_CLUSTER_NAME: CommonVars[String] = CommonVars("wds.streamis.highavailable.cluster.name", "BDAP_UAT")

  val WHETHER_MANAGER_CLUSTER: CommonVars[Boolean] = CommonVars("wds.streamis.whether.manager.cluster", false)

  val HIGHAVAILABLE_CLUSTER_IP: CommonVars[String] = CommonVars("wds.linkis.gateway.ip", "127.0.0.1")
}
