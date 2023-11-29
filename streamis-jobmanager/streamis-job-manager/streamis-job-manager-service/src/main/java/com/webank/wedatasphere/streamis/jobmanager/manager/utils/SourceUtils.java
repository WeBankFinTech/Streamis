package com.webank.wedatasphere.streamis.jobmanager.manager.utils;

import com.google.gson.JsonSyntaxException;
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf.JobConf;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.JobHighAvailableVo;
import com.webank.wedatasphere.streamis.jobmanager.manager.project.service.impl.ProjectPrivilegeServiceImpl;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class SourceUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SourceUtils.class);

    public static JobHighAvailableVo manageJobProjectFile(String highAvailablePolicy,String source) {
        highAvailablePolicy = Optional.ofNullable(highAvailablePolicy).orElse(JobConf.HIGHAVAILABLE_DEFAULT_POLICY().getHotValue());
        JobHighAvailableVo highAvailableVo = new JobHighAvailableVo();
        if (source.equalsIgnoreCase("update args")) {
            highAvailableVo.setHighAvailable(true);
            highAvailableVo.setMsg("用户在页面手动修改args,任务不再支持高可用");
            return highAvailableVo;
        }
        try {
            if (!Boolean.parseBoolean(JobConf.HIGHAVAILABLE_ENABLE().getValue().toString())) {
                highAvailableVo.setHighAvailable(true);
                highAvailableVo.setMsg("管理员未开启高可用配置");
                return highAvailableVo;
            } else {
                //查job conf  wds.streamis.app.highavailable.policy  值
                if (highAvailablePolicy.equals(JobConf.HIGHAVAILABLE_POLICY_DOUBLE().getValue()) || highAvailablePolicy.equals(JobConf.HIGHAVAILABLE_POLICY_DOUBLE_BAK().getValue())){
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
            highAvailableVo.setHighAvailable(true);
            highAvailableVo.setMsg("任务未开启高可用");
            LOG.error("Failed to source parse JSON");
        }
        return highAvailableVo;
    }
}
