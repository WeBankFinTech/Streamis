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

import java.util

import org.apache.linkis.computation.client.utils.LabelKeyUtils
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LaunchJob
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.Transform
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity.StreamisTransformJob


class LabelsStreamisCodeTransform extends Transform {

  override def transform(streamisTransformJob: StreamisTransformJob, job: LaunchJob): LaunchJob = {
    val labels = new util.HashMap[String, Any]
    labels.put(LabelKeyUtils.ENGINE_TYPE_LABEL_KEY, streamisTransformJob.getStreamisJobEngineConn.getEngineConnType)
    labels.put(LabelKeyUtils.USER_CREATOR_LABEL_KEY, streamisTransformJob.getStreamJob.getSubmitUser + "-Streamis")
    labels.put(LabelKeyUtils.ENGINE_CONN_MODE_LABEL_KEY, "once")
    if (job.getLabels != null) labels.putAll(job.getLabels)
    LaunchJob.builder().setLaunchJob(job).setLabels(labels).build()
  }

}
