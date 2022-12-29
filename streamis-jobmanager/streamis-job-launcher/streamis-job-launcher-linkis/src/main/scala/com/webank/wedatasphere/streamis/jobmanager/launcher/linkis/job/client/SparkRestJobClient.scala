package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobStateInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.FlinkSavePointException
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.FlinkJobInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.operator.FlinkTriggerSavepointOperator
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state.FlinkSavepoint
import org.apache.linkis.common.utils.Utils
import org.apache.linkis.computation.client.once.OnceJob

/**
 * @author jefftlin
 */
class SparkRestJobClient(onceJob: OnceJob, jobInfo: FlinkJobInfo, stateManager: JobStateManager) extends YarnRestJobClient(onceJob, jobInfo, stateManager) {

  /**
   * Stop the job connected remote
   *
   * @param snapshot if do snapshot to save the job state
   * @return return the jobState info (if use snapshot) else return null
   */
  override def stop(snapshot: Boolean): JobStateInfo = ???

  /**
   * Trigger save point operation with savePointDir and mode
   *
   * @param savePointDir savepoint directory
   * @param mode         mode
   */
  override def triggerSavepoint(savePointDir: String, mode: String): FlinkSavepoint = {
    Utils.tryCatch{
      onceJob.getOperator(FlinkTriggerSavepointOperator.OPERATOR_NAME) match{
        case savepointOperator: FlinkTriggerSavepointOperator => {
          // TODO Get scheme information from job info
          savepointOperator.setSavepointDir(savePointDir)
          savepointOperator.setMode(mode)
          Option(savepointOperator()) match {
            case Some(savepoint: FlinkSavepoint) =>
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
