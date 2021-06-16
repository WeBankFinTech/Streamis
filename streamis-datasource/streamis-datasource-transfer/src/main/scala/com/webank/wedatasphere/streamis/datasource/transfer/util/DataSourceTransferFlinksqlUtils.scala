package com.webank.wedatasphere.streamis.datasource.transfer.util

object DataSourceTransferFlinksqlUtils {

  val kafkaTable =
    s"""
      |CREATE TABLE %s (
      |  %s
      |  ts TIMESTAMP(3) METADATA FROM 'timestamp'
      |) WITH (
      |  'connector' = 'kafka',
      |  'topic' = '%s',
      |  'properties.bootstrap.servers' = '%s',
      |  'properties.group.id' = '%s',
      |  'scan.startup.mode' = '%s',
      |  'format' = '%s'
      |);
    """.stripMargin
  def kafkaDataSourceTransfer(tableName:String,cols:String,topicName:String,servers:String,groupId:String,offsetMode:String,formatMode:String) : String = {
    kafkaTable.format(tableName , cols , topicName , servers , groupId , offsetMode , formatMode)
  }

  val mysqlTable =
    s"""
      |CREATE TABLE %s (
      |  %s
      |  ts TIMESTAMP(3) METADATA FROM 'timestamp'
      |) WITH (
      |   'connector' = 'jdbc',
      |   'url' = 'jdbc:mysql://%s:3306/%s',
      |   'table-name' = '%s'
      |);
    """.stripMargin
  def mysqlDataSourceTransfer(tableName:String, cols:String, serverIp:String, dbName:String) : String = {
    mysqlTable.format(tableName, cols, serverIp, dbName, tableName)
  }

}
