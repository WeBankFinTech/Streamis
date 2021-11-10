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

package com.webank.wedatasphere.streamis.jobmanager.service.impl;

import org.apache.linkis.common.conf.Configuration;
import org.apache.linkis.server.conf.ServerConfiguration;
import com.webank.wedatasphere.streamis.jobmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by v_wbyynie on 2021/9/27.
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private RestTemplate restTemplate;
    @Override
    public List<String> workspaceUserQuery(HttpServletRequest req,String workspaceId) {
        String url = Configuration.getGateWayURL() + ServerConfiguration.BDP_SERVER_RESTFUL_URI().getValue() + "/dss/framework/workspace/getAllWorkspaceUsers?workspaceId=" + workspaceId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cookie", req.getHeader("Cookie"));
        HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Map.class);
        LinkedHashMap<String, ArrayList> data = (LinkedHashMap<String, ArrayList>) response.getBody().get("data");
        return data.get("users");
    }
}
