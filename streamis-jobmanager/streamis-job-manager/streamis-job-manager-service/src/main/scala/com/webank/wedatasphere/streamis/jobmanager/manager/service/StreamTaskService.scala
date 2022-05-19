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

import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LogRequestPayload
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.FlinkJobInfo
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamTask
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.{JobProgressVo, StreamTaskListVo}

import java.util
import java.util.concurrent.Future
/**
 * Include the method related by stream task (such as execute/pause)
 */
trait StreamTaskService {

  /**
   * Sync to execute job(task)
   * 1) create a new task
   * 2) launch the new task
   * @param jobId job id
   * @param taskId task id
   * @param execUser user name
   * @param restore restore from job state
   */
   def execute(jobId: Long, taskId: Long, execUser: String, restore: Boolean = false): Unit

   def asyncExecute(jobId: Long, taskId: Long, execUser: String, restore: Boolean = false): Future[String]

  /**
   * Sync to pause job(task)
   * @param jobId job id
   * @param taskId task id
   * @param operator user name
   */
   def pause(jobId: Long, taskId: Long, operator: String): Unit

   def asyncPause(jobId: Long, taskId: Long, operator: String): Future[_]

  /**
   * Just launch task by task id
   * @param taskId task id
   */
   def launch(taskId: Long, execUser: String): Unit

  /**
   * Create new task use the latest job version
   * @param jobId job id
   * @param status init status
   * @param creator creator
   */
   def createTask(jobId: Long, status: Int, creator: String): StreamTask

  /**
   * Update the status of latest task by job id
   * @param jobId job id
   * @param status status code
   */
   def updateTaskStatus(jobId: Long, status: Int) : Unit
  /**
   * Query the task history list
   * @param jobId job id
   * @param version version
   * @return
   */
   def queryHistory(jobId: Long, version: String): util.List[StreamTaskListVo]

  /**
   * Get realtime log
   * @param jobId job id
   * @param operator user name
   * @param requestPayload request payload
   * @return
   */
   def getRealtimeLog(jobId: Long, operator: String, requestPayload: LogRequestPayload): util.Map[String, Any]

  /**
   * Fetch job status(the status of latest task)
   * @param jobId job id
   * @param version version
   * @return
   */
   def getByJobStatus(jobId: Long, version: String): JobProgressVo

  /**
   * Get latest task info by job id and version number
   * @param jobId job id
   * @param version version
   * @return
   */
   def getTask(jobId: Long, version: String): FlinkJobInfo
}
