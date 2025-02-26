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

import com.webank.wedatasphere.streamis.jobmanager.launcher.conf.JobConstants
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.LaunchJob
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf.{JobConf, JobConfKeyConstants}

import java.util
import org.apache.linkis.common.utils.JsonUtils
import org.apache.linkis.protocol.utils.TaskUtils
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamisFile
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.{StreamisJobContentTransform, Transform}
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity.{StreamisJarTransformJobContent, StreamisTransformJob, StreamisTransformJobContent}
import com.webank.wedatasphere.streamis.jobmanager.manager.utils.JobUtils

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * Created by enjoyyin on 2021/9/23.
  */
class FlinkJarStreamisJobContentTransform extends StreamisJobContentTransform {
  override protected def transformJobContent(transformJob: StreamisTransformJobContent): util.HashMap[String, AnyRef] = transformJob match {
    case transformJobContent: StreamisJarTransformJobContent =>
      val jobContent = new util.HashMap[String, AnyRef]
      jobContent.put(JobConf.STMS_FLINK_APPLICATION_ARGS_KEY.getValue, transformJobContent.getArgs.asScala.mkString(JobConf.FLINK_APPLICATION_SEPARATE.getHotValue()))
      jobContent.put(JobConf.STMS_FLINK_APPLICATION_SEPARATE_KEY.getValue, JobConf.FLINK_APPLICATION_SEPARATE.getHotValue())
      jobContent.put(JobConf.STMS_FLINK_APPLICATION_MAIN_CLASS_KEY.getValue, transformJobContent.getMainClass)
      jobContent
    case _ => null
  }
}

class FlinkJarStreamisStartupParamsTransform extends Transform {

  override def transform(streamisTransformJob: StreamisTransformJob, job: LaunchJob): LaunchJob = streamisTransformJob.getStreamisTransformJobContent match {
    case transformJobContent: StreamisJarTransformJobContent =>
      val startupMap = new util.HashMap[String, AnyRef]
      startupMap.put("flink.app.main.class.jar", transformJobContent.getMainClassJar.getFileName)
      startupMap.put("flink.app.main.class.jar.bml.json",
        JsonUtils.jackson.writeValueAsString(getStreamisFileContent(transformJobContent.getMainClassJar)))

      /**
       *  Notice : "flink.app.user.class.path" equals to PipelineOptions.CLASSPATHS in Flink
       *  paths must specify a protocol (e.g. file://) and be accessible on all nodes
       *  so we use "flink.yarn.ship-directories" instead
        */
      var classPathFiles = Option(transformJobContent.getDependencyJars) match {
        case Some(list) => list.asScala
        case _ => mutable.Buffer[StreamisFile]()
      }
      Option(transformJobContent.getResources) match {
        case Some(list) => classPathFiles = classPathFiles ++ list.asScala
        case _ => // Do nothing
      }
      startupMap.put("flink.yarn.ship-directories", classPathFiles.map(_.getFileName).mkString(","))
      if(classPathFiles.nonEmpty)
        startupMap.put("flink.app.user.class.path.bml.json",
          JsonUtils.jackson.writeValueAsString(classPathFiles.map(getStreamisFileContent).asJava))
      if(transformJobContent.getHdfsJars != null) {
        startupMap.put("flink.user.lib.path", transformJobContent.getHdfsJars.asScala.mkString(","))
      }
      // clientTpe
      val prodConfig = streamisTransformJob.getConfigMap.get(JobConfKeyConstants.GROUP_PRODUCE.getValue).asInstanceOf[util.HashMap[String, AnyRef]]
      startupMap.put(JobConfKeyConstants.MANAGE_MODE_KEY.getValue, prodConfig.getOrDefault(JobConfKeyConstants.MANAGE_MODE_KEY.getValue, JobConstants.MANAGE_MODE_ATTACH))
      val params = if(job.getParams == null) new util.HashMap[String, AnyRef] else job.getParams
      if (!startupMap.isEmpty) {
        TaskUtils.addStartupMap(params, JobUtils.filterParameterSpec(startupMap))
      }
      LaunchJob.builder().setLaunchJob(job).setParams(params).build()
    case _ => job
  }

  private def getStreamisFileContent(streamisFile: StreamisFile): util.Map[String, Object] = {
    val content = JsonUtils.jackson.readValue(streamisFile.getStorePath, classOf[util.Map[String, Object]])
    content.put("fileName", streamisFile.getFileName)
    content
  }

}