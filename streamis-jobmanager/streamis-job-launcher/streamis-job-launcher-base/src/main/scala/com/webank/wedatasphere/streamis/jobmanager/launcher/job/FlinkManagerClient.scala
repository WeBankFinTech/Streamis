package com.webank.wedatasphere.streamis.jobmanager.launcher.job

import com.webank.wedatasphere.streamis.jobmanager.launcher.enums.FlinkManagerActionType
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.constants.JobConstants
import org.apache.linkis.common.ServiceInstance
import org.apache.linkis.governance.common.enums.OnceJobOperationBoundary

import java.util

trait FlinkManagerClient {

  def setFlinkManagerEngineConnInstance(ecInstance: ServiceInstance): Unit

  def getFlinkManagerEngineConnInstance(): ServiceInstance

  def setFlinkManagerECMInstance(ecmInstance: ServiceInstance): Unit

  def getFlinkManagerECMInstance(): ServiceInstance

  def executeAction(action: FlinkManagerAction): Any
}

trait FlinkManagerAction {

  private var params: util.Map[String, AnyRef] = new util.HashMap[String, AnyRef]()
  private var executeUser: String = _

  def getApplicationId: String

  def getMsg: String

  def getActionType: FlinkManagerActionType

  def getOperationBoundry: OnceJobOperationBoundary

  def getParams(): util.Map[String, AnyRef] = params

  def setParams(params: util.Map[String, AnyRef]): FlinkManagerAction = {
    this.params = params
    this
  }

  def getExecuteUser: String = executeUser

  def setExeuteUser(user: String): FlinkManagerAction = {
    this.executeUser = user
    this
  }

  def setECInstance(ecInstance: ServiceInstance): FlinkManagerAction = {
    params.put(JobConstants.EC_INSTANCE_KEY, ecInstance)
    this
  }

  def build(): Any /* ={
    val params = getParams()
    params.put(JobConstants.APPLICATION_ID_KEY, getApplicationId)
    params.put(JobConstants.MSG_KEY, getMsg)
    params.put(JobConstants.FLINK_MANAGER_OPERATION_TYPE_KEY, getActionType.toString)
    params
  }*/
}

