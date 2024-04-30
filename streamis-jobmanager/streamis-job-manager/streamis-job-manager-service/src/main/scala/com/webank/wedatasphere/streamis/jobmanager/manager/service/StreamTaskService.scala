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

package com.webank.wedatasphere.streamis.jobmanager.manager.service

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobState
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LogRequestPayload
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.jobInfo.EngineConnJobInfo
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.{JobTemplateFiles, StreamJob, StreamJobVersion, StreamTask}
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.{ExecResultVo, JobDetailsVo, JobProgressVo, JobStatusVo, PauseResultVo, StreamTaskListVo, StreamTaskPageInfo}
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity.RealtimeLogEntity

import java.util
import java.util.concurrent.Future

/**
 * Include the method related by stream task (such as execute/pause)
 */
trait StreamTaskService {


  def getTaskById(Id: Long): StreamTask

  def getTaskStatusById(jobId: Long): JobStatusVo
  /**
   * Sync to execute job(task)
   * 1) create a new task
   * 2) launch the new task
   *
   * @param jobId    job id
   * @param taskId   task id
   * @param execUser user name
   * @param restore  restore from job state
   */
  def execute(jobId: Long, taskId: Long, execUser: String, restore: Boolean = false): Unit

  def execute(jobId: Long, taskId: Long, execUser: String): Unit

  /**
   * Async to execute job(task)
   *
   * @param jobId    job id
   * @param taskId   task id
   * @param execUser user name
   * @param restore  restore from job state
   * @return
   */
  def asyncExecute(jobId: Long, taskId: Long, execUser: String, restore: Boolean = false): Future[String]

  def asyncExecute(jobId: Long, taskId: Long, execUser: String): Future[String]

  /**
   * Bulk executing
   *
   * @param jobIds   jobIds
   * @param taskIds  taskIds
   * @param execUser execUser
   * @param restore  restore from job state
   */
  def bulkExecute(jobIds: util.List[Long], taskIds: util.List[Long], execUser: String, restore: Boolean = false): util.List[ExecResultVo]

  def bulkExecute(jobIds: util.List[Long], taskIds: util.List[Long], execUser: String): util.List[ExecResultVo]

  /**
   * Sync to pause job(task)
   *
   * @param jobId    job id
   * @param taskId   task id
   * @param operator user name
   */
  def pause(jobId: Long, taskId: Long, operator: String, snapshot: Boolean, skipHookError: Boolean): PauseResultVo

  def asyncPause(jobId: Long, taskId: Long, operator: String, snapshot: Boolean, skipHookError: Boolean): Future[PauseResultVo]

  /**
   * Bulk pausing
   *
   * @param jobIds   jobIds
   * @param taskIds  taskIds
   * @param operator operator
   * @param snapshot snapshot
   * @return
   */
  def bulkPause(jobIds: util.List[Long], taskIds: util.List[Long], operator: String, snapshot: Boolean, skipHookError: Boolean): util.List[PauseResultVo]

  /**
   * Just launch task by task id
   *
   * @param taskId task id
   */
  def launch(taskId: Long, execUser: String): Unit

  def getLatestTaskByJobId(jobId: Long): StreamTask

  /**
   * Create new task use the latest job version
   *
   * @param jobId   job id
   * @param status  init status
   * @param creator creator
   */
  def createTask(jobId: Long, status: Int, creator: String): StreamTask

  def updateTask(streamTask: StreamTask): Unit

  /**
   * Update the task status
   *
   * @param jobId  job id
   * @param status status code
   * @return task id of latest task
   */
  def transitionTaskStatus(jobId: Long, taskId: Long, status: Int): Long

  /**
   * Query the task history list
   *
   * @param jobId   job id
   * @param version version
   * @return
   */
  def queryHistory(jobId: Long, version: String, pageNow: Long, pageSize: Long): StreamTaskPageInfo


  def queryErrorCode(jobId: Long): StreamTask

  /**
   * Get realtime log
   *
   * @param jobId          job id
   * @param operator       user name
   * @param requestPayload request payload
   * @return
   */
  def getRealtimeLog(jobId: Long, taskId: Long, operator: String, requestPayload: LogRequestPayload): RealtimeLogEntity

  /**
   * Do snapshot
   *
   * @param jobId    job id
   * @param taskId   task id
   * @param operator operator
   * @return snapshot url
   */
  def snapshot(jobId: Long, taskId: Long, operator: String): String

  /**
   * Fetch the progress(job progress/the progress of latest task) by job id and version
   *
   * @param jobId   job id
   * @param version version
   * @return
   */
  def getProgress(jobId: Long, version: String): JobProgressVo

  /**
   * Fetch the status list by job id list
   *
   * @param jobIds job ids
   */
  def getStatusList(jobIds: util.List[Long]): util.List[JobStatusVo]

  /**
   * Get latest task info by job id and version number
   *
   * @param jobId   job id
   * @param version version
   * @return
   */
  def getTaskJobInfo(jobId: Long, version: String): EngineConnJobInfo


  def getStateInfo(taskId: Long): JobState

  def getStateInfo(streamTask: StreamTask): JobState

  def getJobDetailsVO(streamJob: StreamJob, version: String): JobDetailsVo

  def errorCodeMatching(jobId: Long, streamTask: StreamTask): Future[_]

  def errorCodeMatchException(exception: String): (String,String)

  def generateJobStartConfig(job: StreamJob, jobVersion: StreamJobVersion, creator: String,jobTemplate: JobTemplateFiles): String

  def generateJobTemplate(jobTemplate: JobTemplateFiles): String

  def getLatestByJobId(jobId: Long ):StreamTask
}
