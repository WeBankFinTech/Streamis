package com.webank.wedatasphere.streamis.jobmanager.manager.transform.impl
import com.webank.wedatasphere.streamis.jobmanager.launcher.conf.JobConfKeyConstants
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.LaunchJob
import com.webank.wedatasphere.streamis.jobmanager.manager.conf.JobConf
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.impl.FlinkInternalConfigTransform.INTERNAL_CONFIG_MAP
import org.apache.linkis.common.conf.CommonVars

import java.util
import scala.collection.JavaConverters.{mapAsJavaMapConverter, mapAsScalaMapConverter}

/**
 * Flink internal config transform
 */
class FlinkInternalConfigTransform extends FlinkConfigTransform {

  /**
   * Config group name
   *
   * @return
   */
  override protected def configGroup(): String = JobConfKeyConstants.GROUP_INTERNAL.getValue

  override protected def transform(internalConfig: util.Map[String, Any], job: LaunchJob): LaunchJob = {
    transformConfig(internalConfig.asScala.map{
      case (key, value) =>
        (FlinkConfigTransform.FLINK_CONFIG_PREFIX + (INTERNAL_CONFIG_MAP.get(key) match {
          case Some(mappingKey) => mappingKey
          case _ => value
        }), value)
    }.asJava, job)
  }
}

object FlinkInternalConfigTransform {

  private val FLINK_LOG_GATEWAY_CONFIG_NAME = CommonVars("wds.streamis.flink.config.name.log-gateway", "stream.log.gateway.address").getValue

  private val FLINK_LOG_COLLECT_PATH_CONFIG_NAME = CommonVars("wds.streamis.flink.config.name.log-collect-path", "stream.log.collect.path").getValue

  val INTERNAL_CONFIG_MAP = Map(JobConf.STREAMIS_JOB_LOG_GATEWAY.key -> FLINK_LOG_GATEWAY_CONFIG_NAME,
    JobConf.STREAMIS_JOB_LOG_COLLECT_PATH.key -> FLINK_LOG_COLLECT_PATH_CONFIG_NAME
  )
}
