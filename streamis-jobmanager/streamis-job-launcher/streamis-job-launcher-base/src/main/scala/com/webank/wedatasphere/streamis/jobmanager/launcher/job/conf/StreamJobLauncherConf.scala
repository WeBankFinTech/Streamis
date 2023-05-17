package com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf

import com.webank.wedatasphere.streamis.jobmanager.launcher.enums.FlinkManagerActionType
import org.apache.linkis.common.conf.CommonVars

object StreamJobLauncherConf {

  val FLINK_MANAGER_ACTION_BOUNDARY_PRIVATE_ACTIONS = CommonVars("wds.streamis.flink.manager.action.boundary.private.actions", FlinkManagerActionType.values().map(_.toString).mkString(","))

  def isPrivateAction(actionType: FlinkManagerActionType): Boolean = {
    FLINK_MANAGER_ACTION_BOUNDARY_PRIVATE_ACTIONS.getValue.split(",").contains(actionType.toString)
  }

}
