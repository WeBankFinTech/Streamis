package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.FlinkJobInfo
import org.apache.linkis.computation.client.once.OnceJob
import org.apache.linkis.httpclient.dws.DWSHttpClient

/**
 * @author jefftlin
 */
abstract class LinkisEngineConnJobClient(onceJob: OnceJob, override var jobInfo: FlinkJobInfo, stateManager: JobStateManager) extends AbstractJobClient(onceJob, jobInfo, stateManager) {

  /**
   * The linkis client in onceJob
   */
  protected var linkisClient: DWSHttpClient = _

  def setLinkisClient(linkisClient: DWSHttpClient): Unit = {
    this.linkisClient = linkisClient
  }

  def getLinkisClient(): DWSHttpClient = {
    this.linkisClient
  }
}
