package com.webank.wedatasphere.streamis.jobmanager.launcher.service
import com.webank.wedatasphere.streamis.jobmanager.launcher.dao.StreamJobConfMapper
import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.{JobConfDefinition, JobConfValue}
import com.webank.wedatasphere.streamis.jobmanager.launcher.exception.ConfigurationException
import com.webank.wedatasphere.streamis.jobmanager.launcher.service.tools.JobConfValueUtils
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.StreamJobMapper
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJob
import org.apache.linkis.common.utils.Logging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util
import javax.annotation.Resource
import scala.collection.JavaConverters._

@Service
class DefaultStreamJobConfService extends StreamJobConfService with Logging{

  @Resource
  private var streamJobConfMapper: StreamJobConfMapper = _

  @Resource
  private var streamJobMapper: StreamJobMapper = _
  /**
   * Get all config definitions
   *
   * @return list
   */
  override def loadAllDefinitions(): util.List[JobConfDefinition] = {
    streamJobConfMapper.loadAllDefinitions()
  }

  /**
   * Save job configuration
   *
   * @param jobId    job id
   * @param valueMap value map
   */
  @Transactional(rollbackFor = Array(classOf[Exception]))
  override def saveJobConfig(jobId: Long, valueMap: util.Map[String, Any]): Unit = {
    // Can deserialize the value map at first
    val configValues = JobConfValueUtils.deserialize(valueMap, Option(this.streamJobConfMapper.loadAllDefinitions())
      .getOrElse(new util.ArrayList[JobConfDefinition]()))
    trace(s"Query and lock the StreamJob in [$jobId] before saving/update configuration")
    Option(streamJobMapper.queryAndLockJobById(jobId)) match {
      case None => throw new ConfigurationException(s"Unable to saving/update configuration, the StreamJob [$jobId] is not exists.")
      case Some(job: StreamJob) =>
        // Delete all configuration
        this.streamJobConfMapper.deleteConfValuesByJobId(job.getId)
        configValues.asScala.foreach(configValue => {{
          configValue.setJobId(job.getId)
          configValue.setJobName(job.getName)
        }})
        info(s"Save the job configuration size: ${configValues.size()}, jobName: ${job.getName}")
        if (!configValues.isEmpty) {
          // Send to save the configuration new
          this.streamJobConfMapper.batchInsertValues(configValues)
        }
    }
  }

  /**
   * Query the job configuration
   *
   * @param jobId job id
   * @return
   */
  override def getJobConfig(jobId: Long): util.Map[String, Any] = {
    Option(this.streamJobConfMapper.getConfValuesByJobId(jobId)) match {
      case None => new util.HashMap[String, Any]()
      case Some(list: util.List[JobConfValue]) =>
        JobConfValueUtils.serialize(list,
          Option(this.streamJobConfMapper.loadAllDefinitions())
              .getOrElse(new util.ArrayList[JobConfDefinition]()))
    }
  }
}
