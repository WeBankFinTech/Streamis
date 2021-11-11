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

package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.manager

import java.util

import org.apache.linkis.computation.client.once.simple.SimpleOnceJob
import org.apache.linkis.computation.client.once.{OnceJob, SubmittableOnceJob}
import org.apache.linkis.computation.client.operator.impl.ApplicationInfoOperator
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.LinkisJobManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.{FlinkJobInfo, LaunchJob, LinkisJobInfo}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.FlinkJobLaunchErrorException


class SimpleFlinkJobManager extends FlinkJobManager {

  override def getName: String = LinkisJobManager.SIMPLE_FLINK

  protected def buildOnceJob(job: LaunchJob): SubmittableOnceJob = SimpleOnceJob.builder().addExecuteUser(job.getSubmitUser).setLabels(job.getLabels)
    .setJobContent(job.getJobContent).setParams(job.getParams).setSource(job.getSource).build()

  override protected def createSubmittedOnceJob(id: String, user: String): OnceJob = SimpleOnceJob.build(id, user)

  override protected def getStatus(id: String, user: String): String = getOnceJob(id, user) match {
    case simpleOnceJob: SimpleOnceJob =>
      if (simpleOnceJob.isCompleted) deleteOnceJob(id)
      simpleOnceJob.getStatus
  }

  override def getCheckpoints(id: String, user: String): LinkisJobInfo = throw new FlinkJobLaunchErrorException(30401, "Not support method")

  override def triggerSavepoint(id: String, user: String): LinkisJobInfo = throw new FlinkJobLaunchErrorException(30401, "Not support method")

  override def createJobInfo(id: String, user: String): LinkisJobInfo = {
    val nodeInfo = getOnceJob(id, user).getNodeInfo
    val jobInfo = new FlinkJobInfo
    jobInfo.setId(id)
    jobInfo.setUser(user)
    fetchApplicationInfo(jobInfo)
    jobInfo.setResources(nodeInfo.get("nodeResource").asInstanceOf[util.Map[String, Object]])
    jobInfo.setLogPath("") //TODO wait for completed
    jobInfo
  }

  protected def fetchApplicationInfo(jobInfo: FlinkJobInfo): Unit = {
    val applicationInfo = getOnceJob(jobInfo.getId, jobInfo.getUser).getOperator(ApplicationInfoOperator.OPERATOR_NAME) match {
      case applicationInfoOperator: ApplicationInfoOperator => applicationInfoOperator.apply()
    }
    jobInfo.setApplicationId(applicationInfo.applicationId)
    jobInfo.setApplicationUrl(applicationInfo.applicationUrl)
  }
}
