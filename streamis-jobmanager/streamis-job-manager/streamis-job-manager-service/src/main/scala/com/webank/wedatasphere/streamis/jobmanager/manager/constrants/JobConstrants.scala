package com.webank.wedatasphere.streamis.jobmanager.manager.constrants

object JobConstrants {

  val DEFAULT_JSON: String = """{"deployUser": "default","createTime": "default","pkgName": "default","highAvailableMessage": "default","isHighAvailable": false,"source": "aomp"}"""

  val JOB_RESET_ON_RESTART_WAIT_MILLS = 10 * 1000

  val TYPE_PROJECT = "project"

  val TYPE_JOB = "job"
}
