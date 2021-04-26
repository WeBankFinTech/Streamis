package com.webank.wedatasphere.streamis.jobmanager.manager.util

import org.slf4j.LoggerFactory

import java.text.SimpleDateFormat
import java.util.Date

object DateUtils extends Serializable {

  private val logger = LoggerFactory.getLogger(DateUtils.getClass)
  private val serialVersionUID = 1L
  /**
   * 默认短日期格式
   * yyyy-MM-dd
   */
  val DATE_DEFAULT_FORMAT = "yyyy-MM-dd"

  /**
   * 订单号前缀 yyyyMMddHHmmss
   */
  val DATETIME_ORDER_FORMAT = "yyyyMMddHHmmss"
  /**
   * 默认日期时间格式
   * yyyy-MM-dd HH:mm:ss
   */
  val DATETIME_DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss"
  /**
   * 默认时间格式
   * HH:mm:ss
   */
  val TIME_DEFAULT_FORMAT = "HH:mm:ss"
  /**
   * 默认日期短格式
   * yyyyMMdd
   */
  val DATE_DEFAULT_SHORT_FORMAT = "yyyyMMdd"
  /**
   * 默认日期时间格式化
   */
  val dateTimeFormat = new SimpleDateFormat(DATETIME_DEFAULT_FORMAT)

  def intervals(start:Date,end:Date): String ={
    if(start == null || end == null) return ""
    val nm = 1000 * 60
    val diff = end.getTime - start.getTime
    (diff/nm)+"分钟"
  }

  def formatDate(dateTime:Date):String = {
    if(dateTime == null) return ""
    dateTimeFormat.format(dateTime)
  }

}
