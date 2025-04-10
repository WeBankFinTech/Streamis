package com.webank.wedatasphere.streamis.jobmanager.manager.service

import com.webank.wedatasphere.streamis.jobmanager.launcher.dao.StreamJobConfMapper
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf.{JobConf, JobConfKeyConstants}
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.constants.JobConstants
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.errorcode.JobLaunchErrorCode
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.exception.{JobCreateErrorException, JobErrorException, JobFetchErrorException}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.conf.JobLauncherConfiguration
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client.LinkisFlinkManagerJobClient
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.{StreamJobMapper, StreamTaskMapper}
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.{StreamJob, StreamJobVersion, StreamJobVersionFiles}
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.{JobHighAvailableVo, JobInspectVo, JobListInspectVo, JobSnapshotInspectVo, JobVersionInspectVo}
import com.webank.wedatasphere.streamis.jobmanager.manager.utils.SourceUtils
import org.apache.commons.lang3.StringUtils
import org.apache.linkis.common.exception.ErrorException
import org.apache.linkis.common.utils.{JsonUtils, Logging, Utils}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.net.URI
import java.util
import scala.collection.JavaConverters.asScalaBufferConverter

@Service
class DefaultStreamJobInspectService extends StreamJobInspectService with Logging {

  @Autowired
  private var streamTaskService: StreamTaskService = _

  @Autowired
  private var streamJobMapper: StreamJobMapper = _

  @Autowired
  private var streamTaskMapper: StreamTaskMapper = _

  @Autowired
  private var streamJobConfMapper: StreamJobConfMapper = _

  /**
   * Inspect method
   *
   * @param jobId job id
   * @param types type list for inspecting
   * @return
   */
  @throws(classOf[ErrorException])
  @Transactional(rollbackFor = Array(classOf[Exception]))
  override def inspect(jobId: Long, types: Array[JobInspectVo.Types]): util.List[JobInspectVo] = {
    val inspectVos: util.List[JobInspectVo] = new util.ArrayList[JobInspectVo]
    // Lock the stream job
    Option(this.streamJobMapper.queryJobById(jobId)) match {
      case Some(streamJob) =>
        types.foreach {
          case JobInspectVo.Types.VERSION =>
            Option(versionInspect(streamJob)).foreach(inspectVos.add(_))
          case JobInspectVo.Types.SNAPSHOT =>
            Option(snapshotInspect(streamJob)).foreach(inspectVos.add(_))
          case JobInspectVo.Types.LIST =>
            Option(listInspect(streamJob)).foreach(inspectVos.add(_))
          case JobInspectVo.Types.HIGHAVAILABLE =>
            Option(highAvailableInspect(streamJob)).foreach(inspectVos.add(_))
          case _ => null
          // Do nothing
        }
      case _ => //Ignore
    }
    inspectVos
  }

  /**
   * Inspect the job version
   *
   * @param streamJob stream job
   * @return
   */
  private def versionInspect(streamJob: StreamJob): JobVersionInspectVo = {
    val inspectVo = new JobVersionInspectVo
    val latestJobVersion = streamJobMapper.getLatestJobVersion(streamJob.getId)
    inspectVo.setNow(latestJobVersion)
    Option(streamTaskMapper.getLatestByJobId(streamJob.getId)) match {
      case Some(task) =>
        val lastJobVersion = streamJobMapper.getJobVersionById(streamJob.getId, task.getVersion)
        inspectVo.setLast(lastJobVersion)
      case _ =>
    }
    inspectVo
  }

  /**
   * Inspect the snapshot
   *
   * @param streamJob stream job
   * @return
   */
  private def snapshotInspect(streamJob: StreamJob): JobSnapshotInspectVo = {
    Option(this.streamJobConfMapper.getRawConfValue(streamJob.getId, JobConfKeyConstants.SAVEPOINT.getValue + "path")) match {
      case path if (path.isDefined && StringUtils.isNotBlank(path.get)) =>
        val inspectVo = new JobSnapshotInspectVo
        inspectVo.setPath(new URI(path.get).toString)
        inspectVo
      case _ => this.streamJobConfMapper.getRawConfValue(streamJob.getId, JobConfKeyConstants.START_AUTO_RESTORE_SWITCH.getValue) match {
        case "ON" =>
          Option(this.streamTaskService.getStateInfo(streamTaskMapper
            .getLatestLaunchedById(streamJob.getId))) match {
            case Some(jobState) =>
              val inspectVo = new JobSnapshotInspectVo
              inspectVo.setPath(jobState.getLocation.toString)
              inspectVo
            case _ => null
          }
        case _ => null
      }
    }

  }

