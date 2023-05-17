package com.webank.wedatasphere.streamis.jobmanager.manager.utils

import com.webank.wedatasphere.streamis.jobmanager.manager.conf.JobConf

import java.util
import scala.collection.JavaConverters.{asScalaSetConverter, mapAsScalaMapConverter}

object JobUtils {
  /**
   * Filter the illegal characters parameter specific
   * @param params parameters
   */
  def filterParameterSpec(params: util.Map[String, AnyRef]): util.Map[String, AnyRef] ={
    for (paramEntry <- params.entrySet().asScala){
      val value = paramEntry.getValue
      value match {
        case str: String => paramEntry.setValue(str.replace(" ", JobConf.STREAMIS_JOB_PARAM_BLANK_PLACEHOLDER.getValue))
        case _ =>
      }
    }
    params
  }

}
