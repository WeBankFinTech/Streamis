package com.webank.wedatasphere.streamis.jobmanager.entrypoint.service

import com.webank.wedatasphere.streamis.jobmanager.entrypoint.producer.{FlinkStreamJobHeartbeatProducer, SparkStreamJobHeartbeatProducer, StreamJobHeartbeatProducer}
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.sender.StreamJobHeartbeatSender
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.utils.RetryUtil
import org.apache.linkis.common.conf.{CommonVars, TimeType}
import org.apache.linkis.common.utils.Utils
import org.apache.linkis.common.utils.Logging

import java.util.concurrent.{Callable, Future, ThreadPoolExecutor, TimeUnit}

class StreamFlinkJobHeartbeatService extends Logging {

  val RETRY_TIMES: CommonVars[Int] = CommonVars("wds.streamis.job.heartbeat.retry.times", "3")

  val RETRY_INTERVAL: CommonVars[Long] = CommonVars("wds.streamis.job.heartbeat.retry.interval", "1000L")

  val EXPONENTIAL: CommonVars[Boolean] = CommonVars("wds.streamis.job.heartbeat.retry.exponential", "true")

  val HEARTBEAT_INTERVAL_TIME: CommonVars[Long] = CommonVars("wds.streamis.job.heartbeat.interval.time", "30")

  var asyncExecutor: ThreadPoolExecutor = null

  private var future: Future[_] = _

  val JOB_HEARTBEAT_INTERVAL: CommonVars[TimeType] = CommonVars("wds.streamis.task.monitor.interval", new TimeType("1m"))

  def init(): Unit = {
    asyncExecutor = RetryUtil.createThreadPoolExecutor()
  }

  def create(configMap: java.util.Map[String, Object]): Unit = {
    // Create producer and sender
    var streamJobHeartbeatProducer: StreamJobHeartbeatProducer = null
    if (configMap.containsKey("applicationUrl") && configMap.containsKey("jobId")) {
      streamJobHeartbeatProducer = new FlinkStreamJobHeartbeatProducer
    } else {
      streamJobHeartbeatProducer = new SparkStreamJobHeartbeatProducer
    }



  }

  /**
   * Start a heartbeat
   * @param thread
   */
  def start(producer: StreamJobHeartbeatProducer, sender: StreamJobHeartbeatSender): Unit = {
    Utils.defaultScheduler.scheduleAtFixedRate(new Runnable {
      override def run(): Unit = Utils.tryAndWarnMsg {
        RetryUtil.asyncExecuteWithRetry(new Callable[String] {
          override def call(): String = {
            sender.send()
          }
        }, RETRY_TIMES.value, RETRY_INTERVAL.value, EXPONENTIAL.value, HEARTBEAT_INTERVAL_TIME.value, asyncExecutor)
      }("Send job heartbeat failed!")
    }, JOB_HEARTBEAT_INTERVAL.getValue.toLong, JOB_HEARTBEAT_INTERVAL.getValue.toLong, TimeUnit.MILLISECONDS)

    // Add shutdown hook to destroy the heartbeat
    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
      override def run(): Unit = {

      }
    }))
  }
}
