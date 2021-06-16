package com.webank.wedatasphere.streamis.jobmanager.manager.transform.impl

import java.util

import com.webank.wedatasphere.linkis.protocol.utils.TaskUtils
import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.vo.{ConfigKeyVO, ConfigRelationVO}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LaunchJob
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.ConfigTransform

import scala.collection.convert.WrapAsScala._

/**
 *
 * @date 2021-06-06
 * @author enjoyyin
 * @since 0.5.0
 */
class ResourceConfigTransform extends ConfigTransform {

  override protected def transform(config: ConfigKeyVO, job: LaunchJob): LaunchJob =
    transformConfig(config.getResourceConfig, job)

  protected def transformConfig(getConfig: => util.List[ConfigRelationVO], job: LaunchJob): LaunchJob = {
    val startupMap = new util.HashMap[String, Any]
    val configSeq = getConfig
    if(configSeq != null) configSeq.foreach { vo =>
      startupMap.put(vo.getKey, vo.getValue)
    }
    val params = if(job.getParams == null) new util.HashMap[String, Any] else job.getParams
    if(!startupMap.isEmpty) TaskUtils.addStartupMap(params, startupMap)
    LaunchJob.builder().setLaunchJob(job).setParams(params).build()
  }

}
