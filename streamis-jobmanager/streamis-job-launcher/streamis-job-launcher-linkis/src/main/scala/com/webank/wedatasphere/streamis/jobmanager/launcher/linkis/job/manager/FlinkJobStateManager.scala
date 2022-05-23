package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.manager
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobState
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.conf.JobLauncherConfiguration
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state.{Checkpoint, Savepoint}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.url.LinkisURLStreamHandlerFactory

import java.net.URL


/**
 * Flink job state manager
 */
class FlinkJobStateManager extends AbstractJobStateManager {
  /**
   * State type => root path
   */
  val stateRootPath: Map[String, String] = Map(
    classOf[Savepoint].getCanonicalName -> JobLauncherConfiguration.FLINK_SAVEPOINT_PATH.getValue,
    classOf[Checkpoint].getCanonicalName -> JobLauncherConfiguration.FLINk_CHECKPOINT_PATH.getValue
  )
  // TODO register the fetcher

  override def getJobStateRootPath[T <: JobState](clazz: Class[T], schema: String): String = {
     stateRootPath.getOrElse(clazz.getCanonicalName, "")
  }
}

object FlinkJobStateManager{
  // set urlStreamHandler use support schemas
  URL.setURLStreamHandlerFactory(new LinkisURLStreamHandlerFactory(JobLauncherConfiguration.FLINK_STATE_SUPPORT_SCHEMES.getValue.split(","): _*))
}
