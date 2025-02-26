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

package com.webank.wedatasphere.streamis.projectmanager.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.utils.JobUtils;
import com.webank.wedatasphere.streamis.jobmanager.launcher.service.StreamJobConfService;
import com.webank.wedatasphere.streamis.jobmanager.manager.constrants.JobConstrants;
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.StreamJobMapper;
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.StreamJobTemplateMapper;
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.StreamTaskMapper;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.MetaJsonInfo;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamTask;
import com.webank.wedatasphere.streamis.jobmanager.manager.exception.FileException;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.JobTemplateFiles;
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf.JobConf;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.StreamJobService;
import com.webank.wedatasphere.streamis.projectmanager.utils.JsonUtil;
import com.webank.wedatasphere.streamis.projectmanager.utils.MD5Utils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.linkis.common.utils.JsonUtils;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamisFile;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.BMLService;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.StreamiFileService;
import com.webank.wedatasphere.streamis.jobmanager.manager.util.ReaderUtils;
import com.webank.wedatasphere.streamis.projectmanager.dao.ProjectManagerMapper;
import com.webank.wedatasphere.streamis.projectmanager.entity.ProjectFiles;
import com.webank.wedatasphere.streamis.projectmanager.service.ProjectManagerService;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by v_wbyynie on 2021/9/17.
 */
@Service
public class ProjectManagerServiceImpl implements ProjectManagerService, StreamiFileService {

    @Autowired
    private BMLService bmlService;

    @Autowired
    private ProjectManagerMapper projectManagerMapper;

    @Autowired
    private StreamJobMapper streamJobMapper;

    @Autowired
    private StreamTaskMapper streamTaskMapper;

    @Autowired
    private StreamJobTemplateMapper streamJobTemplateMapper;

    private static final String JSON_TYPE = ".json";

    private static final String templateName = "-meta.json";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upload(String username, String fileName, String version, String projectName, String filePath,String comment, String source) throws JsonProcessingException {
        String fileMd5 = MD5Utils.getMD5(filePath);
        Map<String, Object> result = bmlService.upload(username, filePath);
        ProjectFiles projectFiles = new ProjectFiles();
        projectFiles.setFileName(fileName);
        projectFiles.setVersion(version);
        projectFiles.setCreateBy(username);
        projectFiles.setComment(comment);
        projectFiles.setProjectName(projectName);
        projectFiles.setSource(source);
        ReaderUtils readerUtils = new ReaderUtils();
        projectFiles.setStorePath(readerUtils.readAsJson(result.get("version").toString(),result.get("resourceId").toString()));
        projectFiles.setMD5(fileMd5);
        ProjectFiles file = selectFile(fileName, version, projectName);
        if (file == null) {
            projectManagerMapper.insertProjectFilesInfo(projectFiles);
        }else {
            projectFiles.setId(file.getId());
            projectFiles.setUpdateTime(new Date());
            projectManagerMapper.updateFileById(projectFiles);
        }
    }


    @Override
    public StreamisFile getFile(String projectName, String fileName, String version) {
        return projectManagerMapper.selectFile(fileName, version, projectName);
    }

    @Override
    public List<? extends StreamisFile> listFileVersions(String projectName, String fileName) {
        return projectManagerMapper.listFileVersions(projectName, fileName);
    }

    @Override
    public InputStream download(StreamisFile file) throws JsonProcessingException {
        Map<String,String> map = JsonUtils.jackson().readValue(file.getStorePath(), Map.class);
        return bmlService.get(file.getCreateBy(), map.get("resourceId"), map.get("version"));
    }

    @Override
    public ProjectFiles getById(Long id) {
        return projectManagerMapper.getById(id);
    }

    @Override
    public boolean delete(String fileName, String projectName, String username) {
        int count = projectManagerMapper.countFiles(fileName,projectName);
        int delete = projectManagerMapper.deleteVersions(fileName,projectName,username);
        return count == delete;
    }

    @Override
    public void disableTemplate(String name, String projectName, String username){
        List<Long> templateIds = streamJobTemplateMapper.selectTemplateId(name,projectName);
        streamJobTemplateMapper.setEnable(templateIds,false);
    }

    @Override
    public ProjectFiles getFile(Long id, String projectName) {
        return projectManagerMapper.getProjectFile(id);
    }

    @Override
    public List<ProjectFiles> listFiles(String projectName, String username, String filename) {
        return projectManagerMapper.listFiles(projectName,username, filename);
    }

    @Override
    public boolean deleteFiles(String ids,String username) {
        if (!StringUtils.isBlank(ids) && !ArrayUtils.isEmpty(ids.split(","))) {
            String[] split = ids.split(",");
            List<Long> list = new ArrayList<>();
            for (String s : split) {
                list.add(Long.parseLong(s));
            }
            return projectManagerMapper.deleteFiles(list, username) >= list.size();
        }
        return true;
    }

