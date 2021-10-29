package com.webank.wedatasphere.streamis.jobmanager.launcher.service

import com.webank.wedatasphere.linkis.common.utils.Logging
import com.webank.wedatasphere.streamis.jobmanager.launcher.conf.ConfigConf
import com.webank.wedatasphere.streamis.jobmanager.launcher.dao.{ConfigMapper, JobUserRoleMapper}
import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.dto.ConfigKeyValueDTO
import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.{ConfigKeyValue, JobUserRole}
import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.vo.{ConfigKeyVO, ConfigRelationVO}
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.util
import scala.collection.JavaConverters._
/**
 * @author limeng
 */
@Service
class ConfigurationService extends Logging{

  @Autowired private var configMapper:ConfigMapper = _

  @Autowired private var jobUserRoleMapper:JobUserRoleMapper= _

  def addKeyValue(vo:ConfigKeyVO): Unit ={

      val baseList = vo.getResourceConfig.asScala.++=(vo.getAlarmConfig.asScala).++=(vo.getProduceConfig.asScala)

      baseList.foreach(f=>{
        val configKeyValue = configMapper.getConfigKeyValue(vo.getJobId, f.getConfigkeyId)
        if(configKeyValue != null){
          configMapper.deleteKeyValue(vo.getJobId,null,f.getConfigkeyId)
        }

        val value = new ConfigKeyValue()
        value.setConfigkeyId(f.getConfigkeyId)
        value.setKey(f.getKey)
        value.setConfigValue(f.getValue)
        value.setJobId(vo.getJobId)
        configMapper.insertValue(value)

      })

      val jobKeyTypes = configMapper.getConfigKeyValues(ConfigConf.JOBMANAGER_FLINK_CUSTOM.getValue, vo.getJobId)
      if(jobKeyTypes!=null && jobKeyTypes.size()>0){
        configMapper.deleteKeyValue(vo.getJobId,ConfigConf.JOBMANAGER_FLINK_CUSTOM.getValue,null)
      }
      vo.getParameterConfig.asScala.foreach(f=>{
        val keyValue = new ConfigKeyValue()
        keyValue.setKey(f.getKey)
        keyValue.setJobId(vo.getJobId)
        keyValue.setConfigValue(f.getValue)
        keyValue.setType(ConfigConf.JOBMANAGER_FLINK_CUSTOM.getValue)
        configMapper.insertValue(keyValue)
      })

      //权限
      vo.getPermissionConfig.asScala.filter(f=>f.getKey.equals(ConfigConf.JOBMANAGER_FLINK_AUTHORITY_AUTHOR.getValue)).foreach(f=>{
        val value = configMapper.getConfigKeyValue(vo.getJobId, f.getConfigkeyId)
        if(value != null){
          configMapper.deleteKeyValue(vo.getJobId,null,f.getConfigkeyId)
        }

        val keyValue = new ConfigKeyValue()
        keyValue.setKey(f.getKey)
        keyValue.setJobId(vo.getJobId)
        keyValue.setConfigValue(f.getValue)
        keyValue.setType(ConfigConf.JOBMANAGER_FLINK_AUTHORITY.getValue)
        keyValue.setConfigkeyId(f.getConfigkeyId)
        configMapper.insertValue(keyValue)
      })

    jobUserRoleMapper.deleteByJobUserRole(null,vo.getJobId)
     vo.getPermissionConfig.asScala.filter(f=>f.getKey.equals(ConfigConf.JOBMANAGER_FLINK_AUTHORITY_VISIBLE.getValue)).foreach(f=>{
        f.getValueLists.asScala.foreach(f=>{
          val users = jobUserRoleMapper.getUsersByUserName(f.getValue)
          if(users != null && users.size() > 0){
            users.asScala.foreach(f2=>{
              val role = new JobUserRole()
              role.setJobId(vo.getJobId)
              role.setUserId(f2.getId)
              role.setUsername(f2.getUsername)
              jobUserRoleMapper.insertJobUserRole(role)
            })
          }
        })
     })

  }


