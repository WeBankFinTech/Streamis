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

import com.webank.wedatasphere.streamis.errorcode.handler.StreamisErrorCodeHandler
import com.webank.wedatasphere.streamis.jobmanager.launcher.enums.JobClientType
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.{JobClient, LaunchJob}
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobState
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.conf.JobLauncherConfiguration
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.conf.JobLauncherConfiguration.{VAR_FLINK_APP_NAME, VAR_FLINK_SAVEPOINT_PATH}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.FlinkJobLaunchErrorException
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.jobInfo.{EngineConnJobInfo, LinkisJobInfo}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client.factory.AbstractJobClientFactory
import org.apache.commons.lang3.StringEscapeUtils
import org.apache.linkis.common.utils.{Logging, Utils}
import org.apache.linkis.computation.client.once.{OnceJob, SubmittableOnceJob}
import org.apache.linkis.computation.client.utils.LabelKeyUtils
import org.apache.linkis.protocol.utils.TaskUtils

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.util.matching.Regex


trait FlinkJobLaunchManager extends LinkisJobLaunchManager with Logging {

  protected def buildOnceJob(job: LaunchJob): SubmittableOnceJob

  protected def createSubmittedOnceJob(id: String, jobInfo: LinkisJobInfo): OnceJob

  protected def createJobInfo(onceJob: SubmittableOnceJob, job: LaunchJob, jobState: JobState): LinkisJobInfo

  protected def createJobInfo(jobInfo: String): LinkisJobInfo

  protected var jobStateManager: JobStateManager = _



  /**
   * This method is used to launch a new job.
   *
   * @param job      a StreamisJob wanted to be launched.
   * @param jobState job state used to launch
   * @return the job id.
   */
  override def innerLaunch(job: LaunchJob, jobState: JobState): JobClient[LinkisJobInfo] = {
    // Transform the JobState(isRestore = true) into the params in LaunchJob
    Option(jobState).filter(jobState => jobState.isRestore).foreach(state => {
      val startUpParams = TaskUtils.getStartupMap(job.getParams)
      startUpParams.putIfAbsent(VAR_FLINK_SAVEPOINT_PATH.getValue,
        state.getLocation.toString)
    })
    TaskUtils.getStartupMap(job.getParams).put(VAR_FLINK_APP_NAME.getValue,
      Option(job.getJobName) match {
        case None => "EngineConn-Flink"
        case Some(jobName) =>
          val index = jobName.lastIndexOf(".")
          if (index > 0) jobName.substring(0, index) else jobName
    })
    job.getLabels.get(LabelKeyUtils.ENGINE_TYPE_LABEL_KEY) match {
      case engineConnType: String =>
        if(!engineConnType.toLowerCase.startsWith(FlinkJobLaunchManager.FLINK_ENGINE_CONN_TYPE))
          throw new FlinkJobLaunchErrorException(30401, s"Only ${FlinkJobLaunchManager.FLINK_ENGINE_CONN_TYPE} job is supported to be launched to Linkis, but $engineConnType is found.(不识别的引擎类型)", null)

      case _ => throw new FlinkJobLaunchErrorException(30401, s"Not exists ${LabelKeyUtils.ENGINE_TYPE_LABEL_KEY}(缺少引擎标签), StreamisJob cannot be submitted to Linkis successfully.", null)
    }
    Utils.tryCatch {
      val onceJob = buildOnceJob(job)
      onceJob.submit()
      val jobInfo: LinkisJobInfo = Utils.tryCatch(createJobInfo(onceJob, job, jobState)) {
        case e: FlinkJobLaunchErrorException =>
          throw e
        case t: Throwable =>
          error(s"${job.getSubmitUser} create jobInfo failed, now stop this EngineConn ${onceJob.getId}.")
          Utils.tryQuietly(onceJob.kill())
          var stopMsg = ""
          Utils.tryCatch {
            val tmpJobInfo = new EngineConnJobInfo
            tmpJobInfo.setName(StringEscapeUtils.escapeJava(job.getJobName))
            tmpJobInfo.setId(onceJob.getId)
            tmpJobInfo.setUser(job.getSubmitUser)
            val startupMap = TaskUtils.getStartupMap(job.getParams)
            val managerMode = startupMap.getOrDefault(JobLauncherConfiguration.MANAGER_MODE_KEY.getValue, JobClientType.ATTACH.getName).toString.toLowerCase()
            tmpJobInfo.setClientType(managerMode)
            AbstractJobClientFactory.getJobManager().createJobClient(onceJob, tmpJobInfo, getJobStateManager).stop()
          } {
            case e: Exception =>
              val msg = s"Failed to kill job with id : ${onceJob.getId}, because : ${e.getMessage}, please go to check the app in yarn(停止APP失败，请上yarn查看)"
              logger.error(msg)
              stopMsg = msg
          }
          throw new FlinkJobLaunchErrorException(-1, exceptionAnalyze(s"Fail to obtain launched job info(获取任务信息失败,引擎服务可能启动失败). ${stopMsg}", t), t)
      }
      val client = AbstractJobClientFactory.getJobManager().createJobClient(onceJob, jobInfo, getJobStateManager)
      client
    }{
      case e: FlinkJobLaunchErrorException => throw e
      case t: Throwable =>
        error(s"Server Exception in submitting Flink job [${job.getJobName}] to Linkis remote server", t)
        throw new FlinkJobLaunchErrorException(-1, exceptionAnalyze(s"Exception in submitting Flink job to Linkis remote server (提交至Linkis服务失败，请检查服务及网络)", t), t)
    }
  }

  override def launch(job: LaunchJob): JobClient[LinkisJobInfo] = {
    launch(job, null)
  }


  override def connect(id: String, jobInfo: String): JobClient[LinkisJobInfo] = {
    connect(id, createJobInfo(jobInfo))
  }


  override def connect(id: String, jobInfo: LinkisJobInfo): JobClient[LinkisJobInfo] = {
    AbstractJobClientFactory.getJobManager().createJobClient(createSubmittedOnceJob(id, jobInfo), jobInfo, getJobStateManager)
  }


  /**
   * Job state manager(store the state information, example: Checkpoint/Savepoint)
   *
   * @return state manager instance
   */
  override def getJobStateManager: JobStateManager = {
    Option(jobStateManager) match {
      case None =>
        this synchronized{
          // Flink job state manager
          jobStateManager = new FlinkJobStateManager
        }
        jobStateManager
      case Some(stateManager) => stateManager
    }
  }

  /**
   * Exception analyzer
   * @param errorMsg error message
   * @param t throwable
   * @return
   */
  def exceptionAnalyze(errorMsg: String, t: Throwable): String = {
    //    EXCEPTION_PATTERN.findFirstMatchIn(t.getMessage) match {
    //      case Some(m) =>
    //        errorMsg + s", 原因分析[${m.group(1)}]"
    //      case _ => errorMsg
    //    }
    if (null != t) {
      val errorCodes = StreamisErrorCodeHandler.getInstance().handle(t.getMessage)
      if (errorCodes != null && errorCodes.size() > 0) {
        errorCodes.asScala.map(e => e.getErrorDesc).mkString("。")
      } else {
        errorMsg
      }
    } else {
      errorMsg
    }

  }

}

object FlinkJobLaunchManager {
  val FLINK_ENGINE_CONN_TYPE = "flink"

  val EXCEPTION_PATTERN: Regex = "[\\s\\S]+,desc:([\\s\\S]+?),(ip|port|serviceKind)[\\s\\S]+$".r

}