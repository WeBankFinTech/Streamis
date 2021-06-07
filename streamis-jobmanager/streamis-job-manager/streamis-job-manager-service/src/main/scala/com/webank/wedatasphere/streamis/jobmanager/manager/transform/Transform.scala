package com.webank.wedatasphere.streamis.jobmanager.manager.transform

import com.webank.wedatasphere.linkis.common.utils.ClassUtils
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LaunchJob
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity.StreamisTransformJob

/**
 *
 * @date 2021-06-06
 * @author enjoyyin
 * @since 0.5.0
 */
trait Transform {

  def transform(streamisTransformJob: StreamisTransformJob, job: LaunchJob): LaunchJob

}

import scala.collection.convert.WrapAsScala._
object Transform {

  private val transforms = ClassUtils.reflections.getSubTypesOf(classOf[Transform]).filterNot(ClassUtils.isInterfaceOrAbstract)
    .map(_.newInstance).toArray

  def getTransforms: Array[Transform] = transforms

}