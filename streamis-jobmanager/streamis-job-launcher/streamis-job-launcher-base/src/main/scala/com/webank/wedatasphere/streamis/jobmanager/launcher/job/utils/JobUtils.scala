package com.webank.wedatasphere.streamis.jobmanager.launcher.job.utils

import com.google.gson.{GsonBuilder, JsonElement, JsonPrimitive, JsonSerializationContext, JsonSerializer}

import java.lang
import java.lang.reflect.Type

object JobUtils {

  def isAnyVal[T](x: T)(implicit m: Manifest[T]) = m <:< manifest[AnyVal]

  val gson = new GsonBuilder()
    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    .serializeNulls
    .registerTypeAdapter(
      classOf[java.lang.Double],
      new JsonSerializer[java.lang.Double] {

        override def serialize(
                                t: lang.Double,
                                `type`: Type,
                                jsonSerializationContext: JsonSerializationContext
                              ): JsonElement =
          if (t == t.longValue()) new JsonPrimitive(t.longValue()) else new JsonPrimitive(t)

      }
    )
    .create
}
