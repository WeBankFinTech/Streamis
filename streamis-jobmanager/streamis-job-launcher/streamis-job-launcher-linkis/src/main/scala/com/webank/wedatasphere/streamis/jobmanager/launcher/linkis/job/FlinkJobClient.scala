/*
 * Copyright 2021 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.JobClient
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobStateInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.conf.JobLauncherConfiguration
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.core.{FlinkLogIterator, SimpleFlinkJobLogIterator}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LogRequestPayload
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.{FlinkJobLaunchErrorException, FlinkJobStateFetchException, FlinkSavePointException}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.manager.FlinkJobLaunchManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.operator.{FlinkClientLogOperator, FlinkTriggerSavepointOperator, FlinkYarnLogOperator}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state.{Checkpoint, Savepoint}
import org.apache.commons.lang3.StringUtils
import org.apache.linkis.common.utils.{Logging, Utils}
import org.apache.linkis.computation.client.once.action.ECResourceInfoAction
import org.apache.linkis.computation.client.once.result.ECResourceInfoResult
import org.apache.linkis.computation.client.once.{LinkisManagerClient, LinkisManagerClientImpl, OnceJob}
import org.apache.linkis.computation.client.once.simple.{SimpleOnceJob, SimpleOnceJobBuilder}
import org.apache.linkis.computation.client.operator.impl.EngineConnLogOperator
import org.apache.linkis.httpclient.dws.DWSHttpClient
import java.util
import java.net.URI

class FlinkJobClient(onceJob: OnceJob, var jobInfo: FlinkJobInfo, stateManager: JobStateManager)
  extends JobClient[FlinkJobInfo] with Logging{

  /**
   * Log operator
   */
  private var logOperatorMap = Map(
    "client" -> FlinkClientLogOperator.OPERATOR_NAME,
    "yarn" -> FlinkYarnLogOperator.OPERATOR_NAME
  )
  /**
   * The linkis client in onceJob
   */
  private var linkisClient: DWSHttpClient = _

  override def getJobInfo: FlinkJobInfo = {
    getJobInfo(false)
  }

  /**
   * Refresh job info and return
   *
   * @param refresh refresh
   * @return
   */
  override def getJobInfo(refresh: Boolean): FlinkJobInfo = {
    onceJob match {
      case simpleOnceJob: SimpleOnceJob =>
        simpleOnceJob.getStatus
        jobInfo.setStatus(if (refresh) onceJob.getNodeInfo
            .getOrDefault("nodeStatus", simpleOnceJob.getStatus).asInstanceOf[String] else simpleOnceJob.getStatus)
    }
    jobInfo
  }

  /**
   * Stop the job connected remote
   *
   * @param snapshot if do snapshot to save the job state
   */
  override def stop(snapshot: Boolean): JobStateInfo = {
    var stateInfo: JobStateInfo = null
    if (snapshot){
      // Begin to call the savepoint operator
      info(s"Trigger Savepoint operator for job [${jobInfo.getId}] before pausing job.")
      Option(triggerSavepoint()) match {
        case Some(savepoint) =>
          stateInfo = new JobStateInfo
          stateInfo.setLocation(savepoint.getLocation.toString)
          stateInfo.setTimestamp(savepoint.getTimestamp)
        case _ =>
      }
    }
    onceJob.kill()
    stateInfo
  }

  /**
   * Stop directly
   */
  override def stop(): Unit = stop(false)
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
  def getCheckpoints: Array[Checkpoint] = throw new FlinkJobStateFetchException(30401, "Not support method", null)


  /**
   * Trigger save point operation
   * @param savePointDir savepoint directory
   * @param mode mode
   */
  def triggerSavepoint(savePointDir: String, mode: String): Savepoint = {
    Utils.tryCatch{
      onceJob.getOperator(FlinkTriggerSavepointOperator.OPERATOR_NAME) match{
        case savepointOperator: FlinkTriggerSavepointOperator => {
          // TODO Get scheme information from job info
          savepointOperator.setSavepointDir(savePointDir)
          savepointOperator.setMode(mode)
          Option(savepointOperator()) match {
            case Some(savepoint: Savepoint) =>
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
        // TODO defined the code for savepoint exception
        throw new FlinkSavePointException(-1, "Fail to trigger savepoint operator", e)
    }
  }

  def triggerSavepoint(): Savepoint = {
    val savepointURI: URI = this.stateManager.getJobStateDir(classOf[Savepoint], jobInfo.getName)
    triggerSavepoint(savepointURI.toString, JobLauncherConfiguration.FLINK_TRIGGER_SAVEPOINT_MODE.getValue)
  }

  /**
   * Get linkis client
   * @return
   */
  def getLinkisClient: DWSHttpClient = {
    Utils.tryAndWarn{
      if (null == this.linkisClient){
        this.synchronized{
            if (null == this.linkisClient){
              this.linkisClient = SimpleOnceJobBuilder.getLinkisManagerClient match {
                case client: LinkisManagerClient =>
                  val dwsClientField = classOf[LinkisManagerClientImpl].getDeclaredField("dwsHttpClient")
                  dwsClientField.setAccessible(true)
                  dwsClientField.get(client).asInstanceOf[DWSHttpClient]
                case _ => null
              }

            }
        }
      }
      this.linkisClient
    }
  }
}
