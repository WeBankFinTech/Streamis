package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client

import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.vo.YarnAppVo
import com.webank.wedatasphere.streamis.jobmanager.launcher.enums.JobClientType
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.JobInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf.JobConf
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.constants.JobConstants
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.errorcode.JobLaunchErrorCode
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.exception.JobFetchErrorException
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobStateInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.conf.JobLauncherConfiguration
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.{FlinkJobKillECErrorException, FlinkJobParamErrorException, FlinkJobStateFetchException, FlinkSavePointException}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.action.{FlinkKillAction, FlinkSaveAction, FlinkStatusAction, ListYarnAppAction}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client.LinkisFlinkManagerJobClient.linkisFlinkManagerClient
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.jobInfo.EngineConnJobInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state.FlinkSavepoint
import org.apache.commons.lang3.StringUtils
import org.apache.linkis.common.exception.LinkisRetryException
import org.apache.linkis.common.utils.{JsonUtils, Logging, RetryHandler, Utils}
import org.apache.linkis.computation.client.once.OnceJob
import org.apache.linkis.computation.client.once.result.EngineConnOperateResult
import org.apache.linkis.computation.client.once.simple.SimpleOnceJob
import org.apache.linkis.governance.common.constant.ec.ECConstants
import org.apache.linkis.ujes.client.exception.UJESJobException

import java.io.IOException
import java.util
import scala.collection.JavaConverters.asScalaBufferConverter

class LinkisFlinkManagerJobClient(onceJob: OnceJob, jobInfo: JobInfo, stateManager: JobStateManager) extends EngineConnJobClient(onceJob, jobInfo, stateManager) {


  override def init(): Unit = {
    super.init()
    logger.info("LinkisFlinkManagerJobClient inited.")
  }


  private def isDetachJob(info: JobInfo): Boolean = {
    JobClientType.OTHER.toJobClientType(jobInfo.getClientType.toLowerCase()) match {
      case JobClientType.ATTACH =>
        false
      case JobClientType.DETACH =>
        true
      case JobClientType.DETACH_STANDALONE =>
        // TODO  check
       true
      case _ =>
        throw new FlinkJobParamErrorException(s"Job with manager mode : ${jobInfo.getClientType} cannot be submited.", null)
    }
  }

  override def getJobInfo(refresh: Boolean): JobInfo = {
    onceJob match {
      case simpleOnceJob: SimpleOnceJob =>
        if (StringUtils.isNotBlank(jobInfo.getStatus) && JobConf.isCompleted(JobConf.linkisStatusToStreamisStatus(jobInfo.getStatus))) {
//          jobInfo.setStatus(simpleOnceJob.getStatus)
          logger.info(s"Job : ${simpleOnceJob.getId} is completed : ${jobInfo.getStatus}, no need to get status from linkis.")
        } else if (refresh && isDetachJob(jobInfo)) {
          jobInfo match {
            case engineConnJobInfo: EngineConnJobInfo =>
              jobInfo.setStatus(getJobStatusWithRetry(engineConnJobInfo.getApplicationId))
            case _ =>
              throw new FlinkJobParamErrorException(s"Invalid jobInfo : ${jobInfo} , cannot get status.", null)
          }
        } else {
          return super.getJobInfo(refresh)
        }
    }
    jobInfo
  }

  override def stop(snapshot: Boolean): JobStateInfo = {
    if (isDetachJob(jobInfo)) {
      jobInfo match {
        case engineConnJobInfo: EngineConnJobInfo =>
          val appId = engineConnJobInfo.getApplicationId
          return stopApp(appId, snapshot)
        case _ =>
          throw new FlinkJobParamErrorException(s"Invalid jobInfo : ${jobInfo} , cannot stop.", null)
      }
    }
    Utils.tryAndWarn(super.stop(snapshot))
  }

