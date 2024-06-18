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

import com.webank.wedatasphere.streamis.jobmanager.launcher.enums.JobClientType
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf.JobConf
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.constants.JobConstants
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.errorcode.JobLaunchErrorCode
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.{JobState, JobStateInfo}
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.{JobClient, JobInfo, LaunchJob}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.conf.JobLauncherConfiguration
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.StreamisJobLaunchException
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.manager.SimpleFlinkJobLaunchManager.INSTANCE_NAME
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.jobInfo.LinkisJobInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.jobInfo.EngineConnJobInfo
import org.apache.commons.lang3.{StringEscapeUtils, StringUtils}
import org.apache.linkis.common.ServiceInstance
import org.apache.linkis.common.exception.LinkisRetryException
import org.apache.linkis.common.utils.{JsonUtils, RetryHandler, Utils}
import org.apache.linkis.computation.client.once.action.GetEngineConnAction
import org.apache.linkis.computation.client.once.simple.{SimpleOnceJob, SimpleOnceJobBuilder, SubmittableSimpleOnceJob}
import org.apache.linkis.computation.client.once.{OnceJob, SubmittableOnceJob}
import org.apache.linkis.computation.client.operator.impl.EngineConnApplicationInfoOperator
import org.apache.linkis.governance.common.constant.ec.ECConstants
import org.apache.linkis.httpclient.dws.DWSHttpClient
import org.apache.linkis.manager.common.entity.enumeration.NodeStatus
import org.apache.linkis.protocol.utils.TaskUtils
import org.apache.linkis.ujes.client.exception.UJESJobException

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
      job.getLaunchConfigs.asScala.get(LaunchJob.LAUNCH_CONFIG_MAX_SUBMIT_TIME).foreach{ case maxSubmitTime: String => builder.setMaxSubmitTime(maxSubmitTime.toLong)}
    }
    builder.build()
  }

  override protected def createSubmittedOnceJob(id: String, jobInfo: LinkisJobInfo): OnceJob = SimpleOnceJob.build(id, jobInfo.getUser)


  override protected def createJobInfo(onceJob: SubmittableOnceJob, job: LaunchJob, jobState: JobState): LinkisJobInfo = {
    val nodeInfo = onceJob.getNodeInfo
    val jobInfo = new EngineConnJobInfo
    // Escape the job name
    jobInfo.setName(StringEscapeUtils.escapeJava(job.getJobName))
    jobInfo.setId(onceJob.getId)
    jobInfo.setUser(job.getSubmitUser)
    jobInfo.setHighAvailablePolicy(job.getLaunchConfigs.get(JobConf.HIGHAVAILABLE_POLICY_KEY.getHotValue()).toString)
    onceJob match {
      case submittableSimpleOnceJob: SubmittableSimpleOnceJob =>
        jobInfo.setEcInstance(submittableSimpleOnceJob.getEcServiceInstance)
        jobInfo.setECMInstance(submittableSimpleOnceJob.getECMServiceInstance)
      case _ =>
        val typeStr = if (null == onceJob) "null" else onceJob.getClass.getName
        logger.error(s"Invalid job type : ${typeStr}, only SubmittableSimpleOnceJob is supported")
    }
    val startupMap = TaskUtils.getStartupMap(job.getParams)
    val managerMode = startupMap.getOrDefault(JobLauncherConfiguration.MANAGER_MODE_KEY.getValue, JobClientType.ATTACH.getName).toString.toLowerCase()
    jobInfo.setClientType(managerMode)
    logger.info(s"Job manager mode : ${managerMode}")
    Utils.tryCatch(fetchApplicationInfo(onceJob, jobInfo)) { t =>
      val message = s"Unable to fetch the application info of launched job [${job.getJobName}], maybe the engine has been shutdown"
      logger.error(message, t)
      // Mark failed
      jobInfo.setStatus("failed")
      jobInfo.setCompletedMsg(exceptionAnalyze(message,t))
      // kill ec
      Utils.tryAndWarn {
        logger.error(s"Will kill failed ec : ${jobInfo.getEcInstance().toString()} with appid : ${jobInfo.getApplicationId}, url : ${jobInfo.getApplicationUrl}")
        onceJob.kill()
      }
    }
    jobInfo.setJobParams(job.getParams.asInstanceOf[util.Map[String, Object]])
    jobInfo.setResources(nodeInfo.get("nodeResource").asInstanceOf[util.Map[String, Object]])
    // Set job state info into
    Option(jobState).foreach(state => {
      val stateInfo = new JobStateInfo(state.getLocation.toString, state.getTimestamp, state.isRestore)
      jobInfo.setJobStates(Array(stateInfo))
    })
    jobInfo
  }

  override protected def createJobInfo(jobInfo: String): LinkisJobInfo = {
    if (StringUtils.isNotBlank(jobInfo)) {
      DWSHttpClient.jacksonJson.readValue(jobInfo, classOf[EngineConnJobInfo])
    } else {
      null
    }
  }

  protected def fetchApplicationInfo(onceJob: OnceJob, jobInfo: EngineConnJobInfo): Unit = {
    val isDetach = JobClientType.DETACH.toString.equalsIgnoreCase(jobInfo.getClientType)
    if (isDetach) {
      val retryHandler = new RetryHandler {}
      retryHandler.setRetryNum(JobLauncherConfiguration.FLINK_FETCH_APPLICATION_INFO_MAX_TIMES.getValue)
      retryHandler.setRetryMaxPeriod(5000)
      retryHandler.setRetryPeriod(500)
      retryHandler.addRetryException(classOf[UJESJobException])
      retryHandler.addRetryException(classOf[LinkisRetryException])
      retryHandler.addRetryException(classOf[NullPointerException])
      var ecInstance: ServiceInstance = null
      var ecTicketId: String = null
      onceJob match {
        case submittableSimpleOnceJob: SubmittableSimpleOnceJob=>
          ecInstance = submittableSimpleOnceJob.getEcServiceInstance
          ecTicketId = submittableSimpleOnceJob.getEcTicketId
        case _ =>
      }
      retryHandler.retry(getEcMetrics(ecTicketId, ecInstance, jobInfo), "GetEcMetrics")
    } else {
    onceJob.getOperator(EngineConnApplicationInfoOperator.OPERATOR_NAME) match {
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

  }


  def getEcMetrics(ecTickecId: String, ecInstance: ServiceInstance, jobInfo: EngineConnJobInfo): Unit = {
    val getEngingConnAction = new GetEngineConnAction
    getEngingConnAction.setUser("hadoop")
    if (StringUtils.isNotBlank(ecTickecId)) {
      getEngingConnAction.addRequestPayload(JobConstants.EC_TICKET_ID_KEY, ecTickecId)
    }
    if (null != ecInstance) {
      getEngingConnAction.addRequestPayload(JobConstants.APP_NAME_KEY, ecInstance.getApplicationName)
      getEngingConnAction.addRequestPayload(JobConstants.INSTANCE_KEY, ecInstance.getInstance)
    }
    val rs = SimpleOnceJobBuilder.getLinkisManagerClient.getEngineConn(getEngingConnAction)
    if (JobLauncherConfiguration.ENABLE_STATUS_ON_FETCH_METRICS.getHotValue()) {
      val engine = rs.getData.getOrDefault(JobConstants.RESULT_EC_ENGINE_KEY, new util.HashMap[String, AnyRef]).asInstanceOf[util.Map[String, AnyRef]]
      if (!engine.isEmpty) {
        val status = engine.getOrDefault(ECConstants.NODE_STATUS_KEY, "").toString
        logger.info(s"Got ec : ${ecInstance.toString()} status : ${status}")
        if (StringUtils.isNotBlank(status)) {
          if (NodeStatus.isCompleted(NodeStatus.toNodeStatus(status))) {
            val msg = s"Ec : ${ecInstance.toString()} has completed with status : ${status}"
            logger.error(msg)
            throw new StreamisJobLaunchException(JobLaunchErrorCode.JOB_EC_ERROR_CODE, msg, null)
          }
        } else {
          logger.error(s"Got null status for ec : ${ecInstance.toString()}")
        }
      } else {
        logger.error(s"Got null ec info for ec : ${ecInstance.toString()}")
      }
    }

    val metricsStr = rs.getData.getOrDefault(JobConstants.RESULT_EC_METRICS_KEY, "{}")
    val metricsMap = if (null != metricsStr) {
      JsonUtils.jackson.readValue(metricsStr.toString, classOf[util.Map[String, AnyRef]])
    } else {
      logger.warn("metrics: \n" + JsonUtils.jackson.writeValueAsString(rs))
      throw new LinkisRetryException(JobLaunchErrorCode.JOB_EC_METRICS_ERROR, "Got null metrics.")
    }
    val applicationId = if (metricsMap.containsKey(ECConstants.YARN_APPID_NAME_KEY)) {
      metricsMap.get(ECConstants.YARN_APPID_NAME_KEY)
    } else {
      logger.warn("metrics: \n" + JsonUtils.jackson.writeValueAsString(rs))
      throw new LinkisRetryException(JobLaunchErrorCode.JOB_EC_METRICS_ERROR, "Got no appId.")
    }
    jobInfo.setApplicationId(applicationId.toString)
    jobInfo.setApplicationUrl(metricsMap.getOrDefault(ECConstants.YARN_APP_URL_KEY, "").toString)
  }


  /**
   * Init method
   */
  override def init(): Unit = {
    // Init the job state manager
     getJobStateManager.init()
  }


  /**
   * Destroy method
   */
  override def destroy(): Unit = {
    // Destroy the job state manager
    getJobStateManager.destroy()
  }
}
object SimpleFlinkJobLaunchManager{

  val INSTANCE_NAME = "flink";
}
