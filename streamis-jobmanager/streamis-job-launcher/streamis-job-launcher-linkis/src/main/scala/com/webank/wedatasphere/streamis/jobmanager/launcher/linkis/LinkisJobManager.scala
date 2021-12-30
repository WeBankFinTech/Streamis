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

package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis

import org.apache.linkis.common.utils.ClassUtils
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.{LaunchJob, LinkisJobInfo}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.FlinkJobLaunchErrorException


trait LinkisJobManager {

  def getName: String

  def launch(job: LaunchJob): String

  def stop(id: String, user: String): Unit

  def getJobInfo(id: String, user: String): LinkisJobInfo

}

import scala.collection.convert.WrapAsScala._

object LinkisJobManager {

  val SIMPLE_FLINK = "simpleFlink"

  private val linkisJobManagers =
    ClassUtils.reflections.getSubTypesOf(classOf[LinkisJobManager]).filterNot(ClassUtils.isInterfaceOrAbstract)
      .map { clazz =>
        val jobManager = clazz.newInstance()
        jobManager.getName -> jobManager
      }.toMap

  def getLinkisJobManager(jobManagerType: String): LinkisJobManager = linkisJobManagers.getOrElse(jobManagerType,
    throw new FlinkJobLaunchErrorException(30402, "Not exist LinkisJobManagerType " + jobManagerType))

  def getLinkisJobManager: LinkisJobManager = getLinkisJobManager(SIMPLE_FLINK)

}