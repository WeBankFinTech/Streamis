package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.JobInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobStateInfo
import org.apache.linkis.computation.client.once.OnceJob

/**
 * @author jefftlin
 */
class FlinkRestJobClient (onceJob: OnceJob, jobInfo: JobInfo, stateManager: JobStateManager) extends AbstractRestJobClient(onceJob, jobInfo, stateManager){

  def getJobId(): String = null

  /**
   * Stop the job connected remote
   *
   * @param snapshot if do snapshot to save the job state
   */
  override def stop(snapshot: Boolean): JobStateInfo = {
    var stateInfo: JobStateInfo = null
    if (snapshot){
      // Begin to call the savepoint operator
      info(s"Trigger Savepoint operator for job [${jobInfo.getId}] before pausing job.")
      Option(triggerSavepoint()) match {
        case Some(savepoint) =>
          stateInfo = new JobStateInfo
          stateInfo.setLocation(savepoint.getLocation.toString)
          stateInfo.setTimestamp(savepoint.getTimestamp)
        case _ =>
      }
    }
    onceJob.kill()
    stateInfo
  }

  /**
   * Snapshot
   *
   * @return
   */
  def snapshot(): Boolean = {
    triggerSavepoint()
    true
  }

}
