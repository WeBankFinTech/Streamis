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

  override protected def transform(internalConfig: util.Map[String, AnyRef], job: LaunchJob): LaunchJob = {
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
  /**
   * Defined in FlinkStreamisConfigDefine.LOG_GATEWAY_ADDRESS of 'flink-streamis-log-collector'
   */
  private val LOG_GATEWAY_CONFIG_NAME = CommonVars("wds.streamis.flink.config.name.log-gateway", "stream.log.gateway.address").getValue

  /**
   * Defined in FlinkStreamisConfigDefine.LOG_GATEWAY_ADDRESS of 'flink-streamis-log-collector'
   */
  private val LOG_COLLECT_PATH_CONFIG_NAME = CommonVars("wds.streamis.flink.config.name.log-collect-path", "stream.log.collect.path").getValue


  val INTERNAL_CONFIG_MAP = Map(JobConf.STREAMIS_JOB_LOG_GATEWAY.key -> LOG_GATEWAY_CONFIG_NAME,
    JobConf.STREAMIS_JOB_LOG_COLLECT_PATH.key -> LOG_COLLECT_PATH_CONFIG_NAME
  )
}
