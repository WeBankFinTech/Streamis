package com.webank.wedatasphere.streamis.jobmanager.manager.transform.builder

import com.webank.wedatasphere.streamis.jobmanager.launcher.service.ConfigurationService
import com.webank.wedatasphere.streamis.jobmanager.manager.conf.JobConf.JOBMANAGER_FLINK_SQL
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.StreamJobMapper
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJob
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.StreamisTransformJobBuilder
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity.{StreamisSqlTransformJob, StreamisTransformJob}
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.exception.TransformFailedErrorException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 *
 * @date 2021-06-06
 * @author enjoyyin
 * @since 0.5.0
 */
@Component
class StreamisFlinkSqlTransformJobBuilder extends StreamisTransformJobBuilder {

  @Autowired private var streamJobMapper: StreamJobMapper = _
  @Autowired private var configurationService: ConfigurationService = _

  override def canBuild(streamJob: StreamJob): Boolean = JOBMANAGER_FLINK_SQL.getValue == streamJob.getJobType.intValue()

  override def build(streamJob: StreamJob): StreamisTransformJob = {
    val transformJob = new StreamisSqlTransformJob
    transformJob.setEngineConnType("flink")
    transformJob.setStreamJob(streamJob)
    transformJob.setConfig(configurationService.getFullTree(streamJob.getId))
    transformJob.setStreamJobVersion(streamJobMapper.getJobVersionsById(streamJob.getId, streamJob.getCurrentVersion).get(0))
    val jobSqlResources = streamJobMapper.getJobSQLResourcesByJobId(transformJob.getStreamJobVersion.getId)
    val jobSqlResourcesSize = if(jobSqlResources == null) 0 else jobSqlResources.size()
    if(jobSqlResourcesSize != 1) throw new TransformFailedErrorException(30408,
      s"StreamJob-${streamJob.getName} has $jobSqlResourcesSize JobSQLResources.")
    transformJob.setStreamJobSqlResource(jobSqlResources.get(0))
    transformJob
  }

}
