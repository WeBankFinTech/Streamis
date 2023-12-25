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

package com.webank.wedatasphere.streamis.jobmanager.manager.service

import java.util
import java.util.{Date, Optional, function}
import java.util.concurrent.{CompletableFuture, ExecutorService, Future, TimeUnit}
import com.webank.wedatasphere.streamis.jobmanager.launcher.JobLauncherAutoConfiguration
import com.webank.wedatasphere.streamis.jobmanager.launcher.conf.JobConfKeyConstants
import com.webank.wedatasphere.streamis.jobmanager.launcher.dao.StreamJobConfMapper
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.JobInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf.JobConf
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobLaunchManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.jobInfo.{EngineConnJobInfo, LinkisJobInfo}
import com.webank.wedatasphere.streamis.jobmanager.manager.alert.{AlertLevel, Alerter}
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.{StreamJobMapper, StreamTaskMapper}
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.{StreamJob, StreamTask}
import com.webank.wedatasphere.streamis.jobmanager.manager.utils.StreamTaskUtils
import com.webank.wedatasphere.streamis.errorcode.handler.StreamisErrorCodeHandler
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.utils.ServerUtils
import com.webank.wedatasphere.streamis.jobmanager.manager.constrants.JobConstrants

import javax.annotation.{PostConstruct, PreDestroy, Resource}
import org.apache.commons.lang.exception.ExceptionUtils
import org.apache.commons.lang3.StringUtils
import org.apache.linkis.common.exception.ErrorException
import org.apache.linkis.common.utils.{Logging, RetryHandler, Utils}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.convert.WrapAsScala._


@Service
class TaskMonitorService extends Logging {

  @Autowired private var streamTaskMapper:StreamTaskMapper=_
  @Autowired private var streamJobMapper:StreamJobMapper=_
  @Autowired private var jobService: StreamJobService =_

  @Autowired private var alerters:Array[Alerter] = _

  @Resource
  private var streamTaskService: StreamTaskService = _

  @Resource
  private var streamJobConfMapper: StreamJobConfMapper = _

  private var future: Future[_] = _

  private val errorCodeHandler = StreamisErrorCodeHandler.getInstance()

  @PostConstruct
  def init(): Unit = {
    if (JobConf.STREAMIS_JOB_MONITOR_ENABLE.getValue) {
      future = Utils.defaultScheduler.scheduleAtFixedRate(new Runnable {
        override def run(): Unit = Utils.tryAndWarnMsg {
          doMonitor()
        }("Monitor the status of all tasks failed!")
      }, JobConf.TASK_MONITOR_INTERVAL.getValue.toLong, JobConf.TASK_MONITOR_INTERVAL.getValue.toLong, TimeUnit.MILLISECONDS)
    }
    if (JobConf.STREAMIS_JOB_RESET_ON_START_ENABLE.getHotValue()) {
      Utils.defaultScheduler.submit(new Runnable {
        override def run(): Unit = Utils.tryAndError {
          Thread.sleep(JobConstrants.JOB_RESET_ON_RESTART_WAIT_MILLS)
          logger.info("Start to clean halt jobs started in on day after server started.")
          val statusList = new util.ArrayList[Integer]()
          statusList.add(JobConf.FLINK_JOB_STATUS_STARTING.getValue)
          val thisServerInstance = ServerUtils.thisServiceInstance
          val streamTasks = streamTaskMapper.getTasksByStatus(statusList).filter(_.getServerInstance.equalsIgnoreCase(thisServerInstance))
          if (null != streamTasks && !streamTasks.isEmpty) {
            logger.info(s"There are ${streamTasks.size} starting tasks to be killed.")
            val jobLaunchManager = JobLaunchManager.getJobManager(JobLauncherAutoConfiguration.DEFAULT_JOB_LAUNCH_MANGER)
            streamTasks.foreach(task => {
              val tmpTask = new StreamTask()
              tmpTask.setId(task.getId)
              val jobClient = jobLaunchManager.connect(task.getLinkisJobId, task.getLinkisJobInfo)
              Utils.tryAndWarn(jobClient.stop())
              tmpTask.setStatus(JobConf.FLINK_JOB_STATUS_FAILED.getValue)
              val msg = s"Task ${task.getId} of job id : ${task.getJobId} in server : ${task.getServerInstance} was killed after streamis server restarted."
              logger.warn(msg)
              tmpTask.setErrDesc(msg)
              streamTaskMapper.updateTask(tmpTask)
            })
          }
        }
      })
    }
  }

