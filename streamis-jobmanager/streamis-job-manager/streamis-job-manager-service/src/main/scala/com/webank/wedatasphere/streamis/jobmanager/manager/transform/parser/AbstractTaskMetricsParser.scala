package com.webank.wedatasphere.streamis.jobmanager.manager.transform.parser

import java.util

import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.JobDetailsVo
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.TaskMetricsParser
import org.apache.commons.lang3.StringUtils
import org.apache.linkis.httpclient.dws.DWSHttpClient

/**
 *
 * @date 2022-10-21
 * @author enjoyyin
 * @since 0.5.0
 */
trait AbstractTaskMetricsParser extends TaskMetricsParser {

  override def parse(metrics: String): JobDetailsVo = {
    val jobDetailsVO = new JobDetailsVo
    val dataNumberDTOS = new util.ArrayList[JobDetailsVo.DataNumberDTO]
    val loadConditionDTOs = new util.ArrayList[JobDetailsVo.LoadConditionDTO]
    val realTimeTrafficDTOS = new util.ArrayList[JobDetailsVo.RealTimeTrafficDTO]
    jobDetailsVO.setDataNumber(dataNumberDTOS)
    jobDetailsVO.setLoadCondition(loadConditionDTOs)
    jobDetailsVO.setRealTimeTraffic(realTimeTrafficDTOS)
    val metricsMap = if(StringUtils.isNotBlank(metrics)) DWSHttpClient.jacksonJson.readValue(metrics, classOf[util.Map[String, Object]])
      else new util.HashMap[String, Object](0)
    parse(metricsMap, dataNumberDTOS, loadConditionDTOs, realTimeTrafficDTOS)
    jobDetailsVO
  }

  protected def parse(metricsMap: util.Map[String, Object],
                       dataNumberDTOS: util.List[JobDetailsVo.DataNumberDTO],
                      loadConditionDTOs: util.List[JobDetailsVo.LoadConditionDTO],
                      realTimeTrafficDTOS: util.List[JobDetailsVo.RealTimeTrafficDTO]): Unit

}
