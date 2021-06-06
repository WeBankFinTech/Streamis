package com.webank.wedatasphere.streamis.jobmanager.manager.service

import java.util

import com.webank.wedatasphere.linkis.common.utils.Logging
import com.webank.wedatasphere.streamis.jobmanager.manager.conf.JobConf
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.{StreamJobMapper, StreamTaskMapper}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.convert.WrapAsScala._

/**
 *
 * @date 2021-06-06
 * @author enjoyyin
 * @since 0.5.0
 */
@Service
class TaskMonitorService extends Logging {

  @Autowired private var streamTaskMapper:StreamTaskMapper=_
  @Autowired private var streamJobMapper:StreamJobMapper=_

  def monitor(): Unit = {
    info("Try to update all StreamTasks status.")
    val streamTasks = streamTaskMapper.getTasksByStatus(util.Arrays.asList(JobConf.NOT_COMPLETED_STATUS_ARRAY.map(_.getValue)))
    if(streamTasks == null || streamTasks.isEmpty) {
      info("No StreamTasks is running, return...")
      return
    }
    //TODO StreamTask should add a lastUpdateTime field, so we can filter it by lastUpdateTime,
    // some updated ones are not necessary to update it again in a short interval.
    streamTasks.foreach { streamTask =>
      streamTask.getJobVersionId

    }
  }

}
