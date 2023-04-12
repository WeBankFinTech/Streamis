package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client

import com.webank.wedatasphere.streamis.jobmanager.launcher.enums.FlinkManagerActionType
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.constants.JobConstants
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.{JobGenericState, JobStateInfo}
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.{FlinkManagerAction, FlinkManagerClient, JobClient, JobInfo}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.conf.JobLauncherConfiguration
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.{FlinkJobFlinkECErrorException, FlinkJobLaunchErrorException, FlinkJobParamErrorException}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.jobInfo.EngineConnJobInfo
import org.apache.commons.lang3.SerializationUtils
import org.apache.linkis.common.ServiceInstance
import org.apache.linkis.common.utils.{Logging, Utils}
import org.apache.linkis.computation.client.once.OnceJob
import org.apache.linkis.computation.client.once.action.{AskEngineConnAction, CreateEngineConnAction, ECMOperateAction, ECResourceInfoAction, EngineConnOperateAction}
import org.apache.linkis.computation.client.once.result.{ECResourceInfoResult, EngineConnOperateResult}
import org.apache.linkis.computation.client.once.simple.SimpleOnceJobBuilder.getLinkisManagerClient
import org.apache.linkis.computation.client.once.simple.{SimpleOnceJobBuilder, SubmittableSimpleOnceJob}
import org.apache.linkis.governance.common.constant.ec.ECConstants
import org.apache.linkis.governance.common.enums.OnceJobOperationBoundary
import org.apache.linkis.httpclient.dws.DWSHttpClient
import org.apache.linkis.manager.common.constant.AMConstant
import org.apache.linkis.manager.label.conf.LabelCommonConfig
import org.apache.linkis.manager.label.entity.engine.{CodeLanguageLabel, EngineType, EngineTypeLabel, RunType, UserCreatorLabel}

import java.util
import scala.collection.JavaConverters.mapAsScalaMapConverter

class LinkisFlinkManagerClient extends FlinkManagerClient with Logging {

  private var ecInstance: ServiceInstance = _
  private var ecmInstance: ServiceInstance = _

  def init(): Unit =
    getOrCreateLinkisManagerECAndECM()