    @Override
    public void disableTemplateFiles(String ids) {
        if (!StringUtils.isBlank(ids) && !ArrayUtils.isEmpty(ids.split(","))) {
            String[] split = ids.split(",");
            List<Long> list = new ArrayList<>();
            for (String s : split) {
                list.add(Long.parseLong(s));
            }
            for (Long id : list){
                ProjectFiles projectFile = projectManagerMapper.getById(id);
                if(projectFile.getFileName().endsWith(templateName)){
                    streamJobTemplateMapper.setEnableByVersion(projectFile.getFileName(),projectFile.getVersion(),false);
                }
            }
        }
    }

    @Override
    public ProjectFiles selectFile(String fileName, String version, String projectName) {
        return projectManagerMapper.selectFile(fileName, version, projectName);
    }

    @Override
    public List<String> getProjectNames(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
            return Collections.emptyList();
        }
        return projectManagerMapper.selectProjectNamesByIds(ids);
    }

    @Override
    public String getProjectNameById(Long id) {
        return projectManagerMapper.getProjectNameById(id);
    }

    @Override
    public String getProjectNameByFileId(Long id) {
        return projectManagerMapper.getProjectNameByFileId(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadJobTemplate(String username, String fileName, String filePath,String projectName,String version,String storePath) throws FileException, IOException {
        String path = filePath.replace(JSON_TYPE,"");
        ReaderUtils readerUtils = new ReaderUtils();
        MetaJsonInfo metaJsonInfo = readerUtils.parseJson(path,projectName);
        String metaJson = generateJobTemplate(metaJsonInfo);
        JobTemplateFiles jobTemplateFiles = new JobTemplateFiles();
        jobTemplateFiles.setName(fileName);
        jobTemplateFiles.setProjectName(projectName);
        jobTemplateFiles.setStorePath(storePath);
        jobTemplateFiles.setMetaJson(metaJson);
        jobTemplateFiles.setVersion(version);
        jobTemplateFiles.setDate(new Date());
        jobTemplateFiles.setEnable(true);

        JobTemplateFiles file = selectJobTemplate(fileName, version, projectName);
        if (file == null) {
            streamJobTemplateMapper.insertJobTemplate(jobTemplateFiles);
        }else {
            jobTemplateFiles.setId(file.getId());
            jobTemplateFiles.setDate(new Date());
            streamJobTemplateMapper.updateJobTemplateById(jobTemplateFiles);
        }
    }

    public JobTemplateFiles selectJobTemplate(String fileName, String version, String projectName) {
        return streamJobTemplateMapper.selectJobTemplate(fileName, version, projectName);
    }

    public String generateJobTemplate(MetaJsonInfo metaJsonInfo){
        String configJson = JobUtils.gson().toJson(metaJsonInfo);
        JsonObject jsonObj = new JsonParser().parse(configJson).getAsJsonObject();
        if (jsonObj.has(JobConstrants.FIELD_WORKSPACE_NAME())) {
            jsonObj.remove(JobConstrants.FIELD_WORKSPACE_NAME());
        }
//        if (jsonObj.has(JobConstrants.FIELD_METAINFO_NAME())) {
//            jsonObj.remove(JobConstrants.FIELD_METAINFO_NAME());
//        }
        if (jsonObj.has(JobConstrants.FIELD_JOB_NAME())) {
            jsonObj.remove(JobConstrants.FIELD_JOB_NAME());
        }
        if (jsonObj.has(JobConstrants.FIELD_JOB_TYPE())) {
            jsonObj.remove(JobConstrants.FIELD_JOB_TYPE());
        }
        if (jsonObj.has(JobConstrants.FIELD_JOB_TAG())) {
            jsonObj.remove(JobConstrants.FIELD_JOB_TAG());
        }
        if (jsonObj.has(JobConstrants.FIELD_JOB_COMMENT())) {
            jsonObj.remove(JobConstrants.FIELD_JOB_COMMENT());
        }
        if (jsonObj.has(JobConstrants.FIELD_JOB_DESCRIPTION())) {
            jsonObj.remove(JobConstrants.FIELD_JOB_DESCRIPTION());
        }
        return jsonObj.toString();
    }

    @Override
    public Boolean confirmToken(String source){
        Optional<String> sourceOption = Optional.ofNullable(source);
        if(sourceOption.isPresent() && JsonUtil.isJson(sourceOption.get())) {
            String sourceStr = sourceOption.get();
            Map<String,Object> sourceMap = BDPJettyServerHelper.gson().fromJson(sourceStr, Map.class);
            if (sourceMap.containsKey("source")) {
                String sourceValue = sourceMap.get("source").toString();
                if (sourceValue.equals(JobConf.HIGHAVAILABLE_SOURCE().getValue()) && sourceMap.containsKey("token")) {
                        String tokenContent = sourceMap.get("token").toString();
                        return tokenContent.equals(JobConf.HIGHAVAILABLE_TOKEN().getValue());
                    }
                }
            }
        return false;
    }

}
