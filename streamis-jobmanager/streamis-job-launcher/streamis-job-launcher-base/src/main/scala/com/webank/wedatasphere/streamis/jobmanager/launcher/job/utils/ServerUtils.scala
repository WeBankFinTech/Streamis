package com.webank.wedatasphere.streamis.jobmanager.launcher.job.utils

import org.apache.linkis.common.utils.{Logging, Utils}

object ServerUtils extends Logging {

  val thisServiceInstance: String = {
    val hostname = Utils.getComputerName
    val port = Utils.tryCatch {
      Integer.parseInt(System.getProperty("server.port"))
    } {
      case e: Exception =>
        logger.warn("Parse system property 'server.port' failed. {}", e.getMessage)
        0
    }
    hostname + port
  }

}
