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

package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.{JobClient, JobInfo}
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobStateInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.conf.JobLauncherConfiguration
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.FlinkSavePointException
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.jobInfo.EngineConnJobInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.operator.FlinkTriggerSavepointOperator
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state.FlinkSavepoint
import org.apache.linkis.common.ServiceInstance
import org.apache.linkis.common.utils.{Logging, Utils}
import org.apache.linkis.computation.client.once.simple.{SimpleOnceJob, SubmittableSimpleOnceJob}
import org.apache.linkis.computation.client.once.OnceJob

import java.net.URI

/**
 * @author jefftlin
 */
abstract class AbstractJobClient(onceJob: OnceJob, jobInfo: JobInfo, stateManager: JobStateManager)
  extends JobClient[JobInfo] with Logging{

  /**
   * Refresh job info and return
   *
   * @return
   */
  override def getJobInfo: JobInfo = getJobInfo(false)

  /**
   * Refresh job info and return
   *
   * @param refresh refresh
   * @return
   */
  override def getJobInfo(refresh: Boolean): JobInfo = {
    onceJob match {
      case simpleOnceJob: SimpleOnceJob =>
        Utils.tryCatch {
        jobInfo.setStatus(if (refresh && null != onceJob.getNodeInfo) onceJob.getNodeInfo
          .getOrDefault("nodeStatus", simpleOnceJob.getStatus).asInstanceOf[String] else simpleOnceJob.getStatus)
        } {
          case e: Exception =>
            val ec: ServiceInstance = simpleOnceJob.getEcServiceInstance
            if (null != ec) {
              if (e.getMessage.contains(s"Instance does not exist ServiceInstance(linkis-cg-engineconn, ${ec.getInstance}")) {
                logger.warn(s"EC instance : ${ec.toString()} not exist, will set status to Failed.")
                jobInfo.setStatus("Failed")
              }
            } else {
              logger.error(s"EC instance of job : ${jobInfo.getId} is null, no need to get job status.")
              throw e
            }
        }
    }
    jobInfo
  }

  /**
   * Stop directly
   * SparkRestJobClient need to override
   */
  override def stop(): Unit = stop(false)

  /**
   * Stop the job connected remote
   * Used in FlinkRestJobClient & EngineConnJobClient
   * Not support SparkRestJobClient
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
   * Trigger save point operation
   * Used in FlinkRestJobClient & EngineConnJobClient
   * Not support SparkRestJobClient
   *
   * @return
   */
  def triggerSavepoint(): FlinkSavepoint = {
    getJobInfo(true)
    val savepointURI = this.stateManager.getJobStateDir(classOf[FlinkSavepoint], jobInfo.getName,jobInfo.getHighAvailablePolicy)
    triggerSavepoint(savepointURI.toString, JobLauncherConfiguration.FLINK_TRIGGER_SAVEPOINT_MODE.getValue)
  }

  /**
   * Trigger save point operation with savePointDir and mode
   * Used in FlinkRestJobClient & EngineConnJobClient
   * Not support SparkRestJobClient
   *
   * @param savePointDir savepoint directory
   * @param mode         mode
   */
  def triggerSavepoint(savePointDir: String, mode: String): FlinkSavepoint = {
    Utils.tryCatch{
      onceJob.getOperator(FlinkTriggerSavepointOperator.OPERATOR_NAME) match{
        case savepointOperator: FlinkTriggerSavepointOperator => {
          jobInfo match {
            case engineConnJobInfo: EngineConnJobInfo =>
              savepointOperator.setApplicationId(engineConnJobInfo.getApplicationId)
          }
          savepointOperator.setSavepointDir(savePointDir)
          savepointOperator.setMode(mode)
          Option(savepointOperator()) match {
            case Some(savepoint: FlinkSavepoint) =>
              savepoint
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

}
