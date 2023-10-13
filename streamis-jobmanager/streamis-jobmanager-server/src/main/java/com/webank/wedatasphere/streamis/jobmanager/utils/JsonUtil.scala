package com.webank.wedatasphere.streamis.jobmanager.utils

import com.google.gson.{Gson, JsonElement, JsonSyntaxException}
import org.apache.commons.lang3.StringUtils
import org.apache.linkis.common.utils.{Logging, Utils}

import java.io.IOException

object JsonUtil extends Logging {

  lazy private val strictGsonAdapter = new Gson().getAdapter(classOf[JsonElement])

  def isJson(s: String): Boolean = {
    var flag = false
    if (StringUtils.isNotBlank(s)) {
      Utils.tryCatch {
        strictGsonAdapter.fromJson(s)
        flag = true
      } {
        case e: JsonSyntaxException | IOException =>
          logger.error(s"\"${s}\" is invalid json.")
          flag = false
      }
    }
    flag
  }

}
