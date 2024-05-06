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

package com.webank.wedatasphere.streamis.projectmanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamisFile;
import com.webank.wedatasphere.streamis.jobmanager.manager.exception.FileException;
import com.webank.wedatasphere.streamis.projectmanager.entity.ProjectFiles;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by v_wbyynie on 2021/9/17.
 */
public interface ProjectManagerService {
    void upload(String username, String fileName, String version, String projectName, String file, String comment, String source) throws IOException;

    List<ProjectFiles> listFiles(String projectName, String username, String filename);

    boolean deleteFiles(String ids,String username);

    void disableTemplateFiles(String ids);

    ProjectFiles selectFile(String fileName, String version, String projectName);

    List<? extends StreamisFile> listFileVersions(String projectName, String fileName);

    InputStream download(StreamisFile projectFiles) throws JsonProcessingException;

    ProjectFiles getById(Long id);

    boolean delete(String fileName, String projectName, String username);

    void disableTemplate(String name, String projectName, String username);

    ProjectFiles getFile(Long id, String projectName);

    List<String> getProjectNames(List<Long> ids);

    String getProjectNameById(Long id);

    String getProjectNameByFileId(Long id);

    void uploadJobTemplate(String username, String fileName, String filePath, String projectName,String version,String storePath) throws FileException, IOException;

    Boolean confirmToken(String token);
}
