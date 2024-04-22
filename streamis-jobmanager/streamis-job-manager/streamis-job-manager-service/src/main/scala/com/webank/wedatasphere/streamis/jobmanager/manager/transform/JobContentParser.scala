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

package com.webank.wedatasphere.streamis.jobmanager.manager.transform

import org.apache.linkis.manager.label.entity.engine.RunType.RunType
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.{JobTemplateFiles, StreamJob, StreamJobVersion}
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity.StreamisTransformJobContent

/**
  * Created by enjoyyin on 2021/9/22.
  */
trait JobContentParser {

  val jobType: String

  val runType: RunType

  def canParse(job: StreamJob, jobVersion: StreamJobVersion): Boolean

  def parseTo(job: StreamJob, jobVersion: StreamJobVersion,jobTemplate: JobTemplateFiles): StreamisTransformJobContent

}