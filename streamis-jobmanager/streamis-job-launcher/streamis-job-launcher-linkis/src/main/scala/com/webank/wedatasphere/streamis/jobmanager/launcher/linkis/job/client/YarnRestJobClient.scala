package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.JobInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import org.apache.linkis.computation.client.once.OnceJob

/**
 * @author jefftlin
 */
abstract class YarnRestJobClient(onceJob: OnceJob, jobInfo: JobInfo, stateManager: JobStateManager) extends AbstractRestJobClient(onceJob, jobInfo, stateManager) {



}
