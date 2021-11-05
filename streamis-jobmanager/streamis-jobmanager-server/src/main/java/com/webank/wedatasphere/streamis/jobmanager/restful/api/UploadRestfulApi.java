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

package com.webank.wedatasphere.streamis.jobmanager.restful.api;

import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import com.webank.wedatasphere.streamis.jobmanager.exception.JobException;
import com.webank.wedatasphere.streamis.jobmanager.exception.JobExceptionManager;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJobVersion;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.BMLService;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.JobService;
import com.webank.wedatasphere.streamis.jobmanager.manager.util.IoUtils;
import com.webank.wedatasphere.streamis.jobmanager.manager.util.ZipHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * created by cooperyang on 2021/6/17
 * Description:
 */
@Component
@Path("/streamis/streamJobManager/job")
public class UploadRestfulApi {

    private static final Logger LOG = LoggerFactory.getLogger(UploadRestfulApi.class);

    @Autowired
    private JobService jobService;

    @Autowired
    private BMLService bmlService;

    @POST
    @Path("/upload")
    public Response uploadJar(@Context HttpServletRequest req, FormDataMultiPart form) throws IOException, JobException {

        String userName =  SecurityFilter.getLoginUsername(req);
        List<FormDataBodyPart> files = form.getFields("file");
        if (files == null || files.size() <= 0) {
            throw JobExceptionManager.createException(30300, "uploaded files");
        }
        //Only uses 1st file(只取第一个文件)
        FormDataBodyPart p = files.get(0);
        FormDataContentDisposition fileDetail = p.getFormDataContentDisposition();
        String fileName = new String(fileDetail.getFileName().getBytes("ISO8859-1"), StandardCharsets.UTF_8);
        if(!ZipHelper.isZip(fileName)){
            throw JobExceptionManager.createException(30302);
        }
        InputStream is = null;
        OutputStream os = null;
        try{
            String inputPath = IoUtils.generateIOPath(userName, "streamis", fileName);
            File file = new File(inputPath);
            if(file.getParentFile().exists()){
                FileUtils.deleteDirectory(file.getParentFile());
            }
            is = p.getValueAs(InputStream.class);
            os = IoUtils.generateExportOutputStream(inputPath);
            IOUtils.copy(is, os);
            StreamJobVersion job = jobService.uploadJob(userName, inputPath);
            return Message.messageToResponse(Message.ok().data("jobId",job.getJobId()));
        } catch(ErrorException e){
            LOG.error("Failed to upload zip(zip上传失败)", e);
            return Message.messageToResponse(Message.error(e.getDesc()));
        } catch (Exception e){
            LOG.error("failed to upload zip {} fo user {}", fileName, userName, e);
            return Message.messageToResponse(Message.error(e.getMessage()));
        }
        finally{
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(is);
        }

    }
}
