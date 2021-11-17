package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.conf

import org.apache.linkis.common.conf.CommonVars

/**
 *
 * @date 2021-11-17
 * @author enjoyyin
 * @since 0.5.0
 */
object JobLauncherConfiguration {

  val FETCH_FLINK_APPLICATION_INFO_MAX_TIMES = CommonVars("wds.streamis.application.info.fetch.max", 6)

}
