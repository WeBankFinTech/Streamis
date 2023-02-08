package com.webank.wedatasphere.streamis.jobmanager.manager.service
import com.webank.wedatasphere.streamis.jobmanager.launcher.conf.JobConfKeyConstants
import com.webank.wedatasphere.streamis.jobmanager.launcher.dao.StreamJobConfMapper
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.{StreamJobMapper, StreamTaskMapper}
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.{StreamJob, StreamJobVersion}
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.{JobInspectVo, JobSnapshotInspectVo, JobVersionInspectVo}
import org.apache.linkis.common.exception.ErrorException
import org.apache.linkis.common.utils.Logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util

@Service
class DefaultStreamJobInspectService extends StreamJobInspectService with Logging{

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
     Option(this.streamJobMapper.queryAndLockJobById(jobId)) match {
       case Some(streamJob) =>
         types.foreach {
           case JobInspectVo.Types.VERSION =>
             Option(versionInspect(streamJob)).foreach(inspectVos.add(_))
           case JobInspectVo.Types.SNAPSHOT =>
             Option(snapshotInspect(streamJob)).foreach(inspectVos.add(_))
           case _ => null
           // Do nothing
         }
       case _ => //Ignore
     }

     inspectVos
  }

  /**
   * Inspect the job version
   * @param streamJob stream job
   * @return
   */
  private def versionInspect(streamJob: StreamJob): JobVersionInspectVo = {
    Option(streamTaskMapper.getLatestByJobId(streamJob.getId)) match {
      case Some(task) =>
        val latestJobVersion = streamJobMapper.getLatestJobVersion(streamJob.getId)
        if (!task.getVersion.equals(latestJobVersion.getVersion)) {
          val inspectVo = new JobVersionInspectVo

          val lastJobVersion = streamJobMapper.getJobVersionById(streamJob.getId, task.getVersion)
          inspectVo.setLast(lastJobVersion)
          inspectVo.setNow(latestJobVersion)
          logger.info(s"Version inspect [ job: ${streamJob.getName}, id: ${streamJob.getId}," +
            s" last_version: ${task.getVersion}, now_version: ${latestJobVersion.getVersion}]")
          inspectVo
        } else null
      case _ => null
    }
  }

  /**
   * Inspect the snapshot
   * @param streamJob stream job
   * @return
   */
  private def snapshotInspect(streamJob: StreamJob): JobSnapshotInspectVo = {
    this.streamJobConfMapper.getRawConfValue(streamJob.getId, JobConfKeyConstants.START_AUTO_RESTORE_SWITCH.getValue) match {
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
