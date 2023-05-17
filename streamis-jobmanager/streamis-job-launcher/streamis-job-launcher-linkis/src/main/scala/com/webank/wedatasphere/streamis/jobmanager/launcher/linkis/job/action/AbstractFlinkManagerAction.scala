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

    val operateNameKey = EngineConnOperateAction.OPERATOR_NAME_KEY

    // inner params to ec
    val params = getParams()
    params.put(JobConstants.MSG_KEY, getMsg)
    params.put(operateNameKey, getActionType.getName)
    params.put(JobConstants.FLINK_OPERATION_BOUNDARY_KEY, getOperationBoundry.toString)
    params.put(JobConstants.APPLICATION_ID_KEY, getApplicationId)
    // outer params to manager
    operateAction.addRequestPayload("parameters", params)
    getPlayloads().asScala.foreach{kv => operateAction.addRequestPayload(kv._1, kv._2)}
    operateAction.setUser(getExecuteUser)
    operateAction
  }
}


class FlinkStatusAction(applicationId: String, msg: String) extends AbstractFlinkManagerAction {
  override def getApplicationId: String = applicationId

  override def getMsg: String = msg

  override def getActionType: FlinkManagerActionType = FlinkManagerActionType.STATUS

  override def getOperationBoundry: OnceJobOperationBoundary = FlinkManagerActionType.getOperationBoundary(getActionType)
}

class FlinkKillAction(applicationId: String, msg: String) extends AbstractFlinkManagerAction {
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

  def setSavepointPath(path: String): Unit = getParams().put(JobConstants.SAVAPOINT_PATH_KEY, path)

  def setMode(mode: String): Unit = getParams().put(JobConstants.MODE_KEY, mode)
}

