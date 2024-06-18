package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client

import com.webank.wedatasphere.streamis.jobmanager.launcher.enums.FlinkManagerActionType
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.constants.JobConstants
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.{FlinkManagerAction, FlinkManagerClient}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.conf.JobLauncherConfiguration
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.{FlinkJobFlinkECErrorException, FlinkJobParamErrorException}
import org.apache.commons.lang3.{SerializationUtils, StringUtils}
import org.apache.linkis.common.ServiceInstance
import org.apache.linkis.common.utils.{JsonUtils, Logging, Utils}
import org.apache.linkis.computation.client.LinkisJobBuilder
import org.apache.linkis.computation.client.once.LinkisManagerClient
import org.apache.linkis.computation.client.once.action.{AskEngineConnAction, ECMOperateAction, EngineConnOperateAction, GetEngineConnAction}
import org.apache.linkis.computation.client.once.result.EngineConnOperateResult
import org.apache.linkis.computation.client.once.simple.SimpleOnceJobBuilder
import org.apache.linkis.computation.client.once.simple.SimpleOnceJobBuilder.getLinkisManagerClient
import org.apache.linkis.governance.common.constant.ec.ECConstants
import org.apache.linkis.governance.common.enums.OnceJobOperationBoundary
import org.apache.linkis.manager.common.constant.AMConstant
import org.apache.linkis.manager.common.entity.enumeration.NodeStatus
import org.apache.linkis.manager.label.conf.LabelCommonConfig
import org.apache.linkis.manager.label.constant.LabelKeyConstant
import org.apache.linkis.manager.label.entity.TenantLabel
import org.apache.linkis.manager.label.entity.engine._

