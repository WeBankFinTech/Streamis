package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.JobInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.core.{FlinkLogIterator, SimpleFlinkJobLogIterator}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LogRequestPayload
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.FlinkJobStateFetchException
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.jobInfo.{EngineConnJobInfo}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.manager.FlinkJobLaunchManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.operator.{FlinkClientLogOperator, FlinkYarnLogOperator}
import org.apache.commons.lang3.StringUtils
import org.apache.linkis.common.utils.Utils
import org.apache.linkis.computation.client.once.action.ECResourceInfoAction
import org.apache.linkis.computation.client.once.result.ECResourceInfoResult
import org.apache.linkis.computation.client.once.OnceJob
import org.apache.linkis.computation.client.operator.impl.EngineConnLogOperator
import org.apache.linkis.httpclient.dws.DWSHttpClient

import java.util

/**
 * @author jefftlin
 */
class EngineConnJobClient(onceJob: OnceJob, jobInfo: JobInfo, stateManager: JobStateManager)
  extends LinkisEngineConnJobClient(onceJob, jobInfo, stateManager) {

  /**
   * Log operator
   */
  private var logOperatorMap = Map(
    "client" -> FlinkClientLogOperator.OPERATOR_NAME,
    "yarn" -> FlinkYarnLogOperator.OPERATOR_NAME
  )

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
                var logDirSuffix = this.jobInfo.asInstanceOf[EngineConnJobInfo].getLogDirSuffix
                if (StringUtils.isBlank(logDirSuffix)){
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
                        case Some(result) => logDirSuffix = Utils.tryQuietly{Option(result.getData).getOrElse(new util.HashMap[String, AnyRef]()).getOrDefault("ecResourceInfoRecord", new util.HashMap[String, AnyRef]).asInstanceOf[util.Map[String, AnyRef]]
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
              case yarnLogOperator: FlinkYarnLogOperator => yarnLogOperator.setApplicationId(jobInfo.asInstanceOf[EngineConnJobInfo].getApplicationId)
              case _ =>
            }
            engineConnLogOperator.setECMServiceInstance(jobInfo.asInstanceOf[EngineConnJobInfo].getECMInstance)
            engineConnLogOperator.setEngineConnType(FlinkJobLaunchManager.FLINK_ENGINE_CONN_TYPE)
            logIterator.init()
            jobInfo match {
              case jobInfo: EngineConnJobInfo => jobInfo.setLogPath(logIterator.getLogPath)
              case _ =>
            }
            logIterator
        }
      case None =>
        throw new FlinkJobStateFetchException(-1, s"Unrecognized log type: ${requestPayload.getLogType}", null)
    }
  }

}
