package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.FlinkJobInfo
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.linkis.computation.client.once.OnceJob

/**
 * @author jefftlin
 */
abstract class AbstractRestJobClient(onceJob: OnceJob, jobInfo: FlinkJobInfo, stateManager: JobStateManager) extends AbstractJobClient(onceJob, jobInfo, stateManager) {

  /**
   * The http client in onceJob
   */
  protected var httpClient: CloseableHttpClient = _

  def setHttpClient(httpClient: CloseableHttpClient): Unit = {
    this.httpClient = httpClient
  }

  /**
   * Get linkis client
   * @return
   */
  def getHttpClient(): CloseableHttpClient = httpClient
}
