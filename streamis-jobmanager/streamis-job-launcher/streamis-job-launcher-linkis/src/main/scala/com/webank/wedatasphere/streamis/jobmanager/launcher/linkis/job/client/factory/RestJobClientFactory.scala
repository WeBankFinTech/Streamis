package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client.factory

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.{JobClient, JobInfo, LaunchJob}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.jobInfo.{FlinkRestJobInfo, LinkisJobInfo}
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.linkis.common.conf.CommonVars
import org.apache.linkis.computation.client.once.OnceJob

import java.util.concurrent.ConcurrentHashMap

class RestJobClientFactory extends JobClientFactory {

  val jobClientTypeList: CommonVars[String] = CommonVars.apply("wds.streamis.job.client.type", "flink,spark")

  val jobClientMap = new ConcurrentHashMap[String, CloseableHttpClient]

  /**
   * Create job client
   *
   * @param onceJob
   * @param flinkJobInfo
   * @param jobStateManager
   * @return
   */
  override
  def createJobClient(onceJob: OnceJob, jobInfo: JobInfo, jobStateManager: JobStateManager): JobClient[LinkisJobInfo] = {

    //todo create flink spark client




    null
  }

  /**
   * Init the factory, create all kinds of client
   */
  override def init(): Unit = {
    // Create all kinds of client
    jobClientTypeList.getValue.split(",").toList.foreach((engineType: String) => {
      val httpClient = HttpClientUtil.createHttpClientUtil(engineType)
      jobClientMap.put(engineType, httpClient)
    })
  }
}
