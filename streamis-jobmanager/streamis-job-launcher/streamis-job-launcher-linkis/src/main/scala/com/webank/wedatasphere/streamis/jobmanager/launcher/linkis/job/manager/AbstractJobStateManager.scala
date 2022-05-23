/*
 * Copyright 2021 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.manager

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.JobInfo
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobStateManager
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.{JobState, JobStateFetcher}
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.FlinkJobStateFetchException
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.manager.AbstractJobStateManager.WINDOWS_ROOT_DIR_REGEX

import java.net.{URI, URL, URLConnection, URLStreamHandler}
import java.util.concurrent.ConcurrentHashMap
import java.util
import scala.util.matching.Regex
/**
 * Abstract job state manager
 */
abstract class AbstractJobStateManager extends JobStateManager {

  /**
   * Hold the job state fetcher with its type
   */
  private val jobStateFetcherHolder: ConcurrentHashMap[String, JobStateFetcher[_ <: JobState]]
        = new ConcurrentHashMap[String, JobStateFetcher[_ <: JobState]]()

  /**
   * Fetcher loaders
   */
  private val stateFetcherLoaders: util.Map[String, ()=> JobStateFetcher[_ <: JobState]] = new util.HashMap[String, () => JobStateFetcher[_ <: JobState]]()

  override def getOrCreateJobStateFetcher[T <: JobState](clazz: Class[T]): JobStateFetcher[T] = {
    val stateType = clazz.getCanonicalName
    val loader = Option(stateFetcherLoaders.get(stateType))
    if (loader.isEmpty){
      throw new FlinkJobStateFetchException(-1, s"Cannot find the fetcher loader for [$stateType]", null)
    }
    jobStateFetcherHolder.computeIfAbsent(stateType, new util.function.Function[String, JobStateFetcher[_ <: JobState]]{
      override def apply(t: String): JobStateFetcher[_ <: JobState] = loader.get.apply()
    }).asInstanceOf[JobStateFetcher[T]]
  }

  override def getJobState[T <: JobState](clazz: Class[T], jobInfo: JobInfo): T = Option(getOrCreateJobStateFetcher(clazz)) match {
    case Some(jobStateFetcher: JobStateFetcher[T]) =>jobStateFetcher.getState(jobInfo)
    case _ => null.asInstanceOf[T]
  }

  /**
   * Register job state fetcher
   *
   * @param clazz           clazz
   * @param builder job state fetcher loader/builder
   * @tparam T
   */
  override def registerJobStateFetcher[T <: JobState](clazz: Class[T], builder: () => JobStateFetcher[T]): Unit = {
    stateFetcherLoaders.put(clazz.getCanonicalName, builder)
  }

  override def getJobStateDir[T <: JobState](clazz: Class[T], scheme: String, relativePath: String): URI = {
    getJobStateDir(clazz, scheme, null, relativePath)
  }


  /**
   * Get job state directory uri
   *
   * @param clazz        clazz
   * @param scheme       scheme
   * @param authority    authority
   * @param relativePath relative path
   * @tparam T
   * @return
   */
  override def getJobStateDir[T <: JobState](clazz: Class[T], scheme: String, authority: String, relativePath: String): URI = {
    // To Support all schema
    new URI(scheme, authority, normalizePath(getJobStateRootPath(clazz, scheme) + "/" + relativePath), null, null)
  }

  private def normalizePath(input: String): String = {
    var path = input.replace("\\", "/")
    path = path.replaceAll("/+", "/")
    // Replace "." to "/"
    path = path.replaceAll("\\.", "/")
    if (path.endsWith("/") && !(path == "/") && !WINDOWS_ROOT_DIR_REGEX.pattern.matcher(path).matches()) path = path.substring(0, path.length - "/".length)
    path
  }
  def getJobStateRootPath[T <: JobState](clazz: Class[T], schema: String): String
}

object AbstractJobStateManager{
  val WINDOWS_ROOT_DIR_REGEX: Regex = "/\\p{Alpha}+:/".r
}
