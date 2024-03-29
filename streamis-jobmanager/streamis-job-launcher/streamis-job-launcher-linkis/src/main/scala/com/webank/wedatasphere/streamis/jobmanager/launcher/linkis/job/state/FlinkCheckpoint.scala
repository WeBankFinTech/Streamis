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

package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.{JobGenericState, JobState}

/**
 * Hold the check point information
 */
class FlinkCheckpoint(location: String) extends JobGenericState(location) with JobState {

  /**
   * Record the sequence of checkpoint
   */
  private var order: Long = -1

  def setOrder(order: Long): Unit = {
    this.order = order
  }

  def getOrder: Long = {
    this.order
  }
}