  private def listInspect(job: StreamJob): JobListInspectVo = {
    // 如果分离式特性开关开启，就获取分离式client，发送list请求
    val listVo = new JobListInspectVo
    if (JobLauncherConfiguration.ENABLE_FLINK_MANAGER_EC_ENABLE.getHotValue() && JobLauncherConfiguration.ENABLE_FLINK_LIST_INSPECT.getHotValue) {
      val appName = s"${job.getProjectName}.${job.getName}"
      Utils.tryCatch {
        val appType = if (job.getJobType.toLowerCase().contains("flink")) {
          JobConstants.APP_TYPE_FLINK
        } else if (job.getJobType.toLowerCase().contains("spark")) {
          JobConstants.APP_TYPE_SPARK
        } else {
          logger.error(s"Unknown job type : ${job.getJobType}")
          throw new JobCreateErrorException(JobLaunchErrorCode.JOB_LIST_YARN_APP_ERROR, s"Unknown job type : ${job.getJobType}")
        }
        val appTypeList = new util.ArrayList[String]()
        if (StringUtils.isNotBlank(appType)) {
          appTypeList.add(appType)
        }
        logger.info(s"job appType is  : ${appType}")
        val appList = LinkisFlinkManagerJobClient.listYarnApp(appName, job.getSubmitUser, "streamis", appTypeList)
        if (null != appList && !appList.isEmpty) {
          appList.asScala.foreach {
            app =>
              if (app.getApplicationName().equalsIgnoreCase(appName)) {
                listVo.addYarnApp(app)
              } else {
                logger.info(s"yarn app name : ${app.getApplicationName()} like but not equals job name : ${appName}, ignore it. ")
              }
          }
          var size = 0
          if (listVo.getList != null) {
            size = listVo.getList.size
          } else {
            listVo.addOneUrl(null, "无", null)
          }
          logger.info(s"There are ${size} apps with same name : ${appName}")
          logger.info(JsonUtils.jackson.writeValueAsString(appList))
        } else {
          listVo.addOneUrl(null, "无", null)
        }
      } {
        case e: Exception =>
          val msg = s"查询同名运行中yarn应用失败，请稍后再试。appName: ${appName}, user : ${job.getSubmitUser}. ${e.getMessage}"
          logger.error(msg, e)
          throw new JobFetchErrorException(JobLaunchErrorCode.JOB_LIST_YARN_APP_ERROR, msg)
      }
    } else if (JobLauncherConfiguration.ENABLE_FLINK_LIST_INSPECT.getValue) {
      // default notice
      listVo.addOneUrl(null, "管理员未开启管理引擎特性，无法查看运行中同名yarn应用", null)
    } else {
      listVo.addOneUrl(null, "管理员未开启检查运行中同名yarn应用特性", null)
    }
    listVo
  }

  private def highAvailableInspect(job: StreamJob): JobHighAvailableVo = {
    Utils.tryAndError {
      var inspectVo = new JobHighAvailableVo
      val jobId = job.getId
      val jobVersion = streamJobMapper.getLatestJobVersion(jobId)
      val highAvailablePolicy = streamJobConfMapper.getRawConfValue(jobId, JobConf.HIGHAVAILABLE_POLICY_KEY.getValue)
      val sourceOption: Option[String] = Option(jobVersion.getSource)
      sourceOption match {
        case Some(source) =>
          inspectVo = SourceUtils.manageJobProjectFile(highAvailablePolicy, source)
        case None =>
          logger.warn("this job source is null")
          if (highAvailablePolicy == JobConf.HIGHAVAILABLE_DEFAULT_POLICY.getValue || highAvailablePolicy == JobConf.HIGHAVAILABLE_POLICY_SINGLE_BAK.getValue){
            inspectVo.setHighAvailable(true)
            inspectVo.setMsg("job为单活，跳过高可用检查")
          } else {
            if (JobConf.HIGHAVAILABLE_ENABLE_INTERFACE_UPLOAD.getHotValue()) {
          inspectVo.setHighAvailable(true)
          inspectVo.setMsg("用户直接从页面上传，job的source为空，跳过高可用检查")
            } else {
              inspectVo.setHighAvailable(false)
              inspectVo.setMsg("用户直接从页面上传，高可用检查不通过")
            }
          }
      }
      inspectVo
    }
  }

}
