package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client.factory

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.`type`.JobClientType
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.{JobClient, JobInfo, LaunchJob}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.FlinkJobLaunchErrorException
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.{FlinkJobInfo, LinkisJobInfo}
import org.apache.commons.lang3.StringUtils
import org.apache.linkis.computation.client.once.OnceJob

class AbstractJobClientFactory {

  var engineConnJobClientFactory: EngineConnJobClientFactory = _

  var restJobClientFactory: RestJobClientFactory = _

  def validateClientInfo(jobInfo: JobInfo): Boolean = {
    StringUtils.isNotBlank(jobInfo.getEngineType) && StringUtils.isNotBlank(jobInfo.getEngineVersion)
  }

  /**
   * Create job client
   *
   * @param onceJob once job
   * @param jobInfo job info
   * @return
   */
  def createJobClient(job: LaunchJob, onceJob: OnceJob, jobInfo: LinkisJobInfo, jobStateManager: JobStateManager): JobClient[LinkisJobInfo] = {
    if (!validateClientInfo(jobInfo)) {
      throw new FlinkJobLaunchErrorException(-1, "Param: [engineType, engineVersion] is necessary in job information", null)
    }
    val clientType = Option(jobInfo.getClientType).getOrElse(JobClientType.ATTACH)
    jobInfo match {
      case flinkJobInfo: FlinkJobInfo =>
        getJobClientFactory(clientType.toString)
          .createJobClient(job, onceJob, flinkJobInfo, jobStateManager)
          .asInstanceOf[JobClient[LinkisJobInfo]]
      case _ =>
        throw new FlinkJobLaunchErrorException(-1, "JobInfo should be a subclass of FlinkJobInfo", null)
    }
  }

  /**
   * Get jobClientFactory by different connectType
   *
   * @param connectType
   * @return
   */
  def getJobClientFactory(connectType: String): JobClientFactory = {
    connectType match {
      case "attach" => {
        if (null == this.engineConnJobClientFactory) {
          this.synchronized {
            if (null == this.engineConnJobClientFactory) {
              this.engineConnJobClientFactory = new EngineConnJobClientFactory
              this.engineConnJobClientFactory.init ()
            }
          }
        }
        this.engineConnJobClientFactory
      }
      case "detach" => {
        if (null == this.restJobClientFactory) {
          this.synchronized {
            if (null == this.restJobClientFactory) {
              this.restJobClientFactory = new RestJobClientFactory
              this.restJobClientFactory.init()
            }
          }
        }
        this.restJobClientFactory
      }
      case _ =>
        throw new FlinkJobLaunchErrorException(-1, "ConnectType on flinkJobInfo should be attach、detach or detach_standalone", null)
    }
  }
}

object AbstractJobClientFactory{

  /**
   * Store the job launch managers
   */
  private val flinkJobClientFactory = new AbstractJobClientFactory

  def getJobManager(): AbstractJobClientFactory = {
    flinkJobClientFactory
  }

}