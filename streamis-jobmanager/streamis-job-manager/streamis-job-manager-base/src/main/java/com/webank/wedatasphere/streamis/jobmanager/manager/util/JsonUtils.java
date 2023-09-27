package com.webank.wedatasphere.streamis.jobmanager.manager.util;

import com.google.gson.JsonSyntaxException;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    private static final Logger LOG = LoggerFactory.getLogger(JsonUtils.class);
    public static String manageArgs(String jobContent, List<String> args){
        try {
            Map map = BDPJettyServerHelper.gson().fromJson(jobContent, Map.class);
            map.put("args",args);
            return BDPJettyServerHelper.gson().toJson(map);
        }  catch (JsonSyntaxException e) {
            LOG.error("Failed to source parse JSON");
        }
        return null;
    }

    public static String manageSource(String getSource,Boolean isHighAvailable , String highAvailableMessage){
        HashMap<Object, Object> hashMap = new HashMap<>();
        try {
            hashMap = BDPJettyServerHelper.gson().fromJson(getSource, HashMap.class);
            hashMap.put("isHighAvailable",isHighAvailable);
            hashMap.put("highAvailableMessage",highAvailableMessage);
        }  catch (JsonSyntaxException e) {
            LOG.error("Failed to source parse JSON");
            hashMap.put("isHighAvailable",isHighAvailable);
            hashMap.put("highAvailableMessage",highAvailableMessage);
        }
        return BDPJettyServerHelper.gson().toJson(hashMap);
    }
}
