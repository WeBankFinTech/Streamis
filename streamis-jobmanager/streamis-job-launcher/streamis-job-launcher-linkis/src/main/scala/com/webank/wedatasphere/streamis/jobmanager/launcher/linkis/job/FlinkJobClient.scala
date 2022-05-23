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
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.conf.JobLauncherConfiguration
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.core.{FlinkLogIterator, SimpleFlinkJobLogIterator}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LogRequestPayload
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.{FlinkJobLaunchErrorException, FlinkJobStateFetchException, FlinkSavePointException}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.manager.FlinkJobLaunchManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.operator.FlinkTriggerSavepointOperator
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state.{Checkpoint, Savepoint}
import org.apache.linkis.common.utils.{Logging, Utils}
import org.apache.linkis.computation.client.once.OnceJob
import org.apache.linkis.computation.client.once.simple.SimpleOnceJob
import org.apache.linkis.computation.client.operator.impl.EngineConnLogOperator

import java.net.URI

class FlinkJobClient(onceJob: OnceJob, var jobInfo: LinkisJobInfo, stateManager: JobStateManager)
  extends JobClient[LinkisJobInfo] with Logging{

  override def getJobInfo: LinkisJobInfo = {
    getJobInfo(false)
  }

  /**
   * Refresh job info and return
   *
   * @param refresh refresh
   * @return
   */
  override def getJobInfo(refresh: Boolean): LinkisJobInfo = {
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
  override def stop(snapshot: Boolean): Unit = {
    if (snapshot){
      // Begin to call the savepoint operator
      info(s"Trigger Savepoint operator for job [${jobInfo.getId}]")
    }
    onceJob.kill()
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
  def fetchLogs(requestPayload: LogRequestPayload): FlinkLogIterator = onceJob.getOperator(EngineConnLogOperator.OPERATOR_NAME) match {
    case engineConnLogOperator: EngineConnLogOperator =>
      engineConnLogOperator.setECMServiceInstance(jobInfo.getECMInstance)
      engineConnLogOperator.setEngineConnType(FlinkJobLaunchManager.FLINK_ENGINE_CONN_TYPE)
      val logIterator = new SimpleFlinkJobLogIterator(requestPayload, engineConnLogOperator)
      logIterator.init()
      jobInfo match {
        case jobInfo: FlinkJobInfo => jobInfo.setLogPath(logIterator.getLogPath)
        case _ =>
      }
      logIterator
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
  def triggerSavepoint(savePointDir: String, mode: String): Unit = {
    Utils.tryCatch{
      onceJob.getOperator(FlinkTriggerSavepointOperator.OPERATOR_NAME) match{
        case savepointOperator: FlinkTriggerSavepointOperator => {
          // TODO Get scheme information from job info
          savepointOperator.setSavepointDir(savePointDir)
          savepointOperator.setMode(mode)
          Option(savepointOperator()) match {
            case Some(savepoint: Savepoint) =>
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

  def triggerSavepoint(): Unit = {
    val savepointURI: URI = this.stateManager.getJobStateDir(classOf[Savepoint],
      JobLauncherConfiguration.FLINK_STATE_DEFAULT_SCHEME.getValue, JobLauncherConfiguration.FLINK_STATE_DEFAULT_AUTHORITY.getValue, jobInfo.getName)
    triggerSavepoint(savepointURI.toString, JobLauncherConfiguration.FLINK_TRIGGER_SAVEPOINT_MODE.getValue)
  }


}
