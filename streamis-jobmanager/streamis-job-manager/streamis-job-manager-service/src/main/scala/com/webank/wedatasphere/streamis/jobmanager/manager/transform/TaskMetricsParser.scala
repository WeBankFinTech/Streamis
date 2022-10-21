package com.webank.wedatasphere.streamis.jobmanager.manager.transform

import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJob
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.JobDetailsVo

/**
 *
 * @date 2022-10-21
 * @author enjoyyin
 * @since 0.5.0
 */
trait TaskMetricsParser {

  def canParse(streamJob: StreamJob): Boolean

  def parse(metrics: String): JobDetailsVo

}
