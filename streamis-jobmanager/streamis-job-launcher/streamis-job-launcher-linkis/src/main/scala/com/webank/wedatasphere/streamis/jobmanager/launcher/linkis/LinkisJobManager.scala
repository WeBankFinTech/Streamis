package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis

import java.util

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

  private val linkisJobManagers = new util.HashMap[String, LinkisJobManager]()
  ClassUtils.reflections.getSubTypesOf(classOf[LinkisJobManager]).filterNot(ClassUtils.isInterfaceOrAbstract)
    .foreach { clazz =>
      val jobManager = clazz.newInstance()
      linkisJobManagers.put(jobManager.getName, jobManager)
    }

  def getLinkisJobManager(jobManagerType: String): LinkisJobManager = linkisJobManagers.getOrDefault(jobManagerType,
    throw new FlinkJobLaunchErrorException(30402, "Not exist LinkisJobManagerType " + jobManagerType))

  def getLinkisJobManager: LinkisJobManager = getLinkisJobManager(SIMPLE_FLINK)

}