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

import org.apache.linkis.server.Message;
import com.webank.wedatasphere.streamis.jobmanager.exception.ProjectException;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.TaskCoreNumVO;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.JobService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Component
@Path("/streamis/streamJobManager/project")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectRestfulApi {

    @Autowired
    JobService jobService;
    @GET
    @Path("/core/target")
    public Response getView(@Context HttpServletRequest req, @QueryParam("projectName") String projectName) throws IOException, ProjectException {

        if(StringUtils.isBlank(projectName)){
            throw new ProjectException("params cannot be empty!");
        }
        TaskCoreNumVO taskCoreNumVO = jobService.countByCores(projectName);
        return  Message.messageToResponse(Message.ok().data("taskCore",taskCoreNumVO));
    }
}
