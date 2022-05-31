package com.webank.wedatasphere.streamis.projectmanager.service.impl;

import com.webank.wedatasphere.streamis.projectmanager.service.ProjectPrivilegeService;
import org.apache.linkis.server.conf.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProjectPrivilegeServiceImpl implements ProjectPrivilegeService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectPrivilegeServiceImpl.class);

    @Autowired
    RestTemplate restTemplate;

    //TODO get ip:port configution
    private String url_prefix = "ip:port"+ ServerConfiguration.BDP_SERVER_RESTFUL_URI().getValue()+ "/streamis/project/projectPrivilege";

    @Override
    public Boolean hasReleasePrivilege(HttpServletRequest req, String projectId) {
        Map<String, Object> responseData = getResponseData("/hasReleasePrivilege", req, projectId);
        return (Boolean)Optional.ofNullable(responseData).orElse(new HashMap<>()).getOrDefault("releasePrivilege",false);
    }

    @Override
    public Boolean hasEditPrivilege(HttpServletRequest req, String projectId) {
        Map<String, Object> responseData = getResponseData("/hasEditPrivilege", req, projectId);
        return (Boolean)Optional.ofNullable(responseData).orElse(new HashMap<>()).getOrDefault("editPrivilege",false);
    }

    @Override
    public Boolean hasAccessPrivilege(HttpServletRequest req, String projectId) {
        Map<String, Object> responseData = getResponseData("/hasAccessPrivilege", req, projectId);
        return (Boolean)Optional.ofNullable(responseData).orElse(new HashMap<>()).getOrDefault("accessPrivilege",false);
    }

    private Map<String, Object> getResponseData(String reqPath,HttpServletRequest req,String projectId){
        String url = url_prefix + reqPath;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie",req.getHeader("Cookie"));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map.class, projectId);
        LOG.info("obtain the operation privilege of the user,return response:{}", responseEntity.toString());
        if(responseEntity.getBody()!=null && "0".equals(responseEntity.getBody().get("status"))){
            return (Map<String, Object>) responseEntity.getBody().get("data");
        }else{
            LOG.error("user failed to obtain the privilege information");
            return null;
        }
    }

}
