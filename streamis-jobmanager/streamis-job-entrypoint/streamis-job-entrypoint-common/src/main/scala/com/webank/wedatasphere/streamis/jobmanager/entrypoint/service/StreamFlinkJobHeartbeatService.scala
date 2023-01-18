package com.webank.wedatasphere.streamis.jobmanager.entrypoint.service

import com.webank.wedatasphere.streamis.jobmanager.entrypoint.producer.StreamJobHeartbeatProducer
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.sender.StreamJobHeartbeatSender
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.utils.RetryUtil
import org.apache.linkis.common.conf.CommonVars

import java.util.concurrent.{Callable, ThreadPoolExecutor}

class StreamFlinkJobHeartbeatService {

  val RETRY_TIMES: CommonVars[Int] = CommonVars("wds.streamis.job.heartbeat.retry.times", "3")

  val RETRY_INTERVAL: CommonVars[Long] = CommonVars("wds.streamis.job.heartbeat.retry.interval", "1000L")

  val EXPONENTIAL: CommonVars[Boolean] = CommonVars("wds.streamis.job.heartbeat.retry.exponential", "true")

  val HEARTBEAT_INTERVAL_TIME: CommonVars[Long] = CommonVars("wds.streamis.job.heartbeat.interval.time", "30")

  var asyncExecutor: ThreadPoolExecutor = null

  def init(): Unit = {
    asyncExecutor = RetryUtil.createThreadPoolExecutor()
  }

  def create(producer: StreamJobHeartbeatProducer, sender: StreamJobHeartbeatSender): Unit = {


  }

  /**
   * Start a heartbeat
   * @param thread
   */
  def start(thread: Callable[String]): Unit = {
    RetryUtil.asyncExecuteWithRetry(thread,
      RETRY_TIMES.value, RETRY_INTERVAL.value, EXPONENTIAL.value, HEARTBEAT_INTERVAL_TIME.value, asyncExecutor)

    // Add shutdown hook to destroy the heartbeat
    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
      override def run(): Unit = {

      }
    }))
  }
}
