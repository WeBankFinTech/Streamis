package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.action

import com.webank.wedatasphere.streamis.jobmanager.launcher.enums.FlinkManagerActionType
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.FlinkManagerAction
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.constants.JobConstants
import org.apache.linkis.computation.client.once.action.{ECMOperateAction, EngineConnOperateAction}
import org.apache.linkis.governance.common.constant.ec.ECConstants
import org.apache.linkis.governance.common.enums.OnceJobOperationBoundary
import java.util

import scala.collection.JavaConverters.mapAsScalaMapConverter

abstract class AbstractFlinkManagerAction extends FlinkManagerAction {

  override def build(): EngineConnOperateAction = {
    val operateAction: EngineConnOperateAction = getOperationBoundry match {
      case OnceJobOperationBoundary.ECM =>
        new ECMOperateAction()
      case OnceJobOperationBoundary.EC =>
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

  override def getOperationBoundry: OnceJobOperationBoundary = FlinkManagerActionType.getOperationBoundary(getActionType)
}


class FlinkStatusAction(applicationId: String, msg: String) extends AbstractFlinkManagerAction {
  override def getApplicationId: String = applicationId

  override def getMsg: String = msg

  override def getActionType: FlinkManagerActionType = FlinkManagerActionType.STATUS

}

class FlinkKillAction(applicationId: String, msg: String) extends AbstractFlinkManagerAction {
  override def getApplicationId: String = applicationId

  override def getMsg: String = msg

  override def getActionType: FlinkManagerActionType = FlinkManagerActionType.KILL
}

class FlinkSaveAction(applicationId: String, msg: String) extends AbstractFlinkManagerAction {
  override def getApplicationId: String = applicationId

  override def getMsg: String = msg

  override def getActionType: FlinkManagerActionType = FlinkManagerActionType.SAVE

  def setSavepointPath(path: String): Unit = getParams().put(JobConstants.SAVAPOINT_PATH_KEY, path)

  def setMode(mode: String): Unit = getParams().put(JobConstants.MODE_KEY, mode)
}

class ListYarnAppAction(appName: String, user: String, msg: String) extends AbstractFlinkManagerAction {

  override def getApplicationId: String = null

  override def getMsg: String = msg

  override def getActionType: FlinkManagerActionType = FlinkManagerActionType.LIST

  def addAppType(appType: String): Unit = {
    val appTypeList = getParams().getOrDefault(ECConstants.YARN_APP_TYPE_LIST_KEY, new util.ArrayList[String]).asInstanceOf[util.List[String]]
    getParams().putIfAbsent(ECConstants.YARN_APP_STATE_LIST_KEY, appTypeList)
    if (!appTypeList.contains(appType)) {
      appTypeList.add(appType)
    }
  }

  def setAppName(): Unit = getParams().put(ECConstants.YARN_APP_NAME_KEY, appName)

  def setMsg(): Unit = getParams().put(JobConstants.MSG_KEY, msg)

  setAppName()

  setMsg()
}
