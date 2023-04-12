package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client

import com.webank.wedatasphere.streamis.jobmanager.launcher.enums.JobClientType
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.constants.JobConstants
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.errorcode.JobLaunchErrorCode
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.{FlinkManagerClient, JobClient, JobInfo}
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobStateInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.{FlinkJobParamErrorException, FlinkJobStateFetchException}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.action.{FlinkKillAction, FlinkStatusAction}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.jobInfo.{EngineConnJobInfo, LinkisJobInfo}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state.FlinkSavepoint
import org.apache.linkis.common.utils.JsonUtils
import org.apache.linkis.computation.client.once.OnceJob
import org.apache.linkis.computation.client.once.result.EngineConnOperateResult
import org.apache.linkis.computation.client.once.simple.SimpleOnceJob
import org.apache.linkis.governance.common.constant.ec.ECConstants
import org.apache.linkis.protocol.utils.TaskUtils

import java.util
import scala.collection.JavaConverters.mapAsScalaMapConverter

class LinkisFlinkManagerJobClient(onceJob: OnceJob, jobInfo: JobInfo, stateManager: JobStateManager) extends EngineConnJobClient(onceJob, jobInfo, stateManager) {

  private lazy val linkisFlinkManagerClient: LinkisFlinkManagerClient = LinkisFlinkManagerClient.getInstance()

  override def init(): Unit = {
    super.init()
    // 初始化linkisclient，调用managerexecutor进行操作的client
  }


  private def isDetachJob(info: JobInfo): Boolean = {
    /*jobInfo match {
      case engineConnJobInfo: EngineConnJobInfo =>
        val startupParams = TaskUtils.getStartupMap(engineConnJobInfo.getJobParams())
        val stringParams = new util.HashMap[String, String]
        startupParams.asScala.foreach{case (k, v) => stringParams.put(k, v.toString)}
        JobConfKeyConstants.MANAGER_MODE.getValue(stringParams) match {
          case JobConstants.MANAGER_MODE_DETACH =>
            true
          case JobConstants.MANAGER_MODE_ATTACH =>
            false
          case JobConstants.MANAGER_MODE_MANAGER =>
            throw new FlinkJobParamErrorException("Job with manager mode : MANAGER cannot be submited.", null)
        }
      case _ =>
        false
    }*/
    JobClientType.valueOf(jobInfo.getClientType) match {
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
        // TODO check
        if (refresh && isDetachJob(jobInfo)) {
          jobInfo match {
            case engineConnJobInfo: EngineConnJobInfo =>
              jobInfo.setStatus(getJobStatus(engineConnJobInfo.getApplicationId))
            case _ =>
              throw new FlinkJobParamErrorException(s"Invalid jobInfo : ${jobInfo} , cannot get status.", null)
          }
        } else {
          return super.getJobInfo(refresh)
        }
        jobInfo.setStatus(if (refresh) onceJob.getNodeInfo
          .getOrDefault("nodeStatus", simpleOnceJob.getStatus).asInstanceOf[String] else simpleOnceJob.getStatus)
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
    } else {
      return super.stop(snapshot)
    }
  }

  def getJobStatus(appId: String): String = {
    val statusAction = new FlinkStatusAction(appId, null)
    val rs = linkisFlinkManagerClient.executeAction(statusAction)
    rs match {
      case engineConnOperateResult: EngineConnOperateResult =>
        if (engineConnOperateResult.getIsError()) {
          throw new FlinkJobStateFetchException(errorMsg = s"Get status error. Because : ${engineConnOperateResult.getErrorMsg()}", t = null)
        }
        val rsMap = engineConnOperateResult.getResult
        val status = rsMap.getOrDefault(ECConstants.NODE_STATUS_KEY, null)
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
  }

  def stopApp(appId: String, snapshot: Boolean): JobStateInfo = {
    val stopAction = new FlinkKillAction(appId, null, snapshot)
    val rs = linkisFlinkManagerClient.executeAction(stopAction)
    // TODO
    new JobStateInfo("Job stop success.", 1, snapshot)
  }

}
