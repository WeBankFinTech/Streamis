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

package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.manager

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.LaunchJob
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.conf.JobLauncherConfiguration
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.core.{FlinkLogIterator, SimpleFlinkJobLogIterator}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LogRequestPayload
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.{FlinkJobLaunchErrorException, FlinkSavePointException}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.{FlinkJobInfo, LinkisJobInfo, LinkisJobLaunchManager}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.manager.SimpleFlinkJobLaunchManager.INSTANCE_NAME
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.operator.FlinkTriggerSavepointOperator
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state.{Checkpoint, Savepoint}
import org.apache.commons.lang.StringEscapeUtils
import org.apache.linkis.common.utils.{RetryHandler, Utils}
import org.apache.linkis.computation.client.once.simple.{SimpleOnceJob, SubmittableSimpleOnceJob}
import org.apache.linkis.computation.client.once.{OnceJob, SubmittableOnceJob}
import org.apache.linkis.computation.client.operator.impl.{EngineConnApplicationInfoOperator, EngineConnLogOperator}
import org.apache.linkis.httpclient.dws.DWSHttpClient
import org.apache.linkis.ujes.client.exception.UJESJobException

import java.net.URI
import java.util
import scala.collection.JavaConverters.mapAsScalaMapConverter

class SimpleFlinkJobLaunchManager extends FlinkJobLaunchManager {

  override def getName: String = INSTANCE_NAME

  protected def buildOnceJob(job: LaunchJob): SubmittableOnceJob = {
    val builder = SimpleOnceJob.builder().addExecuteUser(job.getSubmitUser).setLabels(job.getLabels)
      .setJobContent(job.getJobContent).setParams(job.getParams).setSource(job.getSource)
    if(job.getLaunchConfigs != null) {
      job.getLaunchConfigs.asScala.get(LaunchJob.LAUNCH_CONFIG_CREATE_SERVICE).foreach{ case createService: String => builder.setCreateService(createService)}
      job.getLaunchConfigs.asScala.get(LaunchJob.LAUNCH_CONFIG_DESCRIPTION).foreach{ case desc: String => builder.setDescription(desc)}
      job.getLaunchConfigs.asScala.get(LaunchJob.LAUNCH_CONFIG_MAX_SUBMIT_TIME).foreach{ case maxSubmitTime: Long => builder.setMaxSubmitTime(maxSubmitTime)}
    }
    builder.build()
  }

  override protected def createSubmittedOnceJob(id: String): OnceJob = SimpleOnceJob.build(id, onceJobIdToJobInfo.get(id).getUser)

  override protected def getStatus(id: String): String = getOnceJob(id) match {
    case simpleOnceJob: SimpleOnceJob =>
      if (simpleOnceJob.isCompleted) deleteOnceJob(id)
      simpleOnceJob.getStatus
  }

  override def getCheckpoints(jobInfo: LinkisJobInfo): Array[Checkpoint] = throw new FlinkJobLaunchErrorException(30401, "Not support method", null)

  /**
   * Trigger save point operator
   * @param jobInfo linkis job info
   */
  override def triggerSavepoint(jobInfo: LinkisJobInfo): Unit = {
    Utils.tryCatch{
      getOnceJob(jobInfo.getId).getOperator(FlinkTriggerSavepointOperator.OPERATOR_NAME) match{
        case savepointOperator: FlinkTriggerSavepointOperator => {
          // TODO Get scheme information from job info
          val savepointURI: URI = this.jobStateManager.getJobStateDir(classOf[Savepoint],
            JobLauncherConfiguration.FLINK_STATE_DEFAULT_SCHEME.getValue, JobLauncherConfiguration.FLINK_STATE_DEFAULT_AUTHORITY.getValue, jobInfo.getName)
          savepointOperator.setSavepointDir(savepointURI.toString)
          savepointOperator.setMode(JobLauncherConfiguration.FLINK_TRIGGER_SAVEPOINT_MODE.getValue)
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

  override protected def createJobInfo(onceJob: SubmittableOnceJob, job: LaunchJob): LinkisJobInfo = {
    val nodeInfo = onceJob.getNodeInfo
    val jobInfo = new FlinkJobInfo
    // Escape the job name
    jobInfo.setName(StringEscapeUtils.escapeJava(job.getJobName))
    jobInfo.setId(onceJob.getId)
    jobInfo.setUser(job.getSubmitUser)
    onceJob match {
      case simpleOnceJob: SubmittableSimpleOnceJob =>
        jobInfo.setECMInstance(simpleOnceJob.getECMServiceInstance)
      case _ =>
    }
    fetchApplicationInfo(jobInfo)
    jobInfo.setResources(nodeInfo.get("nodeResource").asInstanceOf[util.Map[String, Object]])
    jobInfo
  }

  override protected def createJobInfo(jobInfo: String): LinkisJobInfo = DWSHttpClient.jacksonJson.readValue(jobInfo, classOf[FlinkJobInfo])

  protected def fetchApplicationInfo(jobInfo: FlinkJobInfo): Unit = {
    getOnceJob(jobInfo.getId).getOperator(EngineConnApplicationInfoOperator.OPERATOR_NAME) match {
      case applicationInfoOperator: EngineConnApplicationInfoOperator =>
        val retryHandler = new RetryHandler {}
        retryHandler.setRetryNum(JobLauncherConfiguration.FLINK_FETCH_APPLICATION_INFO_MAX_TIMES.getValue)
        retryHandler.setRetryMaxPeriod(5000)
        retryHandler.setRetryPeriod(500)
        retryHandler.addRetryException(classOf[UJESJobException])
        val applicationInfo = retryHandler.retry(applicationInfoOperator(), "Fetch-Yarn-Application-Info")
        jobInfo.setApplicationId(applicationInfo.applicationId)
        jobInfo.setApplicationUrl(applicationInfo.applicationUrl)
    }
  }

  override def fetchLogs(id: String, requestPayload: LogRequestPayload): FlinkLogIterator = getOnceJob(id).getOperator(EngineConnLogOperator.OPERATOR_NAME) match {
    case engineConnLogOperator: EngineConnLogOperator =>
      val jobInfo = getJobInfo(id)
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
}
object SimpleFlinkJobLaunchManager{

  val INSTANCE_NAME = "simpleFlink";

}