  def getJobStatusWithRetry(appId: String): String = {
    val retryHandler = new RetryHandler {}
    retryHandler.setRetryNum(3)
    retryHandler.setRetryMaxPeriod(5000)
    retryHandler.setRetryPeriod(1000)
    retryHandler.addRetryException(classOf[UJESJobException])
    retryHandler.addRetryException(classOf[LinkisRetryException])
    retryHandler.addRetryException(classOf[FlinkJobStateFetchException])
    retryHandler.addRetryException(classOf[IOException])
    retryHandler.retry(
      {
    val statusAction = new FlinkStatusAction(appId, null)
    val rs = linkisFlinkManagerClient.executeAction(statusAction)
    rs match {
      case engineConnOperateResult: EngineConnOperateResult =>
        if (engineConnOperateResult.getIsError()) {
          throw new FlinkJobStateFetchException(errorMsg = s"Get status error. Because : ${engineConnOperateResult.getErrorMsg()}", t = null)
        }
        val rsMap = engineConnOperateResult.getResult
        val status = rsMap.getOrDefault(ECConstants.NODE_STATUS_KEY, null)
            logger.info(s"AppId : ${appId} got status : ${status}")
        if (null != status) {
          return status.toString
        } else {
          val json = JsonUtils.jackson.writeValueAsString(rsMap)
          throw new FlinkJobStateFetchException(errorMsg = s"Get invalid status. Result map : ${json}", t = null)
        }
      case _ =>
        val json = JsonUtils.jackson.writeValueAsString(rs)
        throw new FlinkJobStateFetchException(errorMsg = s"Get invalid result. Response json : ${json}", t = null)
    }
      },
      "Retry-Get-Status")
  }

  def stopApp(appId: String, snapshot: Boolean): JobStateInfo = {
    val jobStateInfo = new JobStateInfo()
    if (snapshot) {
      val savepointURI = this.stateManager.getJobStateDir(classOf[FlinkSavepoint], jobInfo.getName,jobInfo.getHighAvailablePolicy)
      val flinkSavepoint = doSavePoint(appId, null, savepointURI.toString, JobLauncherConfiguration.FLINK_TRIGGER_SAVEPOINT_MODE.getValue)
      jobStateInfo.setLocation(flinkSavepoint.getLocation.toString)
      jobStateInfo.setTimestamp(flinkSavepoint.getTimestamp)
    }
    val stopAction = new FlinkKillAction(appId, null)
    val rs = linkisFlinkManagerClient.executeAction(stopAction)
    rs match {
      case engineConnOperateResult: EngineConnOperateResult =>
        if (engineConnOperateResult.getIsError()) {
          throw new FlinkJobStateFetchException(errorMsg = s"Get status error. Because : ${engineConnOperateResult.getErrorMsg()}", t = null)
        }
      case _ =>
        val json = JsonUtils.jackson.writeValueAsString(rs)
        throw new FlinkJobStateFetchException(errorMsg = s"Get invalid result. Response json : ${json}", t = null)
    }
    if (StringUtils.isBlank(jobStateInfo.getLocation)) {
      jobStateInfo.setLocation("No location")
    }
    // wait for task to change status to stopped
    val MAX_WAIT_TIME = JobLauncherConfiguration.MAX_WAIT_NUM_AFTER_KILL.getHotValue()
    val WAIT_MILLS = 1000
    var retryNum = 0
    while (retryNum < MAX_WAIT_TIME && StringUtils.isNotBlank(jobInfo.getStatus) && !JobConf.isCompleted(JobConf.linkisStatusToStreamisStatus(jobInfo.getStatus))) {
      retryNum += 1
      jobInfo.setStatus(getJobStatusWithRetry(appId))
      Thread.sleep(WAIT_MILLS)
    }
    if (retryNum >= MAX_WAIT_TIME) {
      val times = MAX_WAIT_TIME * WAIT_MILLS / 1000.0
      logger.warn(s"waiting for ${times}s after app : ${appId} was killed, but the task is not completed , status : ${jobInfo.getStatus}")
    }
    jobStateInfo
  }

