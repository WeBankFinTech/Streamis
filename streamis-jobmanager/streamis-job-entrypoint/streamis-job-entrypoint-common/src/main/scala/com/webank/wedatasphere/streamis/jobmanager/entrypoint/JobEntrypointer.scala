package com.webank.wedatasphere.streamis.jobmanager.entrypoint

/**
 * @author jefftlin
 */
abstract class JobEntrypointer {

  def register(engineType: String): Boolean {

  }
}
