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

import com.google.common.collect.{Lists, Sets}
import com.webank.wedatasphere.linkis.common.utils.{Logging, Utils}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.LinkisJobManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LinkisJobInfo
import com.webank.wedatasphere.streamis.jobmanager.manager.alert.{AlertLevel, Alerter}
import com.webank.wedatasphere.streamis.jobmanager.manager.conf.JobConf
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.{StreamJobMapper, StreamTaskMapper}
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamTask
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.convert.WrapAsScala._


@Service
class TaskMonitorService extends Logging {

  @Autowired private var streamTaskMapper:StreamTaskMapper=_
  @Autowired private var streamJobMapper:StreamJobMapper=_

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

  def doMonitor(): Unit = {
    info("Try to update all StreamTasks status.")
    val status = util.Arrays.asList(JobConf.NOT_COMPLETED_STATUS_ARRAY.map(c => new Integer(c.getValue)) :_*)
    val streamTasks = streamTaskMapper.getTasksByStatus(status)
    if(streamTasks == null || streamTasks.isEmpty) {
      info("No StreamTasks is running, return...")
      return
    }
    streamTasks.filter(shouldMonitor).foreach { streamTask =>
      val job = streamJobMapper.getJobById(streamTask.getJobId)
      info(s"Try to update status of StreamJob-${job.getName}.")
      try {
        val jobInfo = LinkisJobManager.getLinkisJobManager.getJobInfo(streamTask.getLinkisJobId, streamTask.getSubmitUser)
      } catch {
        case ex: Exception => {
          // todo 如果有异常  应该再请求N次 如果都是异常 则不再重试 表示已经死亡
          Utils.tryCatch{
            val s = "您的streamis流式应用[%s]进程不存在,请您检查该流式应用的状况".format(job.getName)
            //todo 先告警给createby 和 updateby 和子系统负责人
            val set = Sets.newHashSet(job.getCreateBy, job.getSubmitUser, "cooperyang")
            alerters.foreach(alerter => alerter.alert(AlertLevel.CRITICAL, s, Lists.newArrayList(set)))
          }{
            t => logger.error("failed to send ims alert", t)
              //todo 告警失败一定要再告警
          }
          error(s"do not have this job StreamJob-${job.getName}. maybe run failed", ex)
          streamTask.setLastUpdateTime(new Date())
          streamTask.setErrDesc(ex.getMessage)
          streamTask.setStatus(JobConf.JOBMANAGER_FLINK_JOB_STATUS_SIX.getValue)
          streamTaskMapper.updateTask(streamTask)
          return
        }
      }
      TaskService.updateStreamTaskStatus(streamTask, job.getName, streamTask.getSubmitUser)
      streamTaskMapper.updateTask(streamTask)
    }
    info("All StreamTasks status have updated.")
  }

  protected def shouldMonitor(streamTask: StreamTask): Boolean =
    System.currentTimeMillis - streamTask.getLastUpdateTime.getTime >= JobConf.TASK_MONITOR_INTERVAL.getValue.toLong

  protected def getStatus(jobInfo: LinkisJobInfo): Int = {
    //TODO We should use jobInfo to get more accurate status, such as Alert running, Slow running
    JobConf.linkisStatusToStreamisStatus(jobInfo.getStatus)
  }

}