  override def triggerSavepoint(savePointDir: String, mode: String): FlinkSavepoint = {
    if (isDetachJob(jobInfo)) {
      var appId: String = null
      jobInfo match {
        case engineConnJobInfo: EngineConnJobInfo =>
          appId = engineConnJobInfo.getApplicationId
      }
      doSavePoint(appId, null, savePointDir, mode)
    } else {
      super.triggerSavepoint(savePointDir, mode)
    }
  }

  def doSavePoint(appId: String, msg: String, savePointDir: String, mode: String): FlinkSavepoint = {

    val savepointAction = new FlinkSaveAction(appId, msg)
    savepointAction.setSavepointPath(savePointDir)
    savepointAction.setMode(mode)
    val rs = linkisFlinkManagerClient.executeAction(savepointAction)
    rs match {
      case engineConnOperateResult: EngineConnOperateResult =>
        if (engineConnOperateResult.getIsError()) {
          throw new FlinkJobKillECErrorException(s"Do savepoint error. Because : ${engineConnOperateResult.getErrorMsg()}", null)
        }
        val writePath = engineConnOperateResult.getAs[String](JobConstants.RESULT_SAVEPOINT_PATH_KEY)
        if (StringUtils.isBlank(writePath)) {
          val msg = s"Do savepoint error got null write path. Errormsg : ${engineConnOperateResult.getErrorMsg()} "
          throw new FlinkSavePointException(errorMsg = msg, t = null)
        }
        new FlinkSavepoint(writePath)
      case _ =>
        val rsMsg = JsonUtils.jackson.writeValueAsString(rs)
        val msg = s"Get status error. Result : ${rsMsg}"
        throw new FlinkSavePointException(errorMsg = msg, t = null)
    }
  }

}

object LinkisFlinkManagerJobClient extends Logging {

  private lazy val linkisFlinkManagerClient: LinkisFlinkManagerClient = LinkisFlinkManagerClient.getInstance()

  def listYarnApp(jobName: String, user: String, msg: String = "streamis", appTypeList: util.List[String]): util.List[YarnAppVo] = {
    val resultList = new util.ArrayList[YarnAppVo]()
    val listAction = new ListYarnAppAction(jobName, user, msg, appTypeList)
    listAction.setExeuteUser(user)
    val result = linkisFlinkManagerClient.executeAction(listAction).asInstanceOf[EngineConnOperateResult]
    if (result.getIsError()) {
      val msg = s"list failed for jobName : ${jobName} and user : ${user}, because : ${result.getErrorMsg()}"
      logger.error(msg)
      throw new JobFetchErrorException(JobLaunchErrorCode.JOB_LIST_YARN_APP_ERROR, msg)
    } else {
      val rsMap = result.getResult
      if (rsMap.containsKey(ECConstants.YARN_APP_RESULT_LIST_KEY)) {
        val rsListStr = rsMap.get(ECConstants.YARN_APP_RESULT_LIST_KEY).asInstanceOf[String]
        val rsList = JsonUtils.jackson.readValue(rsListStr, classOf[util.List[util.Map[String, String]]])
        rsList.asScala.foreach(map => {
          val tmpVo = new YarnAppVo()
          tmpVo.setApplicationName(map.getOrDefault(ECConstants.YARN_APP_NAME_KEY, null))
          tmpVo.setApplicationState(map.getOrDefault(ECConstants.NODE_STATUS_KEY, null))
          tmpVo.setYarnAppType(map.getOrDefault(ECConstants.YARN_APP_TYPE_KEY, null))
          tmpVo.setApplicationId(map.getOrDefault(ECConstants.YARN_APPID_NAME_KEY, null))
          tmpVo.setApplicationUrl(map.getOrDefault(ECConstants.YARN_APP_URL_KEY, null))
          resultList.add(tmpVo)
        })
      }
      resultList
    }
  }
}