  private def getOrCreateLinkisManagerECAndECM(): (ServiceInstance, ServiceInstance)  = {
    val initLabels = new util.HashMap[String, String]
    // TODO
    val engineTypeLabel = new EngineTypeLabel
    engineTypeLabel.setEngineType(EngineType.FLINK.toString)
    engineTypeLabel.setVersion(LabelCommonConfig.FLINK_ENGINE_VERSION.getValue)
    val codeTypeLabel = new CodeLanguageLabel
    codeTypeLabel.setCodeType(RunType.JSON.toString)
    val userCreatorLabel = new UserCreatorLabel
    userCreatorLabel.setUser(JobLauncherConfiguration.FLINK_MANAGER_EC_SUBMIT_USER.getValue)
    userCreatorLabel.setCreator(JobLauncherConfiguration.FLINK_MANAGER_EC_SUBMIT_CREATOR.getValue)
    initLabels.put(engineTypeLabel.getLabelKey, engineTypeLabel.getStringValue)
    initLabels.put(codeTypeLabel.getLabelKey, codeTypeLabel.getStringValue)
    val initProperties = new util.HashMap[String, String]()
    // TODO

    var askEngineConnAction = AskEngineConnAction
      .newBuilder()
      .setCreateService(getClass.getSimpleName)
      .setLabels(initLabels)
      .setIgnoreTimeout(false)
      .setProperties(initProperties)
      .setUser(JobLauncherConfiguration.FLINK_MANAGER_EC_SUBMIT_USER.getValue)
      .setMaxSubmitTime(JobLauncherConfiguration.FLINK_ONCE_JOB_MAX_SUBMIT_TIME_MILLS.getValue)
      .setDescription("Ask a flink manager ec.")
      .build()
    val linkisManagerClient = getLinkisManagerClient
    var end = false
    var ecServiceInstance: ServiceInstance = null
    logger.info(s"ask flink manager ec askEngineConnAction: ${askEngineConnAction.getRequestPayload}")
    var nodeInfo = linkisManagerClient.askEngineConn(askEngineConnAction).getNodeInfo
    val tmpLabels = SerializationUtils.clone(initLabels).asInstanceOf[util.Map[String, String]]
    val tmpProps = SerializationUtils.clone(initProperties).asInstanceOf[util.Map[String, String]]
    var lastAsyncId: String = null
    var lastManagerInstance: ServiceInstance = null

    while (!end) {
      nodeInfo.get(AMConstant.EC_ASYNC_START_RESULT_KEY) match {
        case AMConstant.EC_ASYNC_START_RESULT_SUCCESS =>
          end = true
          ecServiceInstance = getServiceInstance(nodeInfo)
          ecmInstance = getECMInstance(nodeInfo)
        case AMConstant.EC_ASYNC_START_RESULT_STARTING =>
          end = false
          val asyncId = nodeInfo.get(AMConstant.EC_ASYNC_START_ID_KEY).toString
          val managerInstance = nodeInfo.get(AMConstant.EC_ASYNC_START_MANAGER_INSTANCE_KEY).asInstanceOf[ServiceInstance]
          tmpProps.put(AMConstant.EC_ASYNC_START_ID_KEY, asyncId)
          if (null == lastAsyncId) {
            lastAsyncId = asyncId
          }
          if (null == lastManagerInstance) {
            lastManagerInstance = managerInstance
          }
          if (!lastAsyncId.equals(asyncId)) {
            logger.error(s"lastAsyncId from : ${lastAsyncId} changed to ${asyncId}.")
          }
          if (!lastManagerInstance.equals(managerInstance)) {
            logger.error("Manager instance changed! Please use fixed manager.")
          }
          // TODO linkisclient需要支持指定发送请求到serviceinstance上。目前只能manager加标签或者只起一个manger
        case AMConstant.EC_ASYNC_START_RESULT_FAIL =>
          end = true
          val failMsg = nodeInfo.getOrDefault(AMConstant.EC_ASYNC_START_FAIL_MSG_KEY, "no reason")
          logger.error(s"start flink manager ec failed because: ${failMsg}")
          logger.warn(s"askEngineConnAction: ${askEngineConnAction.getRequestPayload}")
          if (nodeInfo.get(AMConstant.EC_ASYNC_START_FAIL_RETRY_KEY).toString.toBoolean) {
            if (tmpProps.containsKey(AMConstant.EC_ASYNC_START_ID_KEY)) {
              tmpProps.remove(AMConstant.EC_ASYNC_START_ID_KEY)
            }
          } else {
            throw new FlinkJobFlinkECErrorException(s"Start manager ec failed. Because : ${failMsg}")
          }
        case null =>
          end = true
          logger.error(s"start flink manager ec failed because: null ec result status")
          logger.warn(s"askEngineConnAction: ${askEngineConnAction.getRequestPayload}")
          throw new FlinkJobFlinkECErrorException(s"Start manager ec failed. Null ec result status")
      }
      Thread.sleep(1000)
      askEngineConnAction = AskEngineConnAction
        .newBuilder()
        .setCreateService(getClass.getSimpleName)
        .setLabels(tmpLabels)
        .setIgnoreTimeout(false)
        .setProperties(tmpProps)
        .setUser(JobLauncherConfiguration.FLINK_MANAGER_EC_SUBMIT_USER.getValue)
        .setMaxSubmitTime(JobLauncherConfiguration.FLINK_ONCE_JOB_MAX_SUBMIT_TIME_MILLS.getValue)
        .setDescription("Ask a flink manager ec.")
        .build()
      nodeInfo = linkisManagerClient.askEngineConn(askEngineConnAction).getNodeInfo
    }
//    val onceJob = new SubmittableSimpleOnceJob(getLinkisManagerClient, askEngineConnAction)
//    onceJob.submit()
//    onceJob
    (ecServiceInstance, ecmInstance)
  }

