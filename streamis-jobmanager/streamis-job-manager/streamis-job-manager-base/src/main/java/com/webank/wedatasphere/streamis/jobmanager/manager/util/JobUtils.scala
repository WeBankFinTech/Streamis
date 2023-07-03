package com.webank.wedatasphere.streamis.jobmanager.manager.util

import org.apache.commons.lang3.StringUtils

object JobUtils {

  def escapeChar(string: String): String = {
    var str = string
    if (StringUtils.isNotBlank(string)) {
      str = string.replace("\\\\", "\\\\\\\\")
        .replace("_", "\\_")
        .replace("\'", "\\'")
        .replace("%", "\\%")
        .replace("*", "\\*")
    }
    str
  }

}
