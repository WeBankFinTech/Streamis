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
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamisFile;
import com.webank.wedatasphere.streamis.jobmanager.manager.exception.FileException;
import com.webank.wedatasphere.streamis.jobmanager.manager.exception.FileExceptionManager;
import com.webank.wedatasphere.streamis.jobmanager.manager.util.IoUtils;
import com.webank.wedatasphere.streamis.jobmanager.manager.util.ReaderUtils;
import com.webank.wedatasphere.streamis.projectmanager.entity.ProjectFiles;
import com.webank.wedatasphere.streamis.projectmanager.service.ProjectManagerService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Path("/streamis/streamProjectManager/project")
public class ProjectManagerRestfulApi {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectManagerRestfulApi.class);

    @Autowired
    private ProjectManagerService projectManagerService;

    @POST
    @Path("/files/upload")
    public Message upload(@Context HttpServletRequest req,
                           @RequestParam(name = "version") String version,
                           @RequestParam(name = "projectName") String projectName,
                           @RequestParam(name = "comment", required = false) String comment,
                           @RequestParam(name = "updateWhenExists", required = false) boolean updateWhenExists,
                           @RequestParam(name = "file") List<MultipartFile> files) throws UnsupportedEncodingException, FileException {


        String username = SecurityFilter.getLoginUsername(req);
        if (StringUtils.isBlank(version)) {
            return Message.error("version is null");
        }
        if (StringUtils.isBlank(projectName)) {
            return Message.error("projectName is null");
        }
        //Only uses 1st file(只取第一个文件)
        MultipartFile p = files.get(0);
        String fileName = new String(p.getOriginalFilename().getBytes("ISO8859-1"), StandardCharsets.UTF_8);
        ReaderUtils readerUtils = new ReaderUtils();
        if (!readerUtils.checkName(fileName)) {
            throw FileExceptionManager.createException(30601, fileName);
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
            String inputPath = IoUtils.generateIOPath(username, "streamis", fileName);
            is = p.getInputStream();
            os = IoUtils.generateExportOutputStream(inputPath);
            IOUtils.copy(is, os);
            projectManagerService.upload(username, fileName, version, projectName, inputPath,comment);
        } catch (Exception e) {
            LOG.error("failed to upload zip {} fo user {}", fileName, username, e);
            return Message.error(e.getMessage());
        } finally {
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(is);
        }
        return Message.ok();
    }


    @GET
    @Path("/files/list")
    public Message list(@Context HttpServletRequest req,@QueryParam("filename") String filename,
                         @QueryParam("projectName") String projectName, @QueryParam("username") String username,
                         @DefaultValue("1") @QueryParam("pageNow") Integer pageNow,
                         @DefaultValue("20") @QueryParam("pageSize") Integer pageSize) {
        if (StringUtils.isBlank(projectName)) {
            return Message.error("projectName is null");
        }
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

    @GET
    @Path("/files/version/list")
    public Message versionList(@Context HttpServletRequest req, @QueryParam("fileName") String fileName,
                                @QueryParam("projectName") String projectName,
                                @DefaultValue("1") @QueryParam("pageNow") Integer pageNow,
                                @DefaultValue("20") @QueryParam("pageSize") Integer pageSize) {
        String username = SecurityFilter.getLoginUsername(req);
        if (StringUtils.isBlank(projectName)) {
            return Message.error("projectName is null");
        }
        if (StringUtils.isBlank(fileName)) {
            return Message.error("fileName is null");
        }
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

    @GET
    @Path("/files/delete")
    public Message delete(@Context HttpServletRequest req, @QueryParam("fileName") String fileName,
                           @QueryParam("projectName") String projectName) {
        String username = SecurityFilter.getLoginUsername(req);

        return projectManagerService.delete(fileName, projectName, username) ? Message.ok()
                : Message.warn("you have no permission delete some files not belong to you");
    }

    @GET
    @Path("/files/version/delete")
    public Message deleteVersion(@Context HttpServletRequest req, @QueryParam("ids") String ids) {
        String username = SecurityFilter.getLoginUsername(req);

        return projectManagerService.deleteFiles(ids, username) ? Message.ok()
                : Message.warn("you have no permission delete some files not belong to you");
    }

    @GET
    @Path("/files/download")
    public Message download(@Context HttpServletResponse response, @QueryParam("id") Long id,@QueryParam("projectName")String projectName) {
        ProjectFiles projectFiles = projectManagerService.getFile(id, projectName);
        if (projectFiles == null) {
            return Message.error("no such file in this project");
        }
        if (StringUtils.isBlank(projectFiles.getStorePath())) {
            return Message.error("storePath is null");
        }
        response.setContentType("application/x-download");
        response.setHeader("content-Disposition", "attachment;filename=" + projectFiles.getFileName());
        try (InputStream is = projectManagerService.download(projectFiles);
             OutputStream os = response.getOutputStream()
        ) {
            int len = 0;
            byte[] arr = new byte[2048];
            while ((len = is.read(arr)) > 0) {
                os.write(arr, 0, len);
            }
            os.flush();
        } catch (Exception e) {
            LOG.error("download file: {} failed , message is : {}" , projectFiles.getFileName(), e);
            return Message.error(e.getMessage());
        }
        return Message.ok();
    }
}
