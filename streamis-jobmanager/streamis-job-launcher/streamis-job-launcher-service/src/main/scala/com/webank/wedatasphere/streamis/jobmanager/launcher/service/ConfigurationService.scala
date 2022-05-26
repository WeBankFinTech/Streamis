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

package com.webank.wedatasphere.streamis.jobmanager.launcher.service


import org.apache.linkis.common.utils.Logging
import com.webank.wedatasphere.streamis.jobmanager.launcher.conf.JobConfConstants
import com.webank.wedatasphere.streamis.jobmanager.launcher.dao.ConfigMapper
import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.dto.ConfigKeyValueDTO
import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.vo.{JobConfValueSet, ConfigRelationVO}
import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.{ConfigKeyValue, JobUserRole}
import com.webank.wedatasphere.streamis.jobmanager.launcher.exception.ConfigurationException
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.StreamJobMapper
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.{CollectionUtils, StringUtils}

import scala.collection.JavaConverters._


@Service
class ConfigurationService extends Logging {

  @Autowired private var configMapper: ConfigMapper = _

  @Autowired private var streamJobMapper: StreamJobMapper = _

  @Transactional(rollbackFor = Array(classOf[Exception]))
  def addKeyValue(vo: JobConfValueSet): Unit = {

    val job = streamJobMapper.getJobById(vo.getJobId)
    if (job == null) throw new ConfigurationException(s"no such job,jobId is : ${vo.getJobId} ")

    //Delete all KeyValue Rules under current jobId(删除该jibId下所有的KeyValue规则)
    configMapper.deleteKeyValue(vo.getJobId)
    if (!CollectionUtils.isEmpty(vo.getResourceConfig)){
      vo.getResourceConfig.asScala.foreach(f => {
        val keyValue = new ConfigKeyValue()
        keyValue.setConfigkeyId(f.getConfigkeyId)
        keyValue.setConfigKey(f.getKey)
        keyValue.setJobId(vo.getJobId)
        keyValue.setConfigValue(f.getValue)
        keyValue.setJobName(job.getName)
        keyValue.setType(JobConfConstants.JOBMANAGER_FLINK_RESOURCE.getValue)
        configMapper.insertValue(keyValue)
      })
    }

    if (!CollectionUtils.isEmpty(vo.getProduceConfig)){
      vo.getProduceConfig.asScala.foreach(f => {
        val keyValue = new ConfigKeyValue()
        keyValue.setConfigkeyId(f.getConfigkeyId)
        keyValue.setConfigKey(f.getKey)
        keyValue.setJobId(vo.getJobId)
        keyValue.setConfigValue(f.getValue)
        keyValue.setJobName(job.getName)
        keyValue.setType(JobConfConstants.JOBMANAGER_FLINK_PRODUCE.getValue)
        configMapper.insertValue(keyValue)
      })
    }

    if (!CollectionUtils.isEmpty(vo.getAlarmConfig)) {
      vo.getAlarmConfig.asScala.groupBy(_.getConfigkeyId).map(_._2.head).foreach(f => {
        if (JobConfConstants.JOB_CONF_ALERT_RULE.getValue.equals(f.getKey) && !StringUtils.isEmpty(f.getValue)) {
          val strings: Array[String] = f.getValue.split(",")
          for (i <- strings.indices) {
            val value = new ConfigKeyValue()
            value.setConfigkeyId(f.getConfigkeyId)
            value.setConfigKey(f.getKey)
            value.setConfigValue(strings(i))
            value.setJobId(vo.getJobId)
            value.setJobName(job.getName)
            value.setType(JobConfConstants.JOBMANAGER_FLINK_ALERT.getValue)
            configMapper.insertValue(value)
          }
        } else {
          val value = new ConfigKeyValue()
          value.setConfigkeyId(f.getConfigkeyId)
          value.setConfigKey(f.getKey)
          value.setConfigValue(f.getValue)
          value.setJobId(vo.getJobId)
          value.setJobName(job.getName)
          value.setType(JobConfConstants.JOBMANAGER_FLINK_ALERT.getValue)
          configMapper.insertValue(value)
        }
      })
    }


    if (!CollectionUtils.isEmpty(vo.getParameterConfig)) {
      vo.getParameterConfig.asScala.foreach(f => {
        val keyValue = new ConfigKeyValue()
        keyValue.setConfigkeyId(f.getConfigkeyId)
        keyValue.setConfigKey(f.getKey)
        keyValue.setJobId(vo.getJobId)
        keyValue.setConfigValue(f.getValue)
        keyValue.setJobName(job.getName)
        keyValue.setType(JobConfConstants.JOBMANAGER_FLINK_CUSTOM.getValue)
        configMapper.insertValue(keyValue)
      })
    }
    //Authorization(权限)
    vo.getPermissionConfig.asScala.foreach(f => {
      val keyValue = new ConfigKeyValue()
      keyValue.setConfigKey(f.getKey)
      keyValue.setJobId(vo.getJobId)
      keyValue.setConfigValue(f.getValue)
      keyValue.setType(JobConfConstants.JOBMANAGER_FLINK_AUTHORITY.getValue)
      keyValue.setConfigkeyId(f.getConfigkeyId)
      keyValue.setJobName(job.getName)
      configMapper.insertValue(keyValue)
    })

  }


  def getFullTree(jobId: Long): JobConfValueSet = {

    val configValues = configMapper.getConfigKeyValues(null, jobId).asScala

    var dtos: List[ConfigKeyValueDTO] = null
    if (configValues == null || configValues.isEmpty) {
      val configKeys = configMapper.getConfigKey.asScala
      dtos = configKeys.filter(f => f.getSort != 0).map(m => {
        val dto = new ConfigKeyValueDTO()
        BeanUtils.copyProperties(m, dto)
        dto.setConfigkeyId(m.getId)
        dto
      }).toList
    } else {
      dtos = configValues.filter(f => f.getSort != 0).map(m => {
        val dto = new ConfigKeyValueDTO()
        BeanUtils.copyProperties(m, dto)
        dto.setValue(m.getConfigValue)
        dto.setConfigkeyId(m.getId)
        dto
      }).toList
    }

    val vo = configKeyValueToVO(jobId, dtos)
    val jobKeyTypes = configMapper.getConfigKeyValues(JobConfConstants.JOBMANAGER_FLINK_CUSTOM.getValue, jobId)
    if (jobKeyTypes != null && jobKeyTypes.size() > 0) {
      vo.setParameterConfig(jobKeyTypes.asScala.map(m => {
        val vo = new ConfigRelationVO()
        vo.setConfigkeyId(m.getConfigkeyId)
        vo.setKey(m.getConfigKey)
        vo.setName(m.getName)
        vo.setValue(m.getConfigValue)
        vo
      }).asJava)
    } else {
      val configKeys = configMapper.getConfigKey.asScala
      vo.setParameterConfig(configKeys.groupBy(_.getType).filter(f => f._1.equals(JobConfConstants.JOBMANAGER_FLINK_CUSTOM.getValue)).flatMap(_._2)
      .toList.map(m => {
        val vo = new ConfigRelationVO()
        vo.setConfigkeyId(m.getId)
        vo.setKey(m.getKey)
        vo.setName(m.getName)
        vo.setValue(m.getDefaultValue)
        vo
      }).asJava)
    }
    vo.setJobId(jobId)
    vo
  }


  private def configKeyValueToVO(jobId: Long, dtos: List[ConfigKeyValueDTO]): JobConfValueSet = {
    val statusTwo = JobConfConstants.JOBMANAGER_FLINK_CUSTOM_STATUS_TWO.getValue
    val configVO = new JobConfValueSet()

    val groupDDs = dtos.groupBy(_.getType).map(m => {
      (m._1, m._2.sortBy(_.getSort).map(p => {
        val vo = new ConfigRelationVO()
        BeanUtils.copyProperties(p, vo)

        if (p.getStatus.equals(statusTwo)) {
          val lists = p.getDefaultValue.split(",").map(f => {
            if (p.getKey.equals(JobConfConstants.JOB_CONF_ALERT_RULE.getValue) && p.getValue != null) {
              val s = p.getValue.split(",")
              if (s.contains(f)) new ConfigRelationVO.ValueList(f, true)
              else new ConfigRelationVO.ValueList(f, false)
            }
            else if (p.getValue != null && f.trim.equals(p.getValue)) new ConfigRelationVO.ValueList(f, true)
            else new ConfigRelationVO.ValueList(f, false)
          })
          vo.setValueLists(lists.toList.asJava)
        }
        vo
      }))
    })

    configVO.setResourceConfig(groupDDs.filter(f => f._1.equals(JobConfConstants.JOBMANAGER_FLINK_RESOURCE.getValue)).flatMap(_._2).toList.asJava)
    configVO.setProduceConfig(groupDDs.filter(f => f._1.equals(JobConfConstants.JOBMANAGER_FLINK_PRODUCE.getValue)).flatMap(_._2).toList.asJava)
    configVO.setAlarmConfig(groupDDs.filter(f => f._1.equals(JobConfConstants.JOBMANAGER_FLINK_ALERT.getValue)).flatMap(_._2).toList.asJava)
    configVO.setParameterConfig(groupDDs.filter(f => f._1.equals(JobConfConstants.JOBMANAGER_FLINK_CUSTOM.getValue)).flatMap(_._2).toList.asJava)
    configVO.setPermissionConfig(groupDDs.filter(f => f._1.equals(JobConfConstants.JOBMANAGER_FLINK_AUTHORITY.getValue)).flatMap(_._2).toList.asJava)
    configVO
  }


}
