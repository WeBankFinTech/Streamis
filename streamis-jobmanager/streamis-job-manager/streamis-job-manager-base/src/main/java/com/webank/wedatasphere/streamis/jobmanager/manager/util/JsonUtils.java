package com.webank.wedatasphere.streamis.jobmanager.manager.util;

import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.JobHighAvailableVo;
import org.apache.linkis.server.BDPJettyServerHelper;

import java.util.List;
import java.util.Map;

public class JsonUtils {

    public static String manageJobContent(String jobContent, List<String> args){
        Map map = BDPJettyServerHelper.gson().fromJson(jobContent, Map.class);
        map.put("args",args);
        return BDPJettyServerHelper.gson().toJson(map);
    }
}
