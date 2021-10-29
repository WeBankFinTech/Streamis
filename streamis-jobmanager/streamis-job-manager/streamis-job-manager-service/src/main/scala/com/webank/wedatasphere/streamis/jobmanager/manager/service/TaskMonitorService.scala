package com.webank.wedatasphere.streamis.jobmanager.manager.service

import java.util
import java.util.concurrent.{Future, TimeUnit}

import com.webank.wedatasphere.linkis.common.utils.{Logging, Utils}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.LinkisJobManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LinkisJobInfo
import com.webank.wedatasphere.streamis.jobmanager.manager.conf.JobConf
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.{StreamJobMapper, StreamTaskMapper}
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamTask
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.convert.WrapAsScala._

/**
 *
 * @date 2021-06-06
 * @author enjoyyin
 * @since 0.5.0
 */
@Service
class TaskMonitorService extends Logging {

  @Autowired private var streamTaskMapper:StreamTaskMapper=_
  @Autowired private var streamJobMapper:StreamJobMapper=_

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
      val jobInfo = LinkisJobManager.getLinkisJobManager.getJobInfo(streamTask.getLinkisJobId, streamTask.getSubmitUser)
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
