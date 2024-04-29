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

package com.webank.wedatasphere.streamis.projectmanager.restful.api;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf.JobConf;
import com.webank.wedatasphere.streamis.jobmanager.launcher.service.StreamJobConfService;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.JobTemplateFiles;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamisFile;
import com.webank.wedatasphere.streamis.jobmanager.manager.exception.FileException;
import com.webank.wedatasphere.streamis.jobmanager.manager.project.service.ProjectPrivilegeService;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.StreamJobService;
import com.webank.wedatasphere.streamis.jobmanager.manager.util.IoUtils;
import com.webank.wedatasphere.streamis.jobmanager.manager.util.ReaderUtils;

import com.webank.wedatasphere.streamis.projectmanager.entity.ProjectFiles;
import com.webank.wedatasphere.streamis.projectmanager.service.ProjectManagerService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.utils.ModuleUserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequestMapping(path = "/streamis/streamProjectManager/project")
@RestController
public class ProjectManagerRestfulApi {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectManagerRestfulApi.class);

    @Autowired
    private ProjectManagerService projectManagerService;
    @Autowired
    private ProjectPrivilegeService projectPrivilegeService;
    @Autowired
    private StreamJobService streamJobService;
    @Autowired
    private StreamJobConfService streamJobConfService;

    private static final String NO_OPERATION_PERMISSION_MESSAGE = "the current user has no operation permission";

    private static final String TYPE_PROJECT = "project";

    private static final String TYPE_JOB = "job";

    private static final String templateName = "-meta.json";

    @RequestMapping(path = "/files/upload", method = RequestMethod.POST)
    public Message upload(HttpServletRequest req,
                           @RequestParam(name = "version",required = false) String version,
                           @RequestParam(name = "projectName",required = false) String projectName,
                           @RequestParam(name = "comment", required = false) String comment,
                           @RequestParam(name = "updateWhenExists", required = false) boolean updateWhenExists,
                           @RequestParam(name = "file") List<MultipartFile> files,
                           @RequestParam(name = "source",required = false) String source) throws UnsupportedEncodingException, FileException {
        String username = ModuleUserUtils.getOperationUser(req, "upload project files");
        if (StringUtils.isBlank(version)) {
            return Message.error("version is null");
        }
        if (StringUtils.isBlank(projectName)) {
            return Message.error("projectName is null");
        }
        if (StringUtils.isBlank(source)) {
            LOG.info("source的值为空");
        }
        if (version.length()>=30){
            return Message.error("version character length is to long ,Please less than 30 （版本字符长度过长，请小于30）");
        }
        if (!projectPrivilegeService.hasEditPrivilege(req,projectName)) {
            return Message.error(NO_OPERATION_PERMISSION_MESSAGE);
        }
        if ((Boolean) JobConf.STANDARD_AUTHENTICATION_KEY().getHotValue()){
            if (!projectManagerService.confirmToken(source)){
                return Message.error("As this file is not from standard release, it is not allowed to upload");
            }
        }
        //Only uses 1st file(只取第一个文件)
        MultipartFile p = files.get(0);
        String fileName = new String(p.getOriginalFilename().getBytes("ISO8859-1"), StandardCharsets.UTF_8);
        ReaderUtils readerUtils = new ReaderUtils();
        if (!readerUtils.checkName(fileName)) {
            return Message.warn("fileName should only contains numeric/English characters and '-_'(仅允许包含数字，英文,中划线,下划线)");
        }

        if (!ReaderUtils.isValidFileFormat(fileName)){
            return Message.warn("file should only "+ JobConf.STREAMIS_CHECK_FILE_FORMAT().getHotValue() +"(仅允许 " +JobConf.STREAMIS_CHECK_FILE_FORMAT().getHotValue()+"格式)");
        }
        if (!updateWhenExists) {
            ProjectFiles projectFiles = projectManagerService.selectFile(fileName, version, projectName);
            if (projectFiles != null) {
                return Message.warn("the file:[" + fileName + "]is exist in the project:" + projectName + ",version:" + version);
            }
        }
        InputStream is = null;
        OutputStream os = null;
        try {
            IoUtils.validateFileName(fileName);
            String inputPath = IoUtils.generateIOPath(username, "streamis", fileName);
            is = p.getInputStream();
            os = IoUtils.generateExportOutputStream(inputPath);
            IOUtils.copy(is, os);
            if (!p.isEmpty() && (p.getContentType().equals("application/json") || p.getOriginalFilename().endsWith(templateName))) {
                if(!readerUtils.checkMetaTemplate(fileName,inputPath,projectName)) return Message.error("meta template is not correct,eg:testProject(项目名)-meta.json");
                projectManagerService.upload(username, fileName, version, projectName, inputPath, comment, source);
                StreamisFile file = projectManagerService.selectFile(fileName,version,projectName);
                projectManagerService.uploadJobTemplate(username,fileName,inputPath,projectName,version,file.getStorePath());
            }else{
                projectManagerService.upload(username, fileName, version, projectName, inputPath, comment, source);
            }
            File file = new File(inputPath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            LOG.error("failed to upload zip {} fo user {}", fileName, username, e);
            return Message.error(e.getMessage());
        } finally {
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(is);
        }
        return Message.ok();
    }



    @RequestMapping(path = "/files/list", method = RequestMethod.GET)
    public Message list( HttpServletRequest req,@RequestParam(value = "filename",required = false) String filename,
                         @RequestParam(value = "projectName",required = false) String projectName, @RequestParam(value = "username",required = false) String username,
                         @RequestParam(value = "pageNow",defaultValue = "1") Integer pageNow,
                         @RequestParam(value = "pageSize",defaultValue = "20") Integer pageSize) {
        if (StringUtils.isBlank(projectName)) {
            return Message.error("projectName is null");
        }
        if (!projectPrivilegeService.hasAccessPrivilege(req,projectName)) return Message.error(NO_OPERATION_PERMISSION_MESSAGE);
        PageHelper.startPage(pageNow, pageSize);
        List<ProjectFiles> fileList;
        try {
            fileList = projectManagerService.listFiles(projectName, username, filename);
        } finally {
            PageHelper.clearPage();
        }
        PageInfo pageInfo = new PageInfo(fileList);
        return Message.ok().data("files", fileList).data("totalPage", pageInfo.getTotal());
    }

    @RequestMapping(path = "/files/version/list", method = RequestMethod.GET)
    public Message versionList( HttpServletRequest req, @RequestParam(value = "fileName",required = false) String fileName,
                                @RequestParam(value = "projectName",required = false) String projectName,
                                @RequestParam(value = "pageNow",defaultValue = "1") Integer pageNow,
                                @RequestParam(value = "pageSize",defaultValue = "20") Integer pageSize) {
        if (StringUtils.isBlank(projectName)) {
            return Message.error("projectName is null");
        }
        if (StringUtils.isBlank(fileName)) {
            return Message.error("fileName is null");
        }
        if (!projectPrivilegeService.hasAccessPrivilege(req,projectName)) return Message.error(NO_OPERATION_PERMISSION_MESSAGE);
        PageHelper.startPage(pageNow, pageSize);
        List<? extends StreamisFile> fileList;
        try {
            fileList = projectManagerService.listFileVersions(projectName, fileName);
        } finally {
            PageHelper.clearPage();
        }
        PageInfo pageInfo = new PageInfo(fileList);
        return Message.ok().data("files", fileList).data("totalPage", pageInfo.getTotal());
    }


    @RequestMapping(path = "/files/delete", method = RequestMethod.GET)
    public Message delete( HttpServletRequest req, @RequestParam(value = "fileName",required = false) String fileName,
                           @RequestParam(value = "projectName",required = false) String projectName) {
        String username = ModuleUserUtils.getOperationUser(req, "Delete file:" + fileName + " in project: " + projectName);
        if (!projectPrivilegeService.hasEditPrivilege(req,projectName)) return Message.error(NO_OPERATION_PERMISSION_MESSAGE);
        projectManagerService.deleteTemplate(fileName,projectName,username);
        return projectManagerService.delete(fileName, projectName, username)? Message.ok()
                : Message.warn("you have no permission delete some files not belong to you");
    }

    @RequestMapping(path = "/files/version/delete", method = RequestMethod.GET)
    public Message deleteVersion(HttpServletRequest req, @RequestParam(value = "ids",required = false) String ids) {
        String username = ModuleUserUtils.getOperationUser(req, "Delete file versions in project");
        List<Long> idList = new ArrayList<>();
        if (!StringUtils.isBlank(ids) && !ArrayUtils.isEmpty(ids.split(","))) {
            String[] split = ids.split(",");
            for (String s : split) {
                idList.add(Long.parseLong(s));
            }
        }
        String projectName = projectManagerService.getProjectNameByFileId(Long.valueOf(ids));
        if (!projectPrivilegeService.hasEditPrivilege(req,projectName)) {
            return Message.error(NO_OPERATION_PERMISSION_MESSAGE);
        }
        projectManagerService.deleteTemplateFiles(ids);
        return projectManagerService.deleteFiles(ids, username) ? Message.ok()
                : Message.warn("you have no permission delete some files not belong to you");
    }

    @RequestMapping(path = "/files/download", method = RequestMethod.GET)
    public Message download( HttpServletRequest req, HttpServletResponse response,
                             @RequestParam(value = "id",required = false) Long id,
                             @RequestParam(value = "materialType",required = false) String materialType,
                             @RequestParam(value = "projectName",required = false)String projectName) {
        StreamisFile file = null;
        String userName = ModuleUserUtils.getOperationUser(req, "download job");
        if (org.apache.commons.lang.StringUtils.isBlank(userName)) return Message.error("current user has no permission");
        if (StringUtils.isBlank(projectName)) {
            if (StringUtils.isBlank(materialType)) {
                return Message.error("projectName and materialType is null");
            } else if (materialType.equals(TYPE_JOB)) {
                file = streamJobService.getJobFileById(id);
            } else if (materialType.equals(TYPE_PROJECT)){
                file = projectManagerService.getFile(id, projectName);
            }
        } else {
            if (!projectPrivilegeService.hasEditPrivilege(req, projectName))
                return Message.error(NO_OPERATION_PERMISSION_MESSAGE);
            file = projectManagerService.getFile(id, projectName);
        }
        if (file == null) {
            return Message.error("no such file in this project");
        }
        if (StringUtils.isBlank(file.getStorePath())) {
            return Message.error("storePath is null");
        }
        response.setContentType("application/x-download");
        response.setHeader("content-Disposition", "attachment;filename=" + file.getFileName());

        try (InputStream is = projectManagerService.download(file);
             OutputStream os = response.getOutputStream()
        ) {
            int len = 0;
            byte[] arr = new byte[2048];
            while ((len = is.read(arr)) > 0) {
                os.write(arr, 0, len);
            }
            os.flush();
        } catch (Exception e) {
            LOG.error("download file: {} failed , message is : {}", file.getFileName(), e);
            return Message.error(e.getMessage());
        }
        return Message.ok();
    }
}
