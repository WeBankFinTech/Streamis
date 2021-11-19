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

package com.webank.wedatasphere.streamis.jobmanager.launcher.conf

import org.apache.linkis.common.conf.CommonVars


object ConfigConf {

  val JOBMANAGER_FLINK_RESOURCE = CommonVars("wds.linkis.flink.resource", 1)

  val JOBMANAGER_FLINK_CUSTOM = CommonVars("wds.linkis.flink.custom", 2)

  val JOBMANAGER_FLINK_PRODUCE = CommonVars("wds.linkis.flink.produce", 3)

  val JOBMANAGER_FLINK_ALERT = CommonVars("wds.linkis.flink.alert", 4)

  val JOBMANAGER_FLINK_AUTHORITY = CommonVars("wds.linkis.flink.authority", 5)

  val JOBMANAGER_FLINK_CUSTOM_STATUS_ONE = CommonVars("wds.linkis.flink.custom.one", 1)

  val JOBMANAGER_FLINK_CUSTOM_STATUS_TWO = CommonVars("wds.linkis.flink.custom.two", 2)

  val JOBMANAGER_FLINK_AUTHORITY_VISIBLE = CommonVars("wds.linkis.flink.authority.visible", "wds.linkis.flink.authority.visible")

  val JOBMANAGER_FLINK_ALERT_USER = CommonVars("wds.linkis.flink.alert.failure.user", "wds.linkis.flink.alert.failure.user")

  val JOBMANAGER_FLINK_AUTHORITY_AUTHOR = CommonVars("wds.linkis.flink.authority.author", "wds.linkis.flink.authority.author")

  val JOBMANAGER_FLINK_ALERT_RULE = CommonVars("wds.linkis.flink.alert.rule", "wds.linkis.flink.alert.rule")

  val JOBMANAGER_FLINK_ALERT_LEVEL = CommonVars("wds.linkis.flink.alert.level", "wds.linkis.flink.alert.level")

}
