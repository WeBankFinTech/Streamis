package com.webank.wedatasphere.streamis.jobmanager.manager.transform.parser

import java.util

import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJob
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.JobDetailsVo
import org.springframework.stereotype.Component

/**
 *
 * @date 2022-10-21
 * @author enjoyyin
 * @since 0.5.0
 */
@Component
class FlinkTaskMetricsParser extends AbstractTaskMetricsParser {

  override def canParse(streamJob: StreamJob): Boolean = streamJob.getJobType.startsWith("flink.")

  override def parse(metricsMap: util.Map[String, Object],
                     dataNumberDTOS: util.List[JobDetailsVo.DataNumberDTO],
                     loadConditionDTOs: util.List[JobDetailsVo.LoadConditionDTO],
                     realTimeTrafficDTOS: util.List[JobDetailsVo.RealTimeTrafficDTO]): Unit = {
    // TODO This is just sample datas, waiting for it completed. We have planned it to a later release, welcome all partners to join us to realize this powerful feature.
    val dataNumberDTO = new JobDetailsVo.DataNumberDTO
    dataNumberDTO.setDataName("kafka topic")
    dataNumberDTO.setDataNumber(109345)
    dataNumberDTOS.add(dataNumberDTO)

    val loadConditionDTO = new JobDetailsVo.LoadConditionDTO
    loadConditionDTO.setType("jobManager")
    loadConditionDTO.setHost("localhost")
    loadConditionDTO.setMemory("1.5")
    loadConditionDTO.setTotalMemory("2.0")
    loadConditionDTO.setGcLastTime("2020-08-01")
    loadConditionDTO.setGcLastConsume("1")
    loadConditionDTO.setGcTotalTime("2min")
    loadConditionDTOs.add(loadConditionDTO)

    val realTimeTrafficDTO = new JobDetailsVo.RealTimeTrafficDTO
    realTimeTrafficDTO.setSourceKey("kafka topic")
    realTimeTrafficDTO.setSourceSpeed("100 Records/S")
    realTimeTrafficDTO.setTransformKey("transform")
    realTimeTrafficDTO.setSinkKey("hbase key")
    realTimeTrafficDTO.setSinkSpeed("10 Records/S")
    realTimeTrafficDTOS.add(realTimeTrafficDTO)
  }
}
