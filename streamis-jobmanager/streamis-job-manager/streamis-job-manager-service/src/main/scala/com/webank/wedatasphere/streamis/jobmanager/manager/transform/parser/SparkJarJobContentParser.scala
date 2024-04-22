package com.webank.wedatasphere.streamis.jobmanager.manager.transform.parser

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.exception.JobExecuteErrorException

import java.util
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.{JobTemplateFiles, StreamJob, StreamJobVersion, StreamJobVersionFiles, StreamisFile}
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity.{StreamisJarTransformJobContent, StreamisTransformJobContent}
import org.apache.commons.lang.StringUtils
import org.apache.linkis.common.utils.JsonUtils
import org.apache.linkis.manager.label.entity.engine.RunType
import org.apache.linkis.manager.label.entity.engine.RunType.RunType
import org.springframework.stereotype.Component

import scala.collection.JavaConverters._

/**
 *
 * @date 2022-10-19
 * @author enjoyyin
 * @since 0.5.0
 */
@Component
class SparkJarJobContentParser extends AbstractJobContentParser {

  override val jobType: String = "spark.jar"
  override val runType: RunType = RunType.JAR

  override def parseTo(job: StreamJob, jobVersion: StreamJobVersion,jobTemplate: JobTemplateFiles): StreamisTransformJobContent = {
    val createFile: String => StreamisFile = fileName => {
      val file = new StreamJobVersionFiles()
      file.setFileName(fileName)
      file.setCreateBy(job.getCreateBy)
      file.setCreateTime(job.getCreateTime)
      file.setJobId(job.getId)
      file.setJobVersionId(jobVersion.getId)
      file.setVersion(jobVersion.getVersion)
      file.setStorePath("<Unknown>")
      file.setStoreType("<Unknown>")
      file
    }
    val transformJobContent = new StreamisJarTransformJobContent
    val jobContent = JsonUtils.jackson.readValue(jobVersion.getJobContent, classOf[util.Map[String, Object]])
    jobContent.get("main.class.jar") match {
      case mainClassJar: String =>
        transformJobContent.setMainClassJar(createFile(mainClassJar))
      case _ => throw new JobExecuteErrorException(30500, "main.class.jar is needed.")
    }
    jobContent.get("main.class") match {
      case mainClass: String =>
        transformJobContent.setMainClass(mainClass)
      case _ => throw new JobExecuteErrorException(30500, "main.class is needed.")
    }
    jobContent.get("args") match {
      case args: util.List[String] =>
        transformJobContent.setArgs(args)
      case _ =>
    }
    jobContent.get("hdfs.jars") match {
      case hdfsJars: util.List[String] =>
        transformJobContent.setHdfsJars(hdfsJars)
      case _ =>
    }
    jobContent.get("dependency.jars") match {
      case dependencyJars: util.List[String] =>
        val parsedDependencyJars = dependencyJars.asScala.filter(StringUtils.isNotBlank).map(createFile).asJava
        transformJobContent.setDependencyJars(parsedDependencyJars)
      case _ =>
    }
    jobContent.get("resources") match {
      case resources: util.List[String] =>
        val parsedResources = resources.asScala.filter(StringUtils.isNotBlank).map(createFile).asJava
        transformJobContent.setResources(parsedResources)
      case _ =>
    }
    transformJobContent
  }

}