  @PreDestroy
  def close(): Unit = {
    Option(future).foreach(_.cancel(true))
  }

  def doMonitor(): Unit = {
    info("Try to update all StreamTasks status.")
    val jobLaunchManager = JobLaunchManager.getJobManager(JobLauncherAutoConfiguration.DEFAULT_JOB_LAUNCH_MANGER)
    val status = util.Arrays.asList(JobConf.NOT_COMPLETED_STATUS_ARRAY.map(c => new Integer(c.getValue)) :_*)
    val streamTasks = streamTaskMapper.getTasksByStatus(status)
    if(streamTasks == null || streamTasks.isEmpty) {
      info("No StreamTasks is running, return...")
      return
    }
    streamTasks.filter(shouldMonitor).foreach { streamTask =>
      val job = streamJobMapper.getJobById(streamTask.getJobId)
      if(!JobConf.SUPPORTED_MANAGEMENT_JOB_TYPES.getValue.contains(job.getJobType)) {
        val userList = getAlertUsers(job)
        //user
        val alertMsg = s"Spark Streaming应用[${job.getName}]已经超过 ${Utils.msDurationToString(System.currentTimeMillis - streamTask.getLastUpdateTime.getTime)} 没有更新状态, 请及时确认应用是否正常！"
        alert(jobService.getAlertLevel(job), alertMsg, userList, streamTask)
      } else {
        streamTask.setLastUpdateTime(new Date)
        streamTaskMapper.updateTask(streamTask)
        info(s"Try to update status of StreamJob-${job.getName}.")
        val retryHandler = new RetryHandler {}
        retryHandler.setRetryNum(3)
        retryHandler.setRetryMaxPeriod(2000)
        retryHandler.addRetryException(classOf[ErrorException])
        var jobInfo:JobInfo = null
        Utils.tryCatch {
          jobInfo = retryHandler.retry(refresh(streamTask, jobLaunchManager), s"Task-Monitor-${job.getName}")
        } { ex =>
          error(s"Fetch StreamJob-${job.getName} failed, maybe the Linkis cluster is wrong, please be noticed!", ex)
          val errorMsg = ExceptionUtils.getRootCauseMessage(ex)
          if (errorMsg != null && errorMsg.contains("Not exists EngineConn")) {
            streamTask.setStatus(JobConf.FLINK_JOB_STATUS_FAILED.getValue)
            streamTask.setErrDesc("Not exists EngineConn.")
          } else {
            // 连续三次还是出现异常，说明Linkis的Manager已经不能正常提供服务，告警并不再尝试获取状态，等待下次尝试
            val users = getAdminAlertUsers()
            alert(jobService.getLinkisFlinkAlertLevel(job), s"请求LinkisManager失败，Linkis集群出现异常，请关注！影响任务[${job.getName}]", users, streamTask)
          }
        }
        streamTaskMapper.updateTask(streamTask)
        if(streamTask.getStatus == JobConf.FLINK_JOB_STATUS_FAILED.getValue) {
          warn(s"StreamJob-${job.getName} is failed, please be noticed.")
          var extraMessage = ""
          Option(jobInfo) match {
            case Some(flinkJobInfo: EngineConnJobInfo) =>
              extraMessage = s",${flinkJobInfo.getApplicationId}"
            case _ =>
          }
          // Need to add restart feature if user sets the restart parameters.
          var alertMsg = s"Streamis 流式应用[${job.getName}${extraMessage}]已经失败, 请登陆Streamis查看应用日志."
          streamTask.setErrDesc("原因分析中，请稍后重试"+streamTask.getErrDesc)
          streamTaskMapper.updateTask(streamTask)
          val result: Future[_] = streamTaskService.errorCodeMatching(job.getId,streamTask)
          var highAvailablePolicy = this.streamJobConfMapper.getRawConfValue(job.getId, JobConf.HIGHAVAILABLE_POLICY_KEY.getValue)
          highAvailablePolicy = Optional.ofNullable(highAvailablePolicy).orElse(JobConf.HIGHAVAILABLE_DEFAULT_POLICY.getHotValue)
          this.streamJobConfMapper.getRawConfValue(job.getId, JobConfKeyConstants.FAIL_RESTART_SWITCH.getValue) match {
            case "ON" =>
              alertMsg = s"${alertMsg} 现将自动拉起该应用"
              restartJob(job)
            case _ =>
              if (JobConf.AUTO_RESTART_JOB.getHotValue()) {
                if (!highAvailablePolicy.equals(JobConf.HIGHAVAILABLE_POLICY_SINGLE_BAK.getValue) && !highAvailablePolicy.equals(JobConf.HIGHAVAILABLE_DEFAULT_POLICY.getValue)) {
                  alertMsg = s"${alertMsg} 现将自动拉起该应用"
                  restartJob(job)
                }
              }
          }
          val userList = getAlertUsers(job)
          alert(jobService.getAlertLevel(job), alertMsg, userList, streamTask)
        }
      }
    }
    info("All StreamTasks status have updated.")
  }

