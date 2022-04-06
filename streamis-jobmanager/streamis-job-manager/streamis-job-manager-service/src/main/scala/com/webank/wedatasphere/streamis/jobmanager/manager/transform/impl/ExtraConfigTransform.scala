/*
 * Copyright 2021 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.streamis.jobmanager.manager.transform.impl

import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.vo.{ConfigKeyVO, ConfigRelationVO}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LaunchJob
import org.springframework.beans.BeanUtils

import scala.collection.JavaConverters._


class ExtraConfigTransform extends ResourceConfigTransform {

  override protected def transform(config: ConfigKeyVO, job: LaunchJob): LaunchJob = {
    val newConfigs = config.getParameterConfig.asScala.map{config =>
      val newConfig = new ConfigRelationVO
      BeanUtils.copyProperties(config, newConfig)
      newConfig.setKey(ExtraConfigTransform.FLINK_CONFIG_PREFIX + config.getKey)
      newConfig
    }.asJava
    transformConfig(newConfigs, job)
  }

}
object ExtraConfigTransform {
  private val FLINK_CONFIG_PREFIX = "_FLINK_CONFIG_."
}