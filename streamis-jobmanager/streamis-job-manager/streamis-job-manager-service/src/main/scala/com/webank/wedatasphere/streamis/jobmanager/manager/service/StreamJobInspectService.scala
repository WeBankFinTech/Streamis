package com.webank.wedatasphere.streamis.jobmanager.manager.service
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.JobInspectVo

import java.util

trait StreamJobInspectService {
  /**
   * Inspect method
   * @param jobId job id
   * @param types type list for inspecting
   * @return
   */
  def inspect(jobId: Long, types: Array[JobInspectVo.Types]): util.List[JobInspectVo]
}
