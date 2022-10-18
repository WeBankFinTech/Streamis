package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.operator

import org.apache.commons.lang3.StringUtils
import org.apache.linkis.computation.client.once.action.EngineConnOperateAction
import org.apache.linkis.computation.client.operator.impl.{EngineConnLogOperator, EngineConnLogs}

/**
 * Append "logDirSuffix" parameter
 */
class FlinkClientLogOperator extends EngineConnLogOperator{

  private var logDirSuffix: String = _

  def setLogDirSuffix(logDirSuffix: String): Unit = {
    this.logDirSuffix = logDirSuffix
  }

  protected override def addParameters(builder: EngineConnOperateAction.Builder): Unit = {
    builder.operatorName(EngineConnLogOperator.OPERATOR_NAME)
    if (StringUtils.isNotBlank(this.logDirSuffix)) {
      builder.addParameter("logDirSuffix", logDirSuffix)
    }
    super.addParameters(builder)
  }


  override def getTicketId: String = super.getTicketId

  override def getName: String = FlinkClientLogOperator.OPERATOR_NAME
}

object FlinkClientLogOperator {
  val OPERATOR_NAME = "engineConnLog_flink"
}
