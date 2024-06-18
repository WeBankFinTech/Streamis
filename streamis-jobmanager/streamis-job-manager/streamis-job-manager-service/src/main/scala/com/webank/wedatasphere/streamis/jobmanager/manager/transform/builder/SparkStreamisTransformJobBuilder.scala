package com.webank.wedatasphere.streamis.jobmanager.manager.transform.builder

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.exception.JobExecuteErrorException
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.{JobTemplateFiles, StreamJob}
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.JobContentParser
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity.{StreamisTransformJob, StreamisTransformJobContent}
import org.apache.linkis.manager.label.entity.engine.RunType.RunType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SparkStreamisTransformJobBuilder extends AbstractDefaultStreamisTransformJobBuilder {

  @Autowired private var jobContentParsers: Array[JobContentParser] = _

  override def canBuild(streamJob: StreamJob): Boolean = jobContentParsers.map(_.jobType).contains(streamJob.getJobType.toLowerCase)

  override protected def getRunType(transformJob: StreamisTransformJob): RunType =
    jobContentParsers.find(_.jobType == transformJob.getStreamJob.getJobType.toLowerCase).map(_.runType).get

  override protected def createStreamisTransformJobContent(transformJob: StreamisTransformJob,jobTemplate: JobTemplateFiles): StreamisTransformJobContent =
    jobContentParsers.find(_.canParse(transformJob.getStreamJob, transformJob.getStreamJobVersion))
      .map(_.parseTo(transformJob.getStreamJob, transformJob.getStreamJobVersion,jobTemplate))
      .getOrElse(throw new JobExecuteErrorException(30350, "Not support jobContent " + transformJob.getStreamJobVersion.getJobContent))

}
