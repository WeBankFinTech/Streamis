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

package com.webank.wedatasphere.streamis.jobmanager.manager.transform.builder

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf.{JobConf, JobConfKeyConstants}
import org.apache.linkis.common.conf.CommonVars
import org.apache.linkis.manager.label.entity.engine.RunType.RunType
import com.webank.wedatasphere.streamis.jobmanager.launcher.service.StreamJobConfService
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.{StreamJobMapper, StreamJobTemplateMapper, StreamTaskMapper}
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.{JobTemplateFiles, StreamJob}
import com.webank.wedatasphere.streamis.jobmanager.manager.service.{StreamJobService, StreamTaskService}
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.StreamisTransformJobBuilder
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity.{StreamisJobConnect, StreamisJobConnectImpl, StreamisJobEngineConnImpl, StreamisTransformJob, StreamisTransformJobContent, StreamisTransformJobImpl}
import org.springframework.beans.factory.annotation.Autowired

import java.util.{Map => JavaMap}
import java.util
import scala.collection.JavaConverters.mapAsJavaMapConverter
/**
  * Created by enjoyyin on 2021/9/22.
  */
abstract class AbstractStreamisTransformJobBuilder extends StreamisTransformJobBuilder {

  @Autowired private var streamJobMapper: StreamJobMapper = _
  @Autowired private var streamJobConfService: StreamJobConfService = _
  @Autowired private var streamJobService: StreamJobService = _
  @Autowired private var streamTaskMapper: StreamTaskMapper = _
  @Autowired private var streamJobTemplateMapper:StreamJobTemplateMapper = _

  protected def createStreamisTransformJob(): StreamisTransformJobImpl = new StreamisTransformJobImpl

  protected def createStreamisTransformJobContent(transformJob: StreamisTransformJob,jobTemplate: JobTemplateFiles): StreamisTransformJobContent

  override def build(streamJob: StreamJob): StreamisTransformJob = {
    val transformJob = createStreamisTransformJob()
    transformJob.setStreamJob(streamJob)
    val jobConfig: util.Map[String, AnyRef] = Option(streamJobConfService.getJobConfig(streamJob.getId))
      .getOrElse(new util.HashMap[String, AnyRef]())
    val jobTemplateMapOption = Option(streamJobService.getJobTemplateConfMap(streamJob))
    val finalJobConfig: util.Map[String, AnyRef] = jobTemplateMapOption match {
      case Some(jobTemplateMap) =>
        val mergedConfig = new util.HashMap[String, Object](jobTemplateMap)
        streamJobService.mergeJobConfig(mergedConfig, jobConfig)
        mergedConfig
      case None =>
        jobConfig
    }
    // Put and overwrite internal group, users cannot customize the internal configuration
    val internalGroup = new util.HashMap[String, AnyRef]()
    finalJobConfig.put(JobConfKeyConstants.GROUP_INTERNAL.getValue, internalGroup)
    internalLogConfig(internalGroup)
    transformJob.setConfigMap(finalJobConfig)
    val streamJobVersions = streamJobMapper.getJobVersions(streamJob.getId)
    // 无需判断streamJobVersions是否非空，因为TaskService已经判断了
    transformJob.setStreamJobVersion(streamJobVersions.get(0))
    val streamTask = streamTaskMapper.getLatestByJobId(streamJob.getId)
    val jobTemplate: JobTemplateFiles = if (null != streamTask) {
      if (streamTask.getStatus.equals(JobConf.FLINK_JOB_STATUS_RUNNING.getValue)) {
        streamJobTemplateMapper.getJobTemplate(streamTask.getTemplateId,true)
      }else{
        streamJobTemplateMapper.getLatestJobTemplateFile(streamJob.getProjectName,true)
      }
    }else{
      streamJobTemplateMapper.getLatestJobTemplateFile(streamJob.getProjectName,true)
    }
    transformJob.setStreamisTransformJobContent(createStreamisTransformJobContent(transformJob,jobTemplate))
    transformJob
  }

  /**
   * Log internal configuration
   * @param internal internal config group
   */
  private def internalLogConfig(internal: util.Map[String, AnyRef]): Unit = {
    internal.put(JobConf.STREAMIS_JOB_LOG_GATEWAY.key, JobConf.STREAMIS_JOB_LOG_GATEWAY.getValue)
    internal.put(JobConf.STREAMIS_JOB_LOG_COLLECT_PATH.key, JobConf.STREAMIS_JOB_LOG_COLLECT_PATH.getValue)
    internal.put(JobConf.STREAMIS_JOB_LOG_HEARTBEAT_PATH.key, JobConf.STREAMIS_JOB_LOG_HEARTBEAT_PATH.getValue)
    internal.put(JobConf.STREAMIS_JOB_LOG_HEARTBEAT_INTERVAL.key, new Integer(JobConf.STREAMIS_JOB_LOG_HEARTBEAT_INTERVAL.getValue))
  }
}

abstract class AbstractDefaultStreamisTransformJobBuilder extends AbstractStreamisTransformJobBuilder{

  private val flinkVersion = CommonVars("wds.streamis.flink.submit.version", "1.12.2").getValue

  protected def getRunType(transformJob: StreamisTransformJob): RunType

  override def build(streamJob: StreamJob): StreamisTransformJob = super.build(streamJob) match {
    case transformJob: StreamisTransformJobImpl =>
      val engineConn = new StreamisJobEngineConnImpl
      engineConn.setEngineConnType("flink-" + flinkVersion)
      transformJob.setStreamisJobEngineConn(engineConn)
      val streamisJobConnect = new StreamisJobConnectImpl
      streamisJobConnect.setRunType(getRunType(transformJob))
      streamisJobConnect.setRunEngineVersion(flinkVersion)
      transformJob.setStreamisJobConnect(streamisJobConnect)
      transformJob
    case job => job
  }
}