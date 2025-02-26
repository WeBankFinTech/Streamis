package com.webank.wedatasphere.streamis.jobmanager.manager.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonSyntaxException;
import com.webank.wedatasphere.streamis.jobmanager.manager.exception.JsonFormatWarnException;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    private JsonUtils(){}
    private static final Logger LOG = LoggerFactory.getLogger(JsonUtils.class);
    public static String manageArgs(String jobContent, List<String> args){
        try {
            Map<String,Object> map = BDPJettyServerHelper.gson().fromJson(jobContent, Map.class);
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

    public static ObjectMapper jackson =  new ObjectMapper().setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"))
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public static String toString(Object object) {
        if(object == null) {
            return null;
        }
        return LambdaUtils.supply(() -> jackson.writeValueAsString(object),
                e -> new JsonFormatWarnException(1000100, "反序列化 " + object.getClass().getSimpleName() + " 失败！"));
    }

    public static Map<String, Object> stringToMap(String str) {
        return stringToObject(str, Map.class);
    }

    public static <T> T stringToObject(String str, Class<T> clazz) {
        if(StringUtils.isBlank(str)) {
            return null;
        }
        return LambdaUtils.supply(() -> jackson.readValue(str, clazz),
                e -> new JsonFormatWarnException(1000100,  str + " 序列化失败！"));
    }
}
