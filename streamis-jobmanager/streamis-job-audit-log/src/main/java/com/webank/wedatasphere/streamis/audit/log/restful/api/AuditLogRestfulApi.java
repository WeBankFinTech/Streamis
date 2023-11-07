package com.webank.wedatasphere.streamis.audit.log.restful.api;

import com.webank.wedatasphere.streamis.audit.log.entity.StreamAuditLog;
import com.webank.wedatasphere.streamis.audit.log.service.AuditLogService;
import com.webank.wedatasphere.streamis.jobmanager.manager.project.service.ProjectPrivilegeService;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.StreamJobService;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.utils.ModuleUserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RequestMapping(path = "/streamis/streamJobManager/audit")
@RestController
public class AuditLogRestfulApi {

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private StreamJobService streamJobService;

    @Resource
    private ProjectPrivilegeService privilegeService;

    @GetMapping("/logs")
    public Message searchAuditLogs(HttpServletRequest req,
                                   @RequestParam(value = "apiName", required = false) String apiName,
                                   @RequestParam(value = "user", required = false) String user,
                                   @RequestParam(value = "proxyUser", required = false) String proxyUser,
                                   @RequestParam(value = "startDate", required = false) Date startDate,
                                   @RequestParam(value = "endDate", required = false) Date endDate,
                                   @RequestParam(value = "projectName",required = false) String projectName
                                   ) {
        String userName = ModuleUserUtils.getOperationUser(req, "Query job audit log");
        if (!privilegeService.hasAccessPrivilege(req,projectName)) {
            return Message.error("the current user has no operation permission");
        }
        List<StreamAuditLog> streamAuditLogList = auditLogService.searchAuditLogs(apiName, user, proxyUser, startDate, endDate);
        return Message.ok().data("auditLogs",streamAuditLogList);
    }
}
