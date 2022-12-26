package com.webank.wedatasphere.streamis.jobmanager.launcher.job.`type`

/**
 * @author jefftlin
 * */
object ConnectType extends Enumeration {

  type ConnectType = Value
  val ATTACH = Value("attach")
  val DETACH = Value("detach")
  val DETACH_STANDALONE = Value("detach_standalone")
}

