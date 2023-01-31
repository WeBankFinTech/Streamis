package com.webank.wedatasphere.streamis.jobmanager.entrypoint.service

import com.webank.wedatasphere.streamis.jobmanager.entrypoint.message.JobHeartbeatMessage
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.sender.StreamJobHeartbeatSender
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.utils.{HttpClientUtil, RetryUtil}
import org.apache.linkis.common.conf.{CommonVars, TimeType}
import org.apache.linkis.common.utils.Utils
import org.apache.linkis.common.utils.Logging

import java.util.concurrent.{Callable, Future, ThreadPoolExecutor, TimeUnit}

class StreamJobHeartbeatService extends Logging {

  val RETRY_TIMES: CommonVars[Int] = CommonVars("wds.streamis.job.heartbeat.retry.times", 3)

  val RETRY_INTERVAL: CommonVars[Long] = CommonVars("wds.streamis.job.heartbeat.retry.interval", 1000L)

  val EXPONENTIAL: CommonVars[Boolean] = CommonVars("wds.streamis.job.heartbeat.retry.exponential", true)

  val HEARTBEAT_INTERVAL_TIME: CommonVars[Long] = CommonVars("wds.streamis.job.heartbeat.interval.time", 30)

  val JOB_HEARTBEAT_INTERVAL: CommonVars[TimeType] = CommonVars("wds.streamis.task.monitor.interval", new TimeType("1m"))

  var asyncExecutor: ThreadPoolExecutor = null

  def init(): Unit = {
    asyncExecutor = RetryUtil.createThreadPoolExecutor()
  }

  /**
   * Start job heartbeat
   * @param thread
   */
  def start(message: JobHeartbeatMessage, sender: StreamJobHeartbeatSender): Unit = {
    Utils.defaultScheduler.scheduleAtFixedRate(new Runnable {
      override def run(): Unit = Utils.tryAndWarnMsg {
        RetryUtil.asyncExecuteWithRetry(new Callable[String] {
          override def call(): String = {
            print("Call send method")
            sender.send(message)
          }
        }, RETRY_TIMES.value, RETRY_INTERVAL.value, EXPONENTIAL.value, HEARTBEAT_INTERVAL_TIME.value, asyncExecutor)
      }("Send job heartbeat failed!")
    }, JOB_HEARTBEAT_INTERVAL.getValue.toLong, JOB_HEARTBEAT_INTERVAL.getValue.toLong, TimeUnit.MILLISECONDS)

//    while (true) {
//      Thread.sleep(1000)
//    }
    // Add shutdown hook to destroy the heartbeat
    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
      override def run(): Unit = {
        logger.info("Cancel sending heartbeat information, current message is: ",
          message.getStreamJobConfig.toString)
      }
    }))
  }
}