  protected def restartJob(job: StreamJob): Unit = {
    Utils.tryCatch {
      info(s"Start to reLaunch the StreamisJob [${job.getName}], now to submit and schedule it...")
      // Use submit user to start job
      val startAutoRestoreSwitch = "ON".equals(this.streamJobConfMapper.getRawConfValue(job.getId, JobConfKeyConstants.START_AUTO_RESTORE_SWITCH.getValue))
      val future = streamTaskService.asyncExecute(job.getId, 0L, job.getSubmitUser, startAutoRestoreSwitch)
    } {
      case e: Exception =>
        warn(s"Fail to reLaunch the StreamisJob [${job.getName}]", e)
    }
  }
  /**
   * Refresh streamis task
   * @param streamTask stream task
   * @param jobLaunchManager launch manager
   */
  protected def refresh(streamTask: StreamTask, jobLaunchManager: JobLaunchManager[_ <: JobInfo]): JobInfo ={
    val jobClient = jobLaunchManager.connect(streamTask.getLinkisJobId, streamTask.getLinkisJobInfo)
    StreamTaskUtils.refreshInfo(streamTask, jobClient.getJobInfo(true))
    jobClient.getJobInfo
  }

  protected def getAlertUsers(job: StreamJob): util.List[String] = {
    val allUsers = new util.LinkedHashSet[String]()
    val alertUsers = jobService.getAlertUsers(job)
    var isValid = false
    if (alertUsers!= null) {
      alertUsers.foreach(user => {
        if (StringUtils.isNotBlank(user) && !user.toLowerCase().contains("hduser")) {
          isValid = true
          allUsers.add(user)
        }
      })
      if (!allUsers.contains(job.getSubmitUser)) {
        allUsers.add(job.getSubmitUser)
      }
    }
    if (!isValid){
      allUsers.add(job.getSubmitUser)
      allUsers.add(job.getCreateBy)
    }
    new util.ArrayList[String](allUsers)
  }

  protected def getAdminAlertUsers(): util.List[String] = {
    val allUsers = new util.LinkedHashSet[String]()
    util.Arrays.asList(JobConf.STREAMIS_DEVELOPER.getHotValue().split(","):_*).foreach(user => {
      allUsers.add(user)
    })
    new util.ArrayList[String](allUsers)
  }

  protected def alert(alertLevel: AlertLevel, alertMsg: String, users: util.List[String], streamTask:StreamTask): Unit = alerters.foreach{ alerter =>
    Utils.tryCatch {
      alerter.alert(alertLevel, alertMsg, users, streamTask)
    }(t => error(s"failed to send alert message to ${alerter.getClass.getSimpleName}.", t))
  }

  protected def shouldMonitor(streamTask: StreamTask): Boolean =
    System.currentTimeMillis - streamTask.getLastUpdateTime.getTime >= JobConf.TASK_MONITOR_INTERVAL.getValue.toLong

  protected def getStatus(jobInfo: LinkisJobInfo): Int = {
    //TODO We should use jobInfo to get more accurate status, such as Alert running, Slow running
    JobConf.linkisStatusToStreamisStatus(jobInfo.getStatus)
  }

}
