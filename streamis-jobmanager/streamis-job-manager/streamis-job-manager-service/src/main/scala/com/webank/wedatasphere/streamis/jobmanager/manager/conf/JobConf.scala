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

package com.webank.wedatasphere.streamis.jobmanager.manager.conf

import org.apache.linkis.common.conf.{CommonVars, TimeType}
import com.webank.wedatasphere.streamis.jobmanager.manager.exception.JobExecuteFailedErrorException


object JobConf {

  val STREAMIS_DEVELOPER = CommonVars("wds.streamis.developer", "enjoyyin")

  val JOBMANAGER_FLINK_JOB_STATUS_ZREO = CommonVars("wds.streamis.job.status.zero", 0,"Not Started")

  val JOBMANAGER_FLINK_JOB_STATUS_ONE = CommonVars("wds.streamis.job.status.one", 1,"Completed")

  val JOBMANAGER_FLINK_JOB_STATUS_TWO = CommonVars("wds.streamis.job.status.two", 2,"Wait for restart")

  val JOBMANAGER_FLINK_JOB_STATUS_THREE = CommonVars("wds.streamis.job.status.three", 3,"Alert running")

  val JOBMANAGER_FLINK_JOB_STATUS_FOUR = CommonVars("wds.streamis.job.status.four", 4,"Slow running")

  val JOBMANAGER_FLINK_JOB_STATUS_FIVE = CommonVars("wds.streamis.job.status.five", 5,"running")

  val JOBMANAGER_FLINK_JOB_STATUS_SIX = CommonVars("wds.streamis.job.status.six", 6,"Failed")

  val JOBMANAGER_FLINK_JOB_STATUS_SEVEN = CommonVars("wds.streamis.job.status.seven", 7,"Stopped")

  val STATUS_ARRAY = Array(JOBMANAGER_FLINK_JOB_STATUS_ONE, JOBMANAGER_FLINK_JOB_STATUS_TWO, JOBMANAGER_FLINK_JOB_STATUS_THREE,
    JOBMANAGER_FLINK_JOB_STATUS_FOUR, JOBMANAGER_FLINK_JOB_STATUS_FIVE, JOBMANAGER_FLINK_JOB_STATUS_SIX, JOBMANAGER_FLINK_JOB_STATUS_SEVEN)

  val NOT_COMPLETED_STATUS_ARRAY = Array(JOBMANAGER_FLINK_JOB_STATUS_TWO, JOBMANAGER_FLINK_JOB_STATUS_THREE, JOBMANAGER_FLINK_JOB_STATUS_FOUR,
    JOBMANAGER_FLINK_JOB_STATUS_FIVE)

  def isCompleted(status: Int): Boolean = status match {
    case 1 | 6 | 7 => true
    case _ => false
  }

  def isRunning(status: Int): Boolean = status match {
    case 2 | 3 | 4 | 5 => true
    case _ => false
  }

  def linkisStatusToStreamisStatus(status: String): Int = status.toLowerCase match {
    case "starting" | "unlock" | "locked" | "idle" | "busy" | "running" => JOBMANAGER_FLINK_JOB_STATUS_FIVE.getValue
    case "success" => JOBMANAGER_FLINK_JOB_STATUS_ONE.getValue
    case "failed" | "shuttingdown" => JOBMANAGER_FLINK_JOB_STATUS_SIX.getValue
  }

  def getStatusString(status: Int): String = STATUS_ARRAY.find(_.getValue == status).map(_.description)
    .getOrElse(throw new JobExecuteFailedErrorException(30351, s"Unknown status $status."))

  val TASK_MONITOR_INTERVAL = CommonVars("wds.streamis.task.monitor.interval", new TimeType("1m"))

  val TASK_SUBMIT_TIME_MAX = CommonVars("wds.streamis.task.submit.time.max", new TimeType("5m"))

}
