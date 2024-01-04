package com.webank.wedatasphere.streamis.jobmanager.restful.api;


import com.webank.wedatasphere.streamis.jobmanager.launcher.conf.JobConfKeyConstants;
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf.JobConf;
import com.webank.wedatasphere.streamis.jobmanager.launcher.service.StreamJobConfService;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJob;
import com.webank.wedatasphere.streamis.jobmanager.manager.project.service.ProjectPrivilegeService;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.StreamJobService;
import com.webank.wedatasphere.streamis.jobmanager.service.HighAvailableService;
import com.webank.wedatasphere.streamis.jobmanager.vo.HighAvailableMsg;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.utils.ModuleUserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Optional;

@RequestMapping(path = "/streamis/streamJobManager/highAvailable")
@RestController
public class HighAvailableRestfulApi {
    private static final Logger LOG = LoggerFactory.getLogger(JobBulkRestfulApi.class);
    @Autowired
    private StreamJobService streamJobService;
    @Resource
    private ProjectPrivilegeService privilegeService;
    @Autowired
    private StreamJobConfService streamJobConfService;
    @Autowired
    private HighAvailableService highAvailableService;

    @RequestMapping(path = "/getHAMsg", method = RequestMethod.GET)
    public Message getClusterMsg(HttpServletRequest request){
        Message result = Message.ok("success");
        String userName = ModuleUserUtils.getOperationUser(request, "query job config json");
        if (StringUtils.isBlank(userName)) return Message.error("current user has no permission");
        HighAvailableMsg msg = highAvailableService.getHighAvailableMsg();
        result.data("clusterName",msg.getClusterName());
        result.data("clusterIp",msg.getNodeIp());
        result.data("whetherManager",msg.getWhetherManager());
        return result;
    }
}
