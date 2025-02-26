package com.webank.wedatasphere.streamis.jobmanager.manager.transform.parser

import com.webank.wedatasphere.streamis.jobmanager.manager.constrants.JobConstrants.UNKNOWN_COMMENT

import java.util

import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJob
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.JobDetailsVo
import org.apache.linkis.common.utils.Utils
import org.springframework.stereotype.Component

import scala.collection.JavaConverters._

/**
 *
 * @date 2022-10-21
 * @author enjoyyin
 * @since 0.5.0
 */
@Component
class SparkTaskMetricsParser extends AbstractTaskMetricsParser {

  override protected def parse(metricsMap: util.Map[String, Object],
                               dataNumberDTOS: util.List[JobDetailsVo.DataNumberDTO],
                               loadConditionDTOs: util.List[JobDetailsVo.LoadConditionDTO],
                               realTimeTrafficDTOS: util.List[JobDetailsVo.RealTimeTrafficDTO]): Unit = {
    val addDataNumberDTO: String => Unit = key => {
      val batch = new JobDetailsVo.DataNumberDTO
      batch.setDataName(key)
      batch.setDataNumber(metricsMap.get(key) match {
        case null => -1
        case num => num.toString.toInt
      })
      dataNumberDTOS.add(batch)
    }
    addDataNumberDTO("waitingBatchs")
    addDataNumberDTO("runningBatchs")
    addDataNumberDTO("completedBatchs")
    metricsMap.get("executors") match {
      case executors: util.List[util.Map[String, AnyRef]] if !executors.isEmpty =>
        executors.asScala.foreach { executor =>
          val loadConditionDTO = new JobDetailsVo.LoadConditionDTO
          loadConditionDTO.setType(executor.get("type").asInstanceOf[String])
          loadConditionDTO.setHost(executor.get("host").asInstanceOf[String])
          loadConditionDTO.setMemory(executor.get("memory").asInstanceOf[String])
          loadConditionDTO.setTotalMemory(executor.get("totalMemory").asInstanceOf[String])
          loadConditionDTO.setGcLastTime(executor.get("gcLastTime").asInstanceOf[String])
          loadConditionDTO.setGcLastConsume(executor.get("gcLastConsume").asInstanceOf[String])
          loadConditionDTO.setGcTotalTime(executor.get("gcTotalTime").asInstanceOf[String])
          loadConditionDTOs.add(loadConditionDTO)
        }
      case _ =>
        val loadConditionDTO = new JobDetailsVo.LoadConditionDTO
        loadConditionDTO.setType("Driver")
        loadConditionDTO.setHost(UNKNOWN_COMMENT)
        loadConditionDTO.setMemory(UNKNOWN_COMMENT)
        loadConditionDTO.setTotalMemory(UNKNOWN_COMMENT)
        loadConditionDTO.setGcLastTime(UNKNOWN_COMMENT)
        loadConditionDTO.setGcLastConsume(UNKNOWN_COMMENT)
        loadConditionDTO.setGcTotalTime(UNKNOWN_COMMENT)
        loadConditionDTOs.add(loadConditionDTO)
    }
    val realTimeTrafficDTO = new JobDetailsVo.RealTimeTrafficDTO
    metricsMap.get("batchMetrics") match {
      case batchMetrics: util.List[util.Map[String, Object]] if !batchMetrics.isEmpty =>
        val batchMetric = batchMetrics.asScala.maxBy(_.get("batchTime").asInstanceOf[String])
        realTimeTrafficDTO.setSourceKey(metricsMap.getOrDefault("source", UNKNOWN_COMMENT).asInstanceOf[String])
        realTimeTrafficDTO.setSourceSpeed(batchMetric.get("inputRecords") + " Records")
        realTimeTrafficDTO.setTransformKey("processing")
        realTimeTrafficDTO.setSinkKey(metricsMap.getOrDefault("sink", UNKNOWN_COMMENT).asInstanceOf[String])
        val sinkSpeed = if (batchMetric.containsKey("totalDelay") && batchMetric.get("totalDelay") != null)
          Utils.msDurationToString(batchMetric.get("totalDelay").toString.toInt) + " totalDelay"
        else if (batchMetric.containsKey("taskExecuteTime") && batchMetric.get("taskExecuteTime") != null)
          Utils.msDurationToString(batchMetric.get("taskExecuteTime").toString.toInt) + " executeTime(Last Batch)"
        else UNKNOWN_COMMENT
        realTimeTrafficDTO.setSinkSpeed(sinkSpeed)
      case _ =>
        realTimeTrafficDTO.setSourceKey("<Unknown Source>")
        realTimeTrafficDTO.setSourceSpeed("<Unknown> Records/S")
        realTimeTrafficDTO.setTransformKey("<Unknown Transform>")
        realTimeTrafficDTO.setSinkKey("<Unknown Sink>")
        realTimeTrafficDTO.setSinkSpeed("<Unknown> Records/S")
    }
    realTimeTrafficDTOS.add(realTimeTrafficDTO)
  }

  override def canParse(streamJob: StreamJob): Boolean = streamJob.getJobType.startsWith("spark.")
}
