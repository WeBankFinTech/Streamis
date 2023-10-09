package com.webank.wedatasphere.streamis.jobmanager.manager.utils;

import com.google.gson.JsonSyntaxException;
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf.JobConf;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.JobHighAvailableVo;
import com.webank.wedatasphere.streamis.jobmanager.manager.project.service.impl.ProjectPrivilegeServiceImpl;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SourceUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SourceUtils.class);

    public static JobHighAvailableVo manageJobProjectFile(String highAvailablePolicy,String source) {

        JobHighAvailableVo highAvailableVo = new JobHighAvailableVo();
        try {

            if (!Boolean.parseBoolean(JobConf.HIGHAVAILABLE_ENABLE().getValue().toString())) {
                highAvailableVo.setHighAvailable(true);
                highAvailableVo.setMsg("管理员未开启高可用配置");
                return highAvailableVo;
            } else {
                //查job conf  wds.streamis.app.highavailable.policy  值
                if (highAvailablePolicy.equals(JobConf.HIGHAVAILABLE_POLICY().getValue())){
                    Map map = BDPJettyServerHelper.gson().fromJson(source, Map.class);
                    if (map.containsKey("source")) {
                        String sourceValue = map.get("source").toString();
                        if (sourceValue.equals(JobConf.HIGHAVAILABLE_SOURCE().getValue())) {
                            if (map.containsKey("isHighAvailable")) {
                                highAvailableVo.setHighAvailable(Boolean.parseBoolean(map.get("isHighAvailable").toString()) );
                            }
                            highAvailableVo.setMsg(map.getOrDefault("highAvailableMessage","高可用信息为空，请联系管理员").toString());
                            return highAvailableVo;
                        }  else {
                            highAvailableVo.setHighAvailable(true);
                            highAvailableVo.setMsg("非标准来源,不检测高可用");
                            return highAvailableVo;
                        }
                    }   else {
                        highAvailableVo.setHighAvailable(true);
                        highAvailableVo.setMsg("非标准来源,不检测高可用");
                        return highAvailableVo;
                    }
                } else {
                    highAvailableVo.setHighAvailable(true);
                    highAvailableVo.setMsg("任务未开启高可用");
                    return highAvailableVo;
                }
            }
        } catch (JsonSyntaxException e) {
            // 转换失败，处理异常情况
            LOG.error("Failed to source parse JSON");
        }
        return highAvailableVo;
    }
}
