package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.FlinkJobInfo
import org.apache.linkis.computation.client.once.OnceJob

/**
 * @author jefftlin
 */
abstract class YarnRestJobClient(onceJob: OnceJob, override var jobInfo: FlinkJobInfo, stateManager: JobStateManager) extends AbstractRestJobClient(onceJob, jobInfo, stateManager) {



}
