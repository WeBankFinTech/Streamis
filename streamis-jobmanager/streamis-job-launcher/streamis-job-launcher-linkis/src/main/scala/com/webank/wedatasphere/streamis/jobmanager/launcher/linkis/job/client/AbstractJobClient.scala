/*
 * Copyright 2021 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.JobClient
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobStateInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.conf.JobLauncherConfiguration
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.FlinkJobInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state.Savepoint
import org.apache.linkis.common.utils.Logging
import org.apache.linkis.computation.client.once.simple.SimpleOnceJob
import org.apache.linkis.computation.client.once.OnceJob

/**
 * @author jefftlin
 */
abstract class AbstractJobClient(onceJob: OnceJob, var jobInfo: FlinkJobInfo, stateManager: JobStateManager)
  extends JobClient[FlinkJobInfo] with Logging{

  /**
   * Refresh job info and return
   *
   * @return
   */
  override def getJobInfo: FlinkJobInfo = getJobInfo(false)

  /**
   * Refresh job info and return
   *
   * @param refresh refresh
   * @return
   */
  override def getJobInfo(refresh: Boolean): FlinkJobInfo = {
    onceJob match {
      case simpleOnceJob: SimpleOnceJob =>
        simpleOnceJob.getStatus
        jobInfo.setStatus(if (refresh) onceJob.getNodeInfo
          .getOrDefault("nodeStatus", simpleOnceJob.getStatus).asInstanceOf[String] else simpleOnceJob.getStatus)
    }
    jobInfo
  }

  /**
   * Stop directly
   */
  override def stop(): Unit = stop(false)

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
   * Trigger save point operation
   *
   * @return
   */
  def triggerSavepoint(): Savepoint = {
    val savepointURI = this.stateManager.getJobStateDir(classOf[Savepoint], jobInfo.getName)
    triggerSavepoint(savepointURI.toString, JobLauncherConfiguration.FLINK_TRIGGER_SAVEPOINT_MODE.getValue)
  }

  /**
   * Trigger save point operation with savePointDir and mode
   *
   * @param savePointDir savepoint directory
   * @param mode mode
   */
  def triggerSavepoint(savePointDir: String, mode: String): Savepoint
}
