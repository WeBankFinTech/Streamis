package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.core

import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.conf.JobLauncherConfiguration

import java.io.Closeable
import java.util
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LogRequestPayload
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.operator.FlinkYarnLogOperator
import org.apache.linkis.common.utils.{Logging, Utils}
import org.apache.linkis.computation.client.operator.impl.{EngineConnLogOperator, EngineConnLogs}

/**
 *
 * @date 2021-11-10
 * @author enjoyyin
 * @since 0.5.0
 */
trait FlinkLogIterator extends Iterator[String] with Closeable {
  val requestPayload: LogRequestPayload
  val engineConnLogOperator: EngineConnLogOperator
  def init(): Unit
  def getLogPath: String
  def getLogDirSuffix: String
  def getLogs: util.ArrayList[String]
  def getEndLine: Long
}

class SimpleFlinkJobLogIterator(override val requestPayload: LogRequestPayload,
                                override val engineConnLogOperator: EngineConnLogOperator) extends FlinkLogIterator with Logging {

  private var logs: util.ArrayList[String] = _
  private var index = 0
  private var logPath: String = _
  private var logDirSuffix: String = _
  private var isClosed = true
  private var endLine = 0

  override def init(): Unit = {
    engineConnLogOperator.setPageSize(requestPayload.getPageSize)
    engineConnLogOperator.setFromLine(requestPayload.getFromLine)
    engineConnLogOperator.setIgnoreKeywords(requestPayload.getIgnoreKeywords)
    engineConnLogOperator.setOnlyKeywords(requestPayload.getOnlyKeywords)
    engineConnLogOperator.setLastRows(requestPayload.getLastRows)
    var engineConnLog = EngineConnLogs(null,
      new util.ArrayList[String](
          util.Arrays.asList("yarn log is disabled, please contact flink operator to see logs(yarn日志已关闭，请联系Flink运维查看日志).")),
      1)
    engineConnLogOperator match {
      case yarnLogOperator: FlinkYarnLogOperator =>
        if (JobLauncherConfiguration.FLINK_FETCH_APPLICATION_LOG_ENABLE.getHotValue()) {
          engineConnLog = engineConnLogOperator()
        } else {
          logger.info("will not get flink yarn log, because switch if off.")
        }
      case _ =>
        engineConnLog = engineConnLogOperator()
    }
    logs = engineConnLog.logs
    logPath = engineConnLog.logPath
    endLine = engineConnLog.endLine
  }

  override def close(): Unit = isClosed = true

  override def hasNext: Boolean = {
    if(isClosed) return false
    else if(index < logs.size()) return true
    logs = engineConnLogOperator().logs
    while (logs == null || logs.isEmpty) {
      logs = engineConnLogOperator().logs
      if(isClosed) return false
      Utils.sleepQuietly(2000)
    }
    index = 0
    true
  }

  override def next(): String = {
    val log = logs.get(index)
    index += 1
    log
  }

  override def getLogPath: String = logPath

  override def getLogs: util.ArrayList[String] = logs

  override def getEndLine: Long = endLine

  def setLogDirSuffix(logDirSuffix: String) : Unit = this.logDirSuffix = logDirSuffix

  override def getLogDirSuffix: String = logDirSuffix
}
