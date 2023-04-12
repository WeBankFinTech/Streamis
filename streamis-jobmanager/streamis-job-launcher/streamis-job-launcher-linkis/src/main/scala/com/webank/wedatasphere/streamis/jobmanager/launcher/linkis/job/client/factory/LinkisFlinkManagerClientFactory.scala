package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client.factory
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.{JobClient, JobInfo}
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client.{LinkisFlinkManagerClient, LinkisFlinkManagerJobClient}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.jobInfo.LinkisJobInfo
import org.apache.linkis.computation.client.once.OnceJob

class LinkisFlinkManagerClientFactory extends JobClientFactory {

  override def createJobClient(onceJob: OnceJob, jobInfo: JobInfo, jobStateManager: JobStateManager): JobClient[LinkisJobInfo] = {
    val client = new LinkisFlinkManagerJobClient(onceJob, jobInfo, jobStateManager)
    client.asInstanceOf[JobClient[LinkisJobInfo]]
  }

  /**
   * Init the factory
   */
  override def init(): Unit = {}
}
