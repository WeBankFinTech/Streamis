package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis

import com.webank.wedatasphere.linkis.common.utils.ClassUtils
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.{LaunchJob, LinkisJobInfo}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.FlinkJobLaunchErrorException

/**
 *
 * @date 2021-06-05
 * @author enjoyyin
 * @since 0.5.0
 */
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