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

  /**
   * This method is used to launch a new Linkis Job.
   * @param job a StreamisJob wanted to be launched.
   * @return the Linkis Job id.
   */
  def launch(job: LaunchJob): String

  /**
   * If a StreamisJob is already launched in another process, and you want to refresh the LinkisJobInfo,
   *  please use this method to launch an existed Linkis Job at first.
   * @param id the Linkis Job id.
   * @param jobInfo serialized LinkisJobInfo got from [[launch(job: LaunchJob)]]
   */
  def launch(id: String, jobInfo: String): Unit

  /**
   * If a StreamisJob is already launched in another process, and you want to refresh the LinkisJobInfo,
   *  please use this method to launch an existed Linkis Job at first.
   * @param id the Linkis Job id.
   * @param jobInfo LinkisJobInfo got from [[launch(job: LaunchJob)]]
   */
  def launch(id: String, jobInfo: LinkisJobInfo): Unit

  def isExists(id: String): Boolean

  /**
   * Stop a launched Linkis Job by Linkis JobId. You should use [[launch()]] method before use this method.
   * @param id Linkis JobId
   */
  def stop(id: String): Unit

  /**
   * get LinkisJobInfo of a launched Linkis Job by Linkis JobId. Usually, this method is used to update the status of Linkis Job.
   * You should use [[launch()]] method before use this method.
   * @param id Linkis JobId
   */
  def getJobInfo(id: String): LinkisJobInfo

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