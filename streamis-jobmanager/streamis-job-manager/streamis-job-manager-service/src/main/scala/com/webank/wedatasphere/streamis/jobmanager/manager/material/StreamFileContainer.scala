package com.webank.wedatasphere.streamis.jobmanager.manager.material

import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamisFile
import java.util
/**
 * Define the stream file container
 */
trait StreamFileContainer {
  /**
   * Container name
   * @return
   */
   def getContainerName: String

  /**
   * Get stream files
   * @return
   */
   def getStreamFiles: util.List[StreamisFile]

  /**
   * Get stream files by match function
   * @param matchFunc match function
   * @return
   */
   def getStreamFiles(matchFunc: StreamisFile => Boolean): util.List[StreamisFile]

  /**
   * Get stream file by basename, model name and suffix
   * @param name name
   * @param model model
   * @param suffix suffix
   * @return
   */
   def getStreamFile(name: String, model: String, suffix: String): StreamisFile
}