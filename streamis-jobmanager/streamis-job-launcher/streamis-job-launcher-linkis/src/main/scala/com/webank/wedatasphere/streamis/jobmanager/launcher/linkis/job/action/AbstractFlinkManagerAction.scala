package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.action

import com.webank.wedatasphere.streamis.jobmanager.launcher.enums.FlinkManagerActionType
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.FlinkManagerAction
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.constants.JobConstants
import org.apache.linkis.computation.client.once.action.{ECMOperateAction, EngineConnOperateAction}
import org.apache.linkis.governance.common.enums.OnceJobOperationBoundary

import scala.collection.JavaConverters.mapAsScalaMapConverter

abstract class AbstractFlinkManagerAction extends FlinkManagerAction {

  override def build(): EngineConnOperateAction = {
    val operateAction: EngineConnOperateAction = getOperationBoundry match {
      case OnceJobOperationBoundary.COMMON =>
        new ECMOperateAction()
      case OnceJobOperationBoundary.PRIVATE =>
        new EngineConnOperateAction()
    }

    val params = getParams()
    params.asScala.foreach(kv => operateAction.setFormParam(kv._1, kv._2))
    operateAction.setFormParam(JobConstants.APPLICATION_ID_KEY, getApplicationId)
    operateAction.setFormParam(JobConstants.MSG_KEY, getMsg)
    operateAction.setFormParam(JobConstants.FLINK_MANAGER_OPERATION_TYPE_KEY, getActionType.toString)
    operateAction.setFormParam(JobConstants.FLINK_OPERATION_BOUNDARY_KEY, getOperationBoundry.toString)
    operateAction.setUser(getExecuteUser)
    operateAction.setFormParam(JobConstants.MSG_KEY, getMsg)
    operateAction
  }
}


class FlinkStatusAction(applicationId: String, msg: String) extends AbstractFlinkManagerAction {
  override def getApplicationId: String = applicationId

  override def getMsg: String = msg

  override def getActionType: FlinkManagerActionType = FlinkManagerActionType.STATUS

  override def getOperationBoundry: OnceJobOperationBoundary = FlinkManagerActionType.getOperationBoundary(getActionType)
}

class FlinkKillAction(applicationId: String, msg: String, snapshot: Boolean = false) extends AbstractFlinkManagerAction {
  override def getApplicationId: String = applicationId

  override def getMsg: String = msg

  override def getActionType: FlinkManagerActionType = FlinkManagerActionType.KILL

  override def getOperationBoundry: OnceJobOperationBoundary = FlinkManagerActionType.getOperationBoundary(getActionType)
}

class FlinkSaveAction(applicationId: String, msg: String) extends AbstractFlinkManagerAction {
  override def getApplicationId: String = applicationId

  override def getMsg: String = msg

  override def getActionType: FlinkManagerActionType = FlinkManagerActionType.SAVE

  override def getOperationBoundry: OnceJobOperationBoundary = FlinkManagerActionType.getOperationBoundary(getActionType)
}

