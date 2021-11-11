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

import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.vo.ConfigKeyVO;
import com.webank.wedatasphere.streamis.jobmanager.launcher.exception.ConfigurationException;
import com.webank.wedatasphere.streamis.jobmanager.launcher.service.ConfigurationService;
import com.webank.wedatasphere.streamis.jobmanager.manager.util.CookieUtils;
import com.webank.wedatasphere.streamis.jobmanager.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;


@RequestMapping(path = "/streamis/streamJobManager/config")
@RestController
public class ConfigurationRestfulApi {
    @Autowired
    ConfigurationService configurationService;
    @Autowired
    UserService userService;

    ObjectMapper mapper = new ObjectMapper();


    @RequestMapping(path = "/view", method = RequestMethod.GET)
    public Message getView(@RequestParam(value = "jobId",required = false) Long jobId) throws IOException, ConfigurationException {
        if (jobId == null) {
            throw new ConfigurationException("params cannot be empty!");
        }
        ConfigKeyVO fullTree = configurationService.getFullTree(jobId);
        return Message.ok().data("fullTree", fullTree);
    }

    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public Message updateFullTree(@RequestBody JsonNode json) throws IOException {
        ConfigKeyVO fullTrees = mapper.readValue(json.get("fullTree"), ConfigKeyVO.class);

        configurationService.addKeyValue(fullTrees);
        return Message.ok();
    }

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public Message saveFullTree(@RequestBody JsonNode json) throws IOException {
        ConfigKeyVO fullTrees = mapper.readValue(json.get("fullTree"), ConfigKeyVO.class);

        configurationService.addKeyValue(fullTrees);
        return Message.ok();
    }

    @RequestMapping(path = "/getWorkspaceUsers", method = RequestMethod.GET)
    public Message getWorkspaceUsers(HttpServletRequest req) {
        //获取工作空间
        String workspaceId = CookieUtils.getCookieWorkspaceId(req);
        if (StringUtils.isBlank(workspaceId)) {
            return Message.error("无法获取到工作空间ID，请检查!");
        }
        String userName = SecurityFilter.getLoginUsername(req);
        List<String> list = userService.workspaceUserQuery(req, workspaceId);
        return Message.ok().data("users",list);

    }

}
