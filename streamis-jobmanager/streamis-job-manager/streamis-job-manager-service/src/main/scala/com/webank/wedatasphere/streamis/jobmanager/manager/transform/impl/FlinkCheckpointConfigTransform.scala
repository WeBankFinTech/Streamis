package com.webank.wedatasphere.streamis.jobmanager.manager.transform.impl

import java.util

import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.vo.ConfigKeyVO
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LaunchJob
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.ConfigTransform
import org.apache.commons.lang.StringUtils
import org.apache.linkis.protocol.utils.TaskUtils

import scala.collection.JavaConverters._

/**
 *
 * @date 2021-11-11
 * @author enjoyyin
 * @since 0.5.0
 */
class FlinkCheckpointConfigTransform extends ConfigTransform {

  override protected def transform(config: ConfigKeyVO, job: LaunchJob): LaunchJob = {
    val productConfig = config.getProduceConfig
    if(productConfig == null || productConfig.isEmpty) return job
    productConfig.asScala.find(config => config.getKey == "wds.linkis.flink.checkpoint.interval"
      && StringUtils.isNumeric(config.getValue) && config.getValue.toLong > 0).map { config =>
      val startupMap = new util.HashMap[String, Any]
      startupMap.put("flink.app.checkpoint.enable", true)
      startupMap.put("flink.app.checkpoint.interval", config.getValue.toLong)
      if(job.getParams == null) {
        val params = new util.HashMap[String, Any]
        TaskUtils.addStartupMap(params, startupMap)
        LaunchJob.builder().setLaunchJob(job).setParams(params).build()
      } else {
        TaskUtils.addStartupMap(job.getParams, startupMap)
        job
      }
    }.getOrElse(job)
  }

}
