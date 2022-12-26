package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client.factory

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.`type`.ConnectType
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.{JobClient, LaunchJob}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.FlinkJobLaunchErrorException
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.{FlinkJobInfo, LinkisJobInfo}
import org.apache.linkis.computation.client.once.OnceJob

class AbstractJobClientFactory {

  var engineConnJobClientFactory: EngineConnJobClientFactory = _

  var restJobClientFactory: RestJobClientFactory = _

  def validateLaunchJob(job: LaunchJob): Boolean = {
    job.getLabels.containsKey("engineType") && job.getLabels.containsKey("linkisVersion") && job.getLabels.containsKey("connectType")
  }

  /**
   * Create job client
   *
   * @param onceJob once job
   * @param jobInfo job info
   * @return
   */
  def createJobClient(job: LaunchJob, onceJob: OnceJob, jobInfo: LinkisJobInfo, jobStateManager: JobStateManager): JobClient[LinkisJobInfo] = {
    if (!validateLaunchJob(job)) {
      throw new FlinkJobLaunchErrorException(-1, "LaunchJob should have labels with keys engineType, engineVersion and connectType", null)
    }
    jobInfo match {
      case flinkJobInfo: FlinkJobInfo =>
        getJobClientFactory(job.getLabels.get("connectType").toString)
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