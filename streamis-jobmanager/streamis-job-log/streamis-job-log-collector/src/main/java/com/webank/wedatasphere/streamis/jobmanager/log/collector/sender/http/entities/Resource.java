package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.http.entities;

import java.io.File;
import java.util.List;

/**
 * Entity with resources
 */
public interface Resource {

    /**
     * Resources related
     * @return file list
     */
    List<File> getResources();

}