  def getFullTree(jobId:Long): ConfigKeyVO ={

    val configValues = configMapper.getConfigKeyValues(null,jobId).asScala

    var dtos:List[ConfigKeyValueDTO] = null
    if(configValues == null || configValues.isEmpty){
      val configKeys = configMapper.getConfigKey.asScala
      dtos = configKeys.filter(f=>f.getSort != 0).map(m=>{
        val dto = new ConfigKeyValueDTO()
        BeanUtils.copyProperties(m,dto)
        dto.setConfigkeyId(m.getId)
        dto
      }).toList
    }else{
      dtos = configValues.filter(f=>f.getSort != 0).map(m=>{
        val dto = new ConfigKeyValueDTO()
        BeanUtils.copyProperties(m,dto)
        dto.setValue(m.getConfigValue)
        dto.setConfigkeyId(m.getId)
        dto
      }).toList
    }

    val vo = configKeyValueToVO(jobId,dtos)
    val jobKeyTypes = configMapper.getConfigKeyValues(ConfigConf.JOBMANAGER_FLINK_CUSTOM.getValue, jobId)
    if(jobKeyTypes!=null && jobKeyTypes.size()>0){
      vo.setParameterConfig(jobKeyTypes.asScala.map(m=>{
        val vo = new ConfigRelationVO()
        vo.setConfigkeyId(m.getConfigkeyId)
        vo.setKey(m.getConfigKey)
        vo.setName(m.getName)
        vo.setValue(m.getConfigValue)
        vo
      }).asJava)
    }

    vo.setJobId(jobId)
    vo
  }


  private def configKeyValueToVO(jobId:Long,dtos:List[ConfigKeyValueDTO]): ConfigKeyVO ={
    val statusTwo = ConfigConf.JOBMANAGER_FLINK_CUSTOM_STATUS_TWO.getValue
    val configVO = new ConfigKeyVO()

   val groupDDs = dtos.groupBy(_.getType).map(m=>{
     (m._1,m._2.sortBy(_.getSort).map(p => {
       val vo = new ConfigRelationVO()
       BeanUtils.copyProperties(p,vo)

       if (p.getStatus.equals(statusTwo)) {
         val lists = p.getDefaultValue.split(",").map(f => {
           if(p.getKey.equals(ConfigConf.JOBMANAGER_FLINK_ALERT_RULE.getValue) && p.getValue != null) {
             val s = p.getValue.split(",")
             if(s.contains(f)) new ConfigRelationVO.ValueList(f, true)
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

    configVO.setResourceConfig(groupDDs.filter(f => f._1.equals(ConfigConf.JOBMANAGER_FLINK_RESOURCE.getValue)).flatMap(_._2).toList.asJava)
    configVO.setProduceConfig(groupDDs.filter(f => f._1.equals(ConfigConf.JOBMANAGER_FLINK_PRODUCE.getValue)).flatMap(_._2).toList.asJava)
    configVO.setAlarmConfig(groupDDs.filter(f => f._1.equals(ConfigConf.JOBMANAGER_FLINK_ALERT.getValue)).flatMap(_._2).toList.asJava)

    configVO.setPermissionConfig(groupDDs.filter(f => f._1.equals(ConfigConf.JOBMANAGER_FLINK_AUTHORITY.getValue)).flatMap(_._2).toList.asJava)

    //权限
    val users = jobUserRoleMapper.getUsersByUserName(null)
    val roles = jobUserRoleMapper.getUserRoleById(jobId,null)
    val list = new util.ArrayList[ConfigRelationVO.ValueList]()
    val configRelationVO = new ConfigRelationVO()

    if(users != null && users.size() > 0){
      val lists = users.asScala.map(f => {
        val value = new ConfigRelationVO.ValueList()
        value.setValue(f.getUsername)
        value.setSelected(false)
        value
      })
      if(roles != null && roles.size() > 0){
        lists.foreach(f=>{
          roles.asScala.foreach(f2=>{
            if(f2.getUsername.equals(f.getValue)){
              f.setSelected(true)
            }
          })
        })
      }
      configRelationVO.setKey(ConfigConf.JOBMANAGER_FLINK_AUTHORITY_VISIBLE.getValue)
      configRelationVO.setValueLists(list)

    }

    val permissionConfigs =new util.ArrayList[ConfigRelationVO]()
    permissionConfigs.add(configVO.getPermissionConfig.asScala.filter(f=>f.getKey.equals(ConfigConf.JOBMANAGER_FLINK_AUTHORITY_AUTHOR.getValue)).head)
    permissionConfigs.add(configRelationVO)
    configVO.setPermissionConfig(permissionConfigs)

    configVO
  }




}
