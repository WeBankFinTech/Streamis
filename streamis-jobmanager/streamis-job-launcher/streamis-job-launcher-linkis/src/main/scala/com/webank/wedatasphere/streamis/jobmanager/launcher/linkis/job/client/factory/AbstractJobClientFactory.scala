package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client.factory

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.`type`.JobClientType
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.{JobClient, JobInfo}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.FlinkJobLaunchErrorException
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.jobInfo.{EngineConnJobInfo, LinkisJobInfo}
import org.apache.commons.lang3.StringUtils
import org.apache.linkis.common.utils.{Logging, Utils}
import org.apache.linkis.computation.client.once.OnceJob

import java.util.concurrent.TimeUnit
import scala.concurrent.TimeoutException
import scala.concurrent.duration.Duration

class AbstractJobClientFactory extends Logging {

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
  def createJobClient(onceJob: OnceJob, jobInfo: JobInfo, jobStateManager: JobStateManager): JobClient[LinkisJobInfo] = {
    if (!validateClientInfo(jobInfo)) {
      throw new FlinkJobLaunchErrorException(-1, "Param: [engineType, engineVersion] is necessary in job information", null)
    }
    val clientType = Option(jobInfo.getClientType).getOrElse(JobClientType.ATTACH)
    val client = getJobClientFactory(clientType)
      .createJobClient(onceJob, jobInfo, jobStateManager)
      .asInstanceOf[JobClient[LinkisJobInfo]]
    Utils.tryThrow {
      Utils.waitUntil(() => {
        client.getJobInfo(true).asInstanceOf[EngineConnJobInfo].getApplicationId != null
      }, Duration(10, TimeUnit.SECONDS), 100, 1000)
      client
    } {
      case t: TimeoutException => {
        logger.warn("Timeout to launch job, cannot get applicationId after deployment")
        // Downgraded to yarn call
        null
      }
    }
  }

  /**
   * Get jobClientFactory by different connectType
   *
   * @param connectType
   * @return
   */
  def getJobClientFactory(connectType: JobClientType.Value): JobClientFactory = {
    connectType match {
      case JobClientType.ATTACH => {
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
      case JobClientType.DETACH => {
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