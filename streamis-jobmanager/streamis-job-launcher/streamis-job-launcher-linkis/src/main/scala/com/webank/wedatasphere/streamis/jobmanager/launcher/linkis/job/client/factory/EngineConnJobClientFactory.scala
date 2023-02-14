package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client.factory

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.{JobClient, JobInfo}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client.EngineConnJobClient
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.jobInfo.LinkisJobInfo
import org.apache.linkis.computation.client.once.simple.SimpleOnceJobBuilder
import org.apache.linkis.computation.client.once.{LinkisManagerClient, OnceJob}
import org.apache.linkis.httpclient.dws.DWSHttpClient

class EngineConnJobClientFactory extends JobClientFactory {

  /**
   * The linkis client in onceJob
   */
  private var linkisClient: DWSHttpClient = _

  /**
   * Create job client
   *
   * @param onceJob once job
   * @param jobInfo job info
   * @param jobStateManager
   * @return
   */
   override def createJobClient(onceJob: OnceJob, jobInfo: JobInfo, jobStateManager: JobStateManager): JobClient[LinkisJobInfo] = {
     val flinkEngineConnJobClient = new EngineConnJobClient(onceJob, jobInfo, jobStateManager)
     flinkEngineConnJobClient.setLinkisClient(this.linkisClient)
     flinkEngineConnJobClient.asInstanceOf[JobClient[LinkisJobInfo]]
   }

  /**
   * Init the factory
   */
  override def init(): Unit = {
    if (null == this.linkisClient){
      this.synchronized{
        if (null == this.linkisClient){
          this.linkisClient = SimpleOnceJobBuilder.getLinkisManagerClient match {
            case client: LinkisManagerClient =>
              val dwsClientField = classOf[LinkisManagerClientImpl].getDeclaredField("dwsHttpClient")
              dwsClientField.setAccessible(true)
              dwsClientField.get(client).asInstanceOf[DWSHttpClient]
            case _ => null
          }
        }
      }
    }
  }
}
