package com.webank.wedatasphere.streamis.jobmanager.launcher.job.`type`

/**
 * @author jefftlin
 * */
object JobClientType extends Enumeration {

  type ConnectType = Value
  /**
   * Attach
   */
  val ATTACH: JobClientType.Value = Value("attach")

  /**
   * Detach
   */
  val DETACH: JobClientType.Value = Value("detach")

  /**
   * Detach standalone
   */
  val DETACH_STANDALONE: JobClientType.Value = Value("detach_standalone")
}

