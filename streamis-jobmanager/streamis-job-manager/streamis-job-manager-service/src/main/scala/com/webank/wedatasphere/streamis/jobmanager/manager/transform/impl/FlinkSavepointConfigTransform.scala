package com.webank.wedatasphere.streamis.jobmanager.manager.transform.impl
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.LaunchJob
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf.JobConfKeyConstants
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.conf.JobLauncherConfiguration.VAR_FLINK_SAVEPOINT_PATH
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.impl.FlinkSavepointConfigTransform.SAVE_POINT_PREFIX
import org.apache.commons.lang3.StringUtils

import java.util
import scala.collection.JavaConverters._

/**
 * Flink savepoint config
 */
class FlinkSavepointConfigTransform extends FlinkConfigTransform {


  /**
   * Config group name
   *
   * @return
   */
  override protected def configGroup(): String = JobConfKeyConstants.GROUP_PRODUCE.getValue

  override protected def transform(valueSet: util.Map[String, AnyRef], job: LaunchJob): LaunchJob = {
    val config: util.Map[String, AnyRef] = valueSet.asScala.filter(kv =>
      kv._1.startsWith(JobConfKeyConstants.SAVEPOINT.getValue) && StringUtils.isNotBlank(String.valueOf(kv._2)))
      .map{
        case (key, value) =>
          (FlinkConfigTransform.FLINK_CONFIG_PREFIX + key.replace(JobConfKeyConstants.SAVEPOINT.getValue, SAVE_POINT_PREFIX), value)
      }.asJava
    Option(config.get(FlinkConfigTransform.FLINK_CONFIG_PREFIX + SAVE_POINT_PREFIX + "path")) match {
      case Some(path) =>
        config.put(VAR_FLINK_SAVEPOINT_PATH.getValue, path)
      case _ =>
    }
    transformConfig(config, job)
  }
}

object FlinkSavepointConfigTransform{
  val SAVE_POINT_PREFIX: String = "execution.savepoint."
}
