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
import java.util.Date
import java.util.concurrent.{Future, TimeUnit}
import com.google.common.collect.Sets
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.LinkisJobInfo
import com.webank.wedatasphere.streamis.jobmanager.manager.alert.{AlertLevel, Alerter}
import com.webank.wedatasphere.streamis.jobmanager.manager.conf.JobConf
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.{StreamJobMapper, StreamTaskMapper}
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.{StreamJob, StreamTask}

import javax.annotation.{PostConstruct, PreDestroy}
import org.apache.commons.lang.exception.ExceptionUtils
import org.apache.linkis.common.utils.{Logging, RetryHandler, Utils}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.convert.WrapAsScala._


@Service
class TaskMonitorService extends Logging {

  @Autowired private var streamTaskMapper:StreamTaskMapper=_
  @Autowired private var streamJobMapper:StreamJobMapper=_
  @Autowired private var jobService:JobService=_

  @Autowired private var alerters:Array[Alerter] = _


  private var future: Future[_] = _

  @PostConstruct
  def init(): Unit = {
    future = Utils.defaultScheduler.scheduleAtFixedRate(new Runnable {
      override def run(): Unit = Utils.tryAndWarnMsg {
        doMonitor()
      }("Monitor the status of all tasks failed!")
    }, JobConf.TASK_MONITOR_INTERVAL.getValue.toLong, JobConf.TASK_MONITOR_INTERVAL.getValue.toLong, TimeUnit.MILLISECONDS)
  }

  @PreDestroy
  def close(): Unit = {
    future.cancel(true)
  }

  def doMonitor(): Unit = {
    info("Try to update all StreamTasks status.")
    val status = util.Arrays.asList(JobConf.NOT_COMPLETED_STATUS_ARRAY.map(c => new Integer(c.getValue)) :_*)
    val streamTasks = streamTaskMapper.getTasksByStatus(status)
    if(streamTasks == null || streamTasks.isEmpty) {
      info("No StreamTasks is running, return...")
      return
    }
    streamTasks.filter(shouldMonitor).foreach { streamTask =>
      streamTask.setLastUpdateTime(new Date)
      streamTaskMapper.updateTask(streamTask)
      val job = streamJobMapper.getJobById(streamTask.getJobId)
      info(s"Try to update status of StreamJob-${job.getName}.")
      val retryHandler = new RetryHandler {}
      retryHandler.setRetryNum(3)
      retryHandler.setRetryMaxPeriod(2000)
      Utils.tryCatch {
        retryHandler.retry(DefaultStreamTaskService.updateStreamTaskStatus(streamTask, job.getName), s"Task-Monitor-${job.getName}")
      } { ex =>
          error(s"Fetch StreamJob-${job.getName} failed, maybe the Linkis cluster is wrong, please be noticed!", ex)
        val errorMsg = ExceptionUtils.getRootCauseMessage(ex)
        if(errorMsg != null && errorMsg.contains("Not exists EngineConn")) {
          streamTask.setStatus(JobConf.FLINK_JOB_STATUS_FAILED.getValue)
          streamTask.setErrDesc("Not exists EngineConn.")
        } else {
          // 连续三次还是出现异常，说明Linkis的Manager已经不能正常提供服务，告警并不再尝试获取状态，等待下次尝试
          val users = getAlertUsers(job)
          users.add(job.getCreateBy)
          alert(jobService.getAlertLevel(job), "请求LinkisManager失败，Linkis集群出现异常，请关注！", users ,streamTask)
          return
        }
      }
      streamTaskMapper.updateTask(streamTask)
      if(streamTask.getStatus == JobConf.FLINK_JOB_STATUS_FAILED.getValue) {
        warn(s"StreamJob-${job.getName} is failed, please be noticed.")
        // TODO Need to add restart feature if user sets the restart parameters in checkpoint module.
        val alertMsg = s"您的 streamis 流式应用[${job.getName}]已经失败, 请您确认该流式应用的状态是否正常"
        val userList = Sets.newHashSet(job.getCreateBy, job.getSubmitUser)
        userList.addAll(getAlertUsers(job))
        alert(jobService.getAlertLevel(job), alertMsg, new util.ArrayList[String](userList), streamTask)
      }
    }
    info("All StreamTasks status have updated.")
  }

  protected def getAlertUsers(job: StreamJob): util.List[String] = {
    var users = jobService.getAlertUsers(job)
    if (users == null) {
      users = util.Arrays.asList(JobConf.STREAMIS_DEVELOPER.getValue.split(","):_*)
    }else {
      users.addAll(util.Arrays.asList(JobConf.STREAMIS_DEVELOPER.getValue.split(","):_*))
    }
    users
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