import java.util
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class LinkisFlinkManagerClient extends FlinkManagerClient with Logging {

  private var ecInstance: ServiceInstance = _
  private var ecmInstance: ServiceInstance = _
  private val healthy = new AtomicBoolean(true)

  def init(): Unit = {
    refreshManagerEC()
  }

  private def getOrCreateLinkisManagerECAndECM(): (ServiceInstance, ServiceInstance)  = {
    val initLabels = new util.HashMap[String, String]
    val submitUser = JobLauncherConfiguration.FLINK_MANAGER_EC_SUBMIT_USER.getValue
    val submitCreator = JobLauncherConfiguration.FLINK_MANAGER_EC_SUBMIT_CREATOR.getValue
    val engineTypeLabel = new EngineTypeLabel
    engineTypeLabel.setEngineType(EngineType.FLINK.toString)
    engineTypeLabel.setVersion(LabelCommonConfig.FLINK_ENGINE_VERSION.getValue)
    val codeTypeLabel = new CodeLanguageLabel
    codeTypeLabel.setCodeType(RunType.JSON.toString)
    val userCreatorLabel = new UserCreatorLabel
    userCreatorLabel.setUser(submitUser)
    userCreatorLabel.setCreator(submitCreator)
    if (StringUtils.isNotBlank(JobLauncherConfiguration.FLINK_MANAGER_EC_TENANT.getValue)) {
      val tenantLabel = new TenantLabel
      tenantLabel.setTenant(JobLauncherConfiguration.FLINK_MANAGER_EC_TENANT.getValue)
      initLabels.put(tenantLabel.getLabelKey, tenantLabel.getStringValue)
    }
    val managerLabel = new ManagerLabel
    managerLabel.setManager(EngineType.FLINK.toString)
    initLabels.put(engineTypeLabel.getLabelKey, engineTypeLabel.getStringValue)
    initLabels.put(codeTypeLabel.getLabelKey, codeTypeLabel.getStringValue)
    initLabels.put(userCreatorLabel.getLabelKey, userCreatorLabel.getStringValue)
    initLabels.put(managerLabel.getLabelKey, managerLabel.getStringValue)
    if (StringUtils.isNotBlank(JobLauncherConfiguration.FLINK_MANAGER_EC_TENANT.getValue)) {
      initLabels.put(LabelKeyConstant.TENANT_KEY, JobLauncherConfiguration.FLINK_MANAGER_EC_TENANT.getValue)
    }
    val initProperties = new util.HashMap[String, String]()

    initProperties.put("flink.app.savePointPath", "./tmp")
    initProperties.put("flink.app.name", "FlinkManagerEC")
    initProperties.put(JobLauncherConfiguration.FLINK_MANAGER_EC_KEY.getValue, true.toString)
    initProperties.put(JobLauncherConfiguration.LINKIS_EC_EXPIRE_TIME_KEY.getValue, JobLauncherConfiguration.FLINKK_MANAGER_EXIT_TIME.getHotValue().toString)
    initProperties.put(JobLauncherConfiguration.LINKIS_EC_SUPPORT_PARALLEM, true.toString)

    if (StringUtils.isNotBlank(JobLauncherConfiguration.FLINK_MANAGER_EXTRA_INIT_CONFIGS.getValue)) {
      JobLauncherConfiguration.FLINK_MANAGER_EXTRA_INIT_CONFIGS.getValue.split(JobConstants.DELIMITER_COMMA).foreach(s => {
        if (StringUtils.isNotBlank(s)) {
          val arr = s.split(JobConstants.DELIMITER_EUQAL)
          if (null != arr && arr.length == 2) {
            val key = arr(0)
            val value = arr(1)
            if (initProperties.containsKey(arr(0))) {
              logger.warn(s"init extra params ${key}=${value} will overite params : ${key}=${initProperties.get(key)} for flink manager ec.")
            } else {
              logger.info(s"add init extra params : ${key}=${value} for flink manager ec.")
            }
            initProperties.put(key, value)
          }
        }
      })
    }

    var askEngineConnAction = AskEngineConnAction
      .newBuilder()
      .setCreateService(getClass.getSimpleName)
      .setLabels(initLabels)
      .setProperties(initProperties)
      .setUser(submitUser)
      .setMaxSubmitTime(30000)
      .setDescription("Ask a flink manager ec.")
      .build()
    var end = false
    val linkisManagerClient = getLinkisManagerClient
    var ecServiceInstance: ServiceInstance = null
    var ecmInstance: ServiceInstance = null
    logger.info(s"ask flink manager ec askEngineConnAction: ${askEngineConnAction.getRequestPayload}")
    var nodeInfo: util.Map[String, Any] = null
    Utils.tryCatch {
      nodeInfo = linkisManagerClient.askEngineConn(askEngineConnAction).getNodeInfo
    } {
      case e: Exception =>
        logger.error("call askEngineConn to get manager ec failed. ", e)
    }
    val tmpLabels = SerializationUtils.clone(initLabels).asInstanceOf[util.Map[String, String]]
    val tmpProps = SerializationUtils.clone(initProperties).asInstanceOf[util.Map[String, String]]
    var retryCount = 0
    val MAX_RETRY_COUNT = 10

    while (!end) {
      retryCount = retryCount + 1
      if (null != nodeInfo) {
        nodeInfo.get(AMConstant.EC_ASYNC_START_RESULT_KEY) match {
          case AMConstant.EC_ASYNC_START_RESULT_SUCCESS =>
            end = true
            ecServiceInstance = getServiceInstance(nodeInfo)
            ecmInstance = getECMInstance(nodeInfo)
            val ticketId = getTicketId(nodeInfo)
            val status = getAs[String](nodeInfo, ECConstants.NODE_STATUS_KEY)
            if (!NodeStatus.Unlock.toString.equalsIgnoreCase(status)) {
              logger.info(s"Get manager ec with status : ${status}")
              waitUntilRunOrFailed(ecServiceInstance, ticketId, submitUser)
            }
          case AMConstant.EC_ASYNC_START_RESULT_FAIL =>
            if (retryCount < MAX_RETRY_COUNT) {
              end = false
            } else {
              end = true
            }
            val failMsg = nodeInfo.getOrDefault(AMConstant.EC_ASYNC_START_FAIL_MSG_KEY, "no reason")
            logger.error(s"start flink manager ec failed because: ${failMsg}")
            logger.warn(s"askEngineConnAction: ${askEngineConnAction.getRequestPayload}")
            if (tmpProps.containsKey(AMConstant.EC_ASYNC_START_ID_KEY)) {
              tmpProps.remove(AMConstant.EC_ASYNC_START_ID_KEY)
            }
            if (nodeInfo.get(AMConstant.EC_ASYNC_START_FAIL_RETRY_KEY).toString.toBoolean) {
              logger.warn("start manager ec failed but can retry.")
            } else {
              throw new FlinkJobFlinkECErrorException(s"Start manager ec failed. Because : ${failMsg}")
            }
          case _ =>
            end = true
            logger.error(s"start flink manager ec failed because: unknonw ec result status : ${nodeInfo.get(AMConstant.EC_ASYNC_START_RESULT_KEY)}")
            logger.warn(s"askEngineConnAction: ${askEngineConnAction.getRequestPayload}")
            throw new FlinkJobFlinkECErrorException(s"Start manager ec failed. Unkown ec result status")
        }
      }
      Thread.sleep(1000)
      askEngineConnAction = AskEngineConnAction
        .newBuilder()
        .setCreateService(getClass.getSimpleName)
        .setLabels(tmpLabels)
        .setProperties(tmpProps)
        .setUser("hadoop")
        .setMaxSubmitTime(30000)
        .setDescription("Ask a flink manager ec.")
        .build()
      Utils.tryAndWarn{
        nodeInfo = linkisManagerClient.askEngineConn(askEngineConnAction).getNodeInfo
      }
      logger.debug(JsonUtils.jackson.writeValueAsString(nodeInfo))
    }
    if (null != ecServiceInstance) {
      logger.info("ecInstance : " + ecServiceInstance.toString())
    } else {
      logger.warn("Got null ecInstance.")
    }
    if (null != ecmInstance ) {
      logger.info("ecmInstance : " + ecmInstance.toString())
    } else {
      logger.warn("Got null ecm Instance")
    }
    (ecServiceInstance, ecmInstance)
  }

  private def waitUntilRunOrFailed(serviceInstance: ServiceInstance, ticketId: String, user: String): Unit = {
    var end = false
    val MAX_RETRY = 10
    var count = 0
    while (!end) {
      count = count + 1
      Utils.tryAndWarn {
        val result = LinkisManagerClient.apply(LinkisJobBuilder.getDefaultUJESClient).getEngineConn(GetEngineConnAction
          .newBuilder()
          .setApplicationName(serviceInstance.getApplicationName)
          .setInstance(serviceInstance.getInstance)
          .setTicketId(ticketId)
          .setUser(user)
          .build())
        val nodeInfo = result.getNodeInfo
        val status = getAs[String](nodeInfo, ECConstants.NODE_STATUS_KEY)
        logger.info(s"ec : ${serviceInstance.toString()} status : ${status}")
        if (NodeStatus.Unlock == NodeStatus.isCompleted(NodeStatus.toNodeStatus(status))) {
          end = true
        } else {
          end = false
        }
      }
      if (count > MAX_RETRY) {
        end = true
      }
      Thread.sleep(1000)
    }
  }

  override def setFlinkManagerEngineConnInstance(ecInstance: ServiceInstance): Unit = this.ecInstance = ecInstance

  override def getFlinkManagerEngineConnInstance(): ServiceInstance = {
    if (null == ecInstance) {
      refreshManagerEC()
    }
    ecInstance
  }

  override def setFlinkManagerECMInstance(ecmInstance: ServiceInstance): Unit = this.ecmInstance = ecmInstance

  override def getFlinkManagerECMInstance(): ServiceInstance = this.ecmInstance

  override def executeAction(action: FlinkManagerAction): Any = {
    action.setExeuteUser(JobLauncherConfiguration.FLINK_MANAGER_EC_SUBMIT_USER.getValue)
    action.getActionType match {
      case validType if FlinkManagerActionType.values().contains(validType) =>
        val ecInstance = getFlinkManagerEngineConnInstance()
        action.getPlayloads.put(ECConstants.EC_SERVICE_INSTANCE_KEY, ecInstance)
        action.getPlayloads().put(ECConstants.ECM_SERVICE_INSTANCE_KEY, getFlinkManagerECMInstance())
        action.setECInstance(getFlinkManagerEngineConnInstance())
        val builtAction = action.build()
        action.getOperationBoundry match {
          case OnceJobOperationBoundary.ECM =>
            if (builtAction.isInstanceOf[ECMOperateAction]) {
              return doExecution(builtAction.asInstanceOf[ECMOperateAction])
            } else {
              throw new FlinkJobParamErrorException("FlinkManagerAction build invalid ECMOperation with operationBounday : common")
            }
          case OnceJobOperationBoundary.EC =>
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
    val operation = JsonUtils.jackson.writeValueAsString(operationAction)
    if (!healthy.get()) {
      logger.warn(s"FlinkManager is not healthy, will skip the operation : ${operation}.")
      throw new FlinkJobFlinkECErrorException(s"FlinkManager is not healthy, skip the operation : ${operation}(管理EC异常，请稍后再试).")
    }
    val managerEcInstance = getFlinkManagerEngineConnInstance()
    val notFoundMsg = s"Ec : ${managerEcInstance.toString()} not found"
    Utils.tryCatch {
      var ecOperateResult = SimpleOnceJobBuilder.getLinkisManagerClient.executeEngineConnOperation(operationAction)
      if (ecOperateResult.getIsError()) {
        if (null != ecOperateResult.getErrorMsg() && ecOperateResult.getErrorMsg().contains(notFoundMsg)) {
          logger.warn(notFoundMsg)
          Utils.tryAndError(refreshManagerEC())
          ecOperateResult = SimpleOnceJobBuilder.getLinkisManagerClient.executeEngineConnOperation(operationAction)
          return ecOperateResult
        } else {
          logger.error(s"There are errors, but the errorMsg is null. rs : ${JsonUtils.jackson.writeValueAsString(ecOperateResult)}")
          return ecOperateResult
        }
      } else {
        return ecOperateResult
      }
    } {
      case e: Exception =>
        logger.error(s"executeEngineConnOperation failed with action : ${operationAction}", e)
        if (null == managerEcInstance || e.getMessage.contains(notFoundMsg)) {
          logger.error(e.getMessage)
          Utils.tryAndError(refreshManagerEC())
          return SimpleOnceJobBuilder.getLinkisManagerClient.executeEngineConnOperation(operationAction)
        }
        throw e
    }

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

  private def getManagerInstance(nodeInfo: util.Map[String, Any]): ServiceInstance =
    nodeInfo.getOrDefault(ECConstants.MANAGER_SERVICE_INSTANCE_KEY, null) match {
      case serviceInstance: ServiceInstance =>
        serviceInstance
      case _ =>
        null
    }

  private def getAs[T](map: util.Map[String, Any], key: String): T =
    map.getOrDefault(key, null).asInstanceOf[T]

  def getTicketId(nodeInfo: util.Map[String, Any]): String = getAs(nodeInfo, "ticketId")

  override def refreshManagerEC(): Unit = {
    LinkisFlinkManagerClient.ASK_EC_LOCK.synchronized {
      logger.info("Start to refresh manager ec.")

      def refreshEC(): Unit = {
        val rs = getOrCreateLinkisManagerECAndECM()
        ecInstance = rs._1
        ecmInstance = rs._2
        healthy.set(true)
      }

      Utils.tryCatch(refreshEC()) {
        case e: Exception =>
          logger.error("Refresh manager ec failed. Will try once.", e)
          healthy.set(false)
          Utils.tryCatch(refreshEC()) {
            case  e1: Exception =>
              logger.error("Refresh manager ec again failed. Will throw the exception.", e1)
              healthy.set(false)
              throw e1
          }
      }
    }
  }
}

object LinkisFlinkManagerClient extends Logging {

  private val CLIENT_LOCK = new Object()

  private val ASK_EC_LOCK = new Object()

  private var client: LinkisFlinkManagerClient = _

  def getInstance(): LinkisFlinkManagerClient = {
    if (null == client) {
      CLIENT_LOCK.synchronized {
        if (null == client) {
          client = new LinkisFlinkManagerClient
        }
      }
    }
    client
  }


  def initScheduledTask(): Unit = {
    if (!JobLauncherConfiguration.ENABLE_FLINK_MANAGER_EC_ENABLE.getHotValue()) {
      logger.info("Flink manager ec refresh task was disabled. Will skip the scheduled refreshing task.")
      return
    }
    Utils.defaultScheduler.scheduleWithFixedDelay(new Runnable {
      override def run(): Unit = {
        logger.info("Start to refresh manager EC.")
        Utils.tryAndError(getInstance().refreshManagerEC())
      }
    },
      5000,
      JobLauncherConfiguration.FLINK_MANAGER_EC_REFRESH_INTERVAL.getValue,
      TimeUnit.MILLISECONDS)
    logger.info("Manager EC refreshing task started.")
  }

}