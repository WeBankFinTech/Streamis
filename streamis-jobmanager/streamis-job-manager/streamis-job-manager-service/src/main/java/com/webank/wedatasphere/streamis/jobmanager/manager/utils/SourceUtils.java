package com.webank.wedatasphere.streamis.jobmanager.manager.utils;

import com.google.gson.JsonSyntaxException;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.JobHighAvailableVo;
import com.webank.wedatasphere.streamis.jobmanager.manager.project.service.impl.ProjectPrivilegeServiceImpl;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SourceUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SourceUtils.class);
    public static JobHighAvailableVo manageJobProjectFile(String source) {
        JobHighAvailableVo highAvailableVo = new JobHighAvailableVo();
        try {
            Map map = BDPJettyServerHelper.gson().fromJson(source, Map.class);
            if (map.containsKey("isHighAvailable")) {
                highAvailableVo.setHighAvailable((Boolean) map.get("isHighAvailable"));
            }
            if (map.containsKey("highAvailableMessage")) {
                highAvailableVo.setMsg(map.get("highAvailableMessage").toString());
            }
            return highAvailableVo;
        } catch (JsonSyntaxException e) {
            // 转换失败，处理异常情况
            LOG.error("Failed to source parse JSON" );
        }
        return highAvailableVo;
    }
}
