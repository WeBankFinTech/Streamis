package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobStateInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.conf.JobLauncherConfiguration
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.core.{FlinkLogIterator, SimpleFlinkJobLogIterator}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LogRequestPayload
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.{FlinkJobStateFetchException, FlinkSavePointException}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.FlinkJobInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.manager.FlinkJobLaunchManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.operator.{FlinkClientLogOperator, FlinkTriggerSavepointOperator, FlinkYarnLogOperator}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state.{FlinkCheckpoint, FlinkSavepoint}
import org.apache.commons.lang3.StringUtils
import org.apache.linkis.common.utils.Utils
import org.apache.linkis.computation.client.once.action.ECResourceInfoAction
import org.apache.linkis.computation.client.once.result.ECResourceInfoResult
import org.apache.linkis.computation.client.once.OnceJob
import org.apache.linkis.computation.client.operator.impl.EngineConnLogOperator
import org.apache.linkis.httpclient.dws.DWSHttpClient

import java.net.URI
import java.util

/**
 * @author jefftlin
 */
class EngineConnJobClient(onceJob: OnceJob, jobInfo: FlinkJobInfo, stateManager: JobStateManager)
  extends LinkisEngineConnJobClient(onceJob, jobInfo, stateManager) {

  /**
   * Log operator
   */
  private var logOperatorMap = Map(
    "client" -> FlinkClientLogOperator.OPERATOR_NAME,
    "yarn" -> FlinkYarnLogOperator.OPERATOR_NAME
  )

  /**
   * Trigger save point operation with savePointDir and mode
   *
   * @param savePointDir savepoint directory
   * @param mode mode
   */
  override def triggerSavepoint(savePointDir: String, mode: String): FlinkSavepoint = {
    Utils.tryCatch{
      onceJob.getOperator(FlinkTriggerSavepointOperator.OPERATOR_NAME) match{
        case savepointOperator: FlinkTriggerSavepointOperator => {
          // TODO Get scheme information from job info
          savepointOperator.setSavepointDir(savePointDir)
          savepointOperator.setMode(mode)
          Option(savepointOperator()) match {
            case Some(savepoint: FlinkSavepoint) =>
              savepoint
            // TODO store into job Info
            case _ => throw new FlinkSavePointException(-1, "The response savepoint info is empty", null)
          }
        }
      }
    }{
      case se: FlinkSavePointException =>
        throw se
      case e: Exception =>
        throw new FlinkSavePointException(30501, "Fail to trigger savepoint operator", e)
    }
  }

  /**
   * Fetch logs
   * @param requestPayload request payload
   * @return
   */
  def fetchLogs(requestPayload: LogRequestPayload): FlinkLogIterator = {
    logOperatorMap.get(requestPayload.getLogType) match {
      case Some(operator) =>
        onceJob.getOperator(operator) match {
          case engineConnLogOperator: EngineConnLogOperator =>
            val logIterator = new SimpleFlinkJobLogIterator(requestPayload, engineConnLogOperator)
            engineConnLogOperator match {
              case clientLogOperator: FlinkClientLogOperator =>
                var logDirSuffix = this.jobInfo.getLogDirSuffix
                if (StringUtils.isBlank(logDirSuffix) && requestPayload.isLogHistory){
                  // If want to fetch the history log, must get the log directory suffix first
                  getLinkisClient match {
                    case client: DWSHttpClient =>
                      Option(Utils.tryCatch{
                        client.execute(ECResourceInfoAction.newBuilder().setUser(jobInfo.getUser)
                          .setTicketid(clientLogOperator.getTicketId).build()).asInstanceOf[ECResourceInfoResult]
                      }{
                        case e: Exception =>
                          warn("Fail to query the engine conn resource info from linkis",  e)
                          null
                      }) match {
                        case Some(result) => logDirSuffix = Utils.tryAndWarn{result.getData.getOrDefault("ecResourceInfoRecord", new util.HashMap[String, Any]).asInstanceOf[util.Map[String, Any]]
                          .getOrDefault("logDirSuffix", "").asInstanceOf[String]}
                        case _ =>
                      }
                  }
                }
                clientLogOperator.setLogDirSuffix(logDirSuffix)
                logIterator.setLogDirSuffix(logDirSuffix)
              case _ =>
            }
            engineConnLogOperator match {
              case yarnLogOperator: FlinkYarnLogOperator => yarnLogOperator.setApplicationId(jobInfo.getApplicationId)
              case _ =>
            }
            engineConnLogOperator.setECMServiceInstance(jobInfo.getECMInstance)
            engineConnLogOperator.setEngineConnType(FlinkJobLaunchManager.FLINK_ENGINE_CONN_TYPE)
            logIterator.init()
            jobInfo match {
              case jobInfo: FlinkJobInfo => jobInfo.setLogPath(logIterator.getLogPath)
              case _ =>
            }
            logIterator
        }
      case None =>
        throw new FlinkJobStateFetchException(-1, s"Unrecognized log type: ${requestPayload.getLogType}", null)
    }
  }

  /**
   * Get check points
   * @return
   */
  def getCheckpoints: Array[FlinkCheckpoint] = throw new FlinkJobStateFetchException(30401, "Not support method", null)

}
