package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobStateInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.FlinkSavePointException
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.FlinkJobInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.operator.FlinkTriggerSavepointOperator
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state.Savepoint
import org.apache.linkis.common.utils.Utils
import org.apache.linkis.computation.client.once.OnceJob

/**
 * @author jefftlin
 */
class FlinkRestJobClient (onceJob: OnceJob, override var jobInfo: FlinkJobInfo, stateManager: JobStateManager) extends AbstractRestJobClient(onceJob, jobInfo, stateManager){


  def getJobId(): String = null

  /**
   * Stop the job connected remote
   *
   * @param snapshot if do snapshot to save the job state
   */
  override def stop(snapshot: Boolean): JobStateInfo = {
    val stateInfo = super.stop(snapshot)

    var stateInfo2 = new JobStateInfo
    if (snapshot){
      //todo Patch {applicationUrl}/{job}/{jobId}
    }
    stateInfo2
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

  /**
   * Trigger save point operation with savePointDir and mode
   *
   * @param savePointDir savepoint directory
   * @param mode         mode
   */
  override def triggerSavepoint(savePointDir: String, mode: String): Savepoint = {
    Utils.tryCatch{
      onceJob.getOperator(FlinkTriggerSavepointOperator.OPERATOR_NAME) match{
        case savepointOperator: FlinkTriggerSavepointOperator => {
          // TODO Get scheme information from job info
          savepointOperator.setSavepointDir(savePointDir)
          savepointOperator.setMode(mode)
          Option(savepointOperator()) match {
            case Some(savepoint: Savepoint) =>
              savepoint
            // TODO store into job Info
            case _ => throw new FlinkSavePointException(-1, "The response savepoint info is empty", null)
          }
        }
      }
    }{
      case se: FlinkSavePointException =>
        throw se
      case e: Exception =>
        throw new FlinkSavePointException(30501, "Fail to trigger savepoint operator", e)
    }
  }
}
