package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client

import com.webank.wedatasphere.streamis.jobmanager.launcher.enums.FlinkManagerActionType
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.constants.JobConstants
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.{JobGenericState, JobStateInfo}
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.{FlinkManagerAction, FlinkManagerClient, JobClient, JobInfo}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.conf.JobLauncherConfiguration
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.{FlinkJobFlinkECErrorException, FlinkJobLaunchErrorException, FlinkJobParamErrorException}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client.LinkisFlinkManagerClient.initScheduledTask
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.jobInfo.EngineConnJobInfo
import org.apache.commons.lang3.{SerializationUtils, StringUtils}
import org.apache.linkis.common.ServiceInstance
import org.apache.linkis.common.utils.{JsonUtils, Logging, Utils}
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
import org.apache.linkis.manager.label.entity.TenantLabel
import org.apache.linkis.manager.label.entity.engine.{CodeLanguageLabel, EngineType, EngineTypeLabel, RunType, UserCreatorLabel}

import java.util
import java.util.concurrent.TimeUnit
import scala.collection.JavaConverters.mapAsScalaMapConverter
import scala.tools.scalap.scalax.util.StringUtil

class LinkisFlinkManagerClient extends FlinkManagerClient with Logging {

  private var ecInstance: ServiceInstance = _
  private var ecmInstance: ServiceInstance = _

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
    initLabels.put(engineTypeLabel.getLabelKey, engineTypeLabel.getStringValue)
    initLabels.put(codeTypeLabel.getLabelKey, codeTypeLabel.getStringValue)
    initLabels.put(userCreatorLabel.getLabelKey, userCreatorLabel.getStringValue)
    val initProperties = new util.HashMap[String, String]()

    initProperties.put("flink.app.savePointPath", "./tmp")
    initProperties.put("flink.app.name", "FlinkManagerEC")
    initProperties.put(JobLauncherConfiguration.FLINK_MANAGER_MODE_KEY.getValue, true.toString)
    initProperties.put(AMConstant.EC_SYNC_START_KEY, true.toString)

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
    var nodeInfo = linkisManagerClient.askEngineConn(askEngineConnAction).getNodeInfo
    val tmpLabels = SerializationUtils.clone(initLabels).asInstanceOf[util.Map[String, String]]
    val tmpProps = SerializationUtils.clone(initProperties).asInstanceOf[util.Map[String, String]]
    var lastAsyncId: String = null
    var lastManagerInstance: ServiceInstance = null
    var retryCount = 0
    val MAX_RETRY_COUNT = 10

    while (!end) {
      retryCount = retryCount + 1
      nodeInfo.get(AMConstant.EC_ASYNC_START_RESULT_KEY) match {
        case AMConstant.EC_ASYNC_START_RESULT_SUCCESS =>
          end = true
          ecServiceInstance = getServiceInstance(nodeInfo)
          ecmInstance = getECMInstance(nodeInfo)
        case AMConstant.EC_ASYNC_START_RESULT_STARTING =>
          end = false
          if (initProperties.getOrDefault(AMConstant.EC_SYNC_START_KEY, "false").toBoolean) {
            logger.error("EC is starting but syncStart is true, there are errors in the linkismanager.")
          }
          val asyncId = nodeInfo.get(AMConstant.EC_ASYNC_START_ID_KEY).toString
          val ecmInstance = getECMInstance(nodeInfo)
          val managerInstance = getManagerInstance(nodeInfo)
          tmpProps.put(AMConstant.EC_ASYNC_START_ID_KEY, asyncId)
          if (null == lastAsyncId) {
            lastAsyncId = asyncId
          }
          if (null == lastManagerInstance && null != managerInstance) {
            lastManagerInstance = managerInstance
          }
          if (!lastAsyncId.equals(asyncId)) {
            logger.error(s"lastAsyncId from : ${lastAsyncId} changed to ${asyncId}.")
          }
          if (null != lastManagerInstance && !lastManagerInstance.equals(ecmInstance)) {
            logger.error("Manager instance changed! Please use fixed manager.")
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
        .setProperties(tmpProps)
        .setUser("hadoop")
        .setMaxSubmitTime(30000)
        .setDescription("Ask a flink manager ec.")
        .build()
      nodeInfo = linkisManagerClient.askEngineConn(askEngineConnAction).getNodeInfo
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
    Utils.tryCatch {
      SimpleOnceJobBuilder.getLinkisManagerClient.executeEngineConnOperation(operationAction)
    } {
      case e: Exception =>
        logger.error(s"executeEngineConnOperation failed with action : ${operationAction}")
        Utils.defaultScheduler.submit(new Runnable {
          override def run(): Unit = {
            logger.info("Will refresh manager ec in single thread.")
            Utils.tryAndError(refreshManagerEC())
          }
        })
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
    map.get(key).asInstanceOf[T]

  def getTicketId(nodeInfo: util.Map[String, Any]): String = getAs(nodeInfo, "ticketId")

  override def refreshManagerEC(): Unit = {
    LinkisFlinkManagerClient.ASK_EC_LOCK.synchronized {
      val rs = getOrCreateLinkisManagerECAndECM()
      ecInstance = rs._1
      ecmInstance = rs._2
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
    if (!JobLauncherConfiguration.ENABLE_FLINK_MANAGER_EC_REFRESH_TASK.getValue) {
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