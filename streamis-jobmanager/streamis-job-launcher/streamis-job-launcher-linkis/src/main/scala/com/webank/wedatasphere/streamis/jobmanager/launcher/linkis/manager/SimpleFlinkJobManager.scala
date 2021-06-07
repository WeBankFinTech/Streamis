package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.manager
import com.webank.wedatasphere.linkis.computation.client.once.simple.SimpleOnceJob
import com.webank.wedatasphere.linkis.computation.client.once.{OnceJob, SubmittableOnceJob}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.LinkisJobManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.{FlinkJobInfo, LaunchJob, LinkisJobInfo}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.FlinkJobLaunchErrorException

/**
 *
 * @date 2021-06-05
 * @author enjoyyin
 * @since 0.5.0
 */
class SimpleFlinkJobManager extends FlinkJobManager {

  override def getName: String = LinkisJobManager.SIMPLE_FLINK

  protected def buildOnceJob(job: LaunchJob): SubmittableOnceJob = SimpleOnceJob.builder().addExecuteUser(job.getSubmitUser).setLabels(job.getLabels)
    .setJobContent(job.getJobContent).setParams(job.getParams).setSource(job.getSource).build()

  override protected def createSubmittedOnceJob(id: String, user: String): OnceJob = SimpleOnceJob.build(id, user)

  override protected def getStatus(id: String, user: String): String = getOnceJob(id, user) match {
    case simpleOnceJob: SimpleOnceJob =>
      if(simpleOnceJob.isCompleted) deleteOnceJob(id)
      simpleOnceJob.getStatus
  }

  override def getCheckpoints(id: String, user: String): LinkisJobInfo = throw new FlinkJobLaunchErrorException(30401, "Not support method")

  override def triggerSavepoint(id: String, user: String): LinkisJobInfo = throw new FlinkJobLaunchErrorException(30401, "Not support method")

  override def createJobInfo(id: String, user: String): LinkisJobInfo = {
    val jobInfo = new FlinkJobInfo
    jobInfo.setId(id)
    jobInfo.setUser(user)
    jobInfo.setApplicationId()
    jobInfo.setApplicationUrl()
    jobInfo.setResources()
    jobInfo.setLogPath()
    jobInfo
  }
}
