package com.webank.wedatasphere.streamis.jobmanager.manager.constrants

object JobConstrants {

  val DEFAULT_JSON: String = """{"deployUser": "default","createTime": "default","pkgName": "default","highAvailableMessage": "default","isHighAvailable": false,"source": "aomp"}"""

  val JOB_RESET_ON_RESTART_WAIT_MILLS = 10 * 1000

  val TYPE_PROJECT = "project"

  val TYPE_JOB = "job"

  val FIELD_WORKSPACE_NAME = "workspaceName"

  val FIELD_METAINFO_NAME = "metaInfo"

  val FIELD_JOB_NAME = "jobName"

  val FIELD_JOB_TYPE = "jobType"
  val FIELD_JOB_TAG = "tags"
  val FIELD_JOB_COMMENT = "comment"
  val FIELD_JOB_DESCRIPTION = "description"

  val FLINK_APPLICATION_SEPARATE = "#"
}