  override def setFlinkManagerEngineConnInstance(ecInstance: ServiceInstance): Unit = this.ecInstance = ecInstance

  override def getFlinkManagerEngineConnInstance(): ServiceInstance = {
    if (null == ecInstance) {
      LinkisFlinkManagerClient.ASK_EC_LOCK.synchronized {
        if (null == ecInstance) {
          val rs = getOrCreateLinkisManagerECAndECM()
          ecInstance = rs._1
          ecmInstance = rs._2
        }
      }
    }
    ecInstance
  }

  override def setFlinkManagerECMInstance(ecmInstance: ServiceInstance): Unit = this.ecmInstance = ecmInstance

  override def getFlinkManagerECMInstance(): ServiceInstance = this.ecmInstance

  override def executeAction(action: FlinkManagerAction): Any = {
    action.getActionType match {
      case validType if FlinkManagerActionType.values().contains(validType) =>
        action.getParams().put(ECConstants.EC_SERVICE_INSTANCE_KEY, getFlinkManagerEngineConnInstance())
        action.getParams().put(ECConstants.ECM_SERVICE_INSTANCE_KEY, ecmInstance)
        val builtAction = action.build()
        action.getOperationBoundry match {
          case OnceJobOperationBoundary.COMMON =>
            if (builtAction.isInstanceOf[ECMOperateAction]) {
              return doExecution(builtAction.asInstanceOf[ECMOperateAction])
            } else {
              throw new FlinkJobParamErrorException("FlinkManagerAction build invalid ECMOperation with operationBounday : common")
            }
          case OnceJobOperationBoundary.PRIVATE =>
            if (builtAction.isInstanceOf[EngineConnOperateAction]) {
              return doExecution(builtAction.asInstanceOf[EngineConnOperateAction])
            } else {
              throw new FlinkJobParamErrorException("FlinkManagerAction build invalid EngineConnOperation with operationBounday : private")
            }
        }
      case _ =>
        throw new FlinkJobParamErrorException(s"Unsupported FlinkManagerAction : ${action}")
    }
  }

  def doExecution(operationAction: EngineConnOperateAction): EngineConnOperateResult = {
    SimpleOnceJobBuilder.getLinkisManagerClient.executeEngineConnOperation(operationAction).asInstanceOf[EngineConnOperateResult]
  }

  private def getServiceInstance(nodeInfo: util.Map[String, Any]): ServiceInstance =
    nodeInfo.get(ECConstants.EC_SERVICE_INSTANCE_KEY) match {
      case serviceInstance: util.Map[String, Any] =>
        ServiceInstance(
          getAs(serviceInstance, "applicationName"),
          getAs(serviceInstance, "instance")
        )
      case _ =>
        null
    }

  private def getECMInstance(nodeInfo: util.Map[String, Any]): ServiceInstance =
    nodeInfo.get(ECConstants.ECM_SERVICE_INSTANCE_KEY) match {
      case serviceInstance: util.Map[String, Any] =>
        ServiceInstance(
          getAs(serviceInstance, "applicationName"),
          getAs(serviceInstance, "instance")
        )
      case _ =>
        null
    }

  private def getAs[T](map: util.Map[String, Any], key: String): T =
    map.get(key).asInstanceOf[T]

  def getTicketId(nodeInfo: util.Map[String, Any]): String = getAs(nodeInfo, "ticketId")
}

object LinkisFlinkManagerClient {

  private val CLIENT_LOCK = new Object()

  private val ASK_EC_LOCK = new Object()

  private var client: LinkisFlinkManagerClient = _

  def getInstance(): LinkisFlinkManagerClient = {
    if (null == client) {
      CLIENT_LOCK.synchronized {
        if (null == client) {
          client = new LinkisFlinkManagerClient
          client.init()
        }
      }
    }
    client
  }

}