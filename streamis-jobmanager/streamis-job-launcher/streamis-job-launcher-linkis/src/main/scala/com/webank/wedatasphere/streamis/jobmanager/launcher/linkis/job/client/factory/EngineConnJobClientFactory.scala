package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client.factory

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.{JobClient, JobInfo}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client.EngineConnJobClient
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.jobInfo.LinkisJobInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.utils.LinkisClientUtils
import org.apache.linkis.computation.client.once.simple.SimpleOnceJobBuilder
import org.apache.linkis.computation.client.once.{LinkisManagerClient, LinkisManagerClientImpl, OnceJob}
import org.apache.linkis.httpclient.dws.DWSHttpClient

class EngineConnJobClientFactory extends JobClientFactory {


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
     flinkEngineConnJobClient.setLinkisClient(LinkisClientUtils.getLinkisDwsClient)
     flinkEngineConnJobClient.asInstanceOf[JobClient[LinkisJobInfo]]
   }

  /**
   * Init the factory
   */
  override def init(): Unit = {
    //init
  }
}
