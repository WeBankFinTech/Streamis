package com.webank.wedatasphere.streamis.audit.log.restful.api;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.streamis.audit.log.entity.StreamAuditLog;
import com.webank.wedatasphere.streamis.audit.log.service.AuditLogService;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.QueryJobListVo;
import com.webank.wedatasphere.streamis.jobmanager.manager.project.service.ProjectPrivilegeService;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.StreamJobService;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.utils.ModuleUserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RequestMapping(path = "/streamis/streamJobManager/audit")
@RestController
public class AuditLogRestfulApi {

    @Autowired
    private AuditLogService auditLogService;

    @Resource
    private ProjectPrivilegeService privilegeService;

    @GetMapping("/logs")
    public Message searchAuditLogs(HttpServletRequest req,
                                   @RequestParam(value = "pageNow", required = false) Integer pageNow,
                                   @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                   @RequestParam(value = "apiName", required = false) String apiName,
                                   @RequestParam(value = "user", required = false) String user,
                                   @RequestParam(value = "proxyUser", required = false) String proxyUser,
                                   @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startDate,
                                   @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endDate,
                                   @RequestParam(value = "projectName",required = false) String projectName
                                   ) {
        String userName = ModuleUserUtils.getOperationUser(req, "Query job audit log");
        if(StringUtils.isBlank(projectName)){
            return Message.error("Project name cannot be empty(项目名不能为空，请指定)");
        }
        if (!privilegeService.hasAccessPrivilege(req,projectName)) {
            return Message.error("the current user has no operation permission");
        }
        if (Objects.isNull(pageNow)) {
            pageNow = 1;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = 20;
        }
        PageInfo<StreamAuditLog> pageInfo;
        PageHelper.startPage(pageNow, pageSize);
        try {
            pageInfo = auditLogService.searchAuditLogs(apiName, user, proxyUser, startDate, endDate,projectName);
        } finally {
            PageHelper.clearPage();
        }

        return Message.ok().data("auditLogs",pageInfo.getList()).data("totalPage", pageInfo.getTotal());
    }
}
