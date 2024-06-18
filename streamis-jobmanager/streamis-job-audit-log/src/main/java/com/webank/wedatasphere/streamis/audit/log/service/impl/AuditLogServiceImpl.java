package com.webank.wedatasphere.streamis.audit.log.service.impl;

import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.streamis.audit.log.dao.StreamAuditLogMapper;
import com.webank.wedatasphere.streamis.audit.log.entity.StreamAuditLog;
import com.webank.wedatasphere.streamis.audit.log.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogService {
    @Autowired
    private StreamAuditLogMapper auditLogMapper;

    public PageInfo<StreamAuditLog> searchAuditLogs(String apiName, String user, String proxyUser, Date startDate, Date endDate,String projectName) {
        List<StreamAuditLog> streamAuditLogs = auditLogMapper.searchAuditLogs(apiName, user, proxyUser, startDate, endDate,projectName);
        return new PageInfo<>(streamAuditLogs);
    }

    @Override
    public void saveAuditLog(StreamAuditLog auditLog) {
        auditLogMapper.saveAuditLog(auditLog);
    }

    @Override
    public String getProjectNameById(Long jobId) {
        return auditLogMapper.getProjectNameById(jobId);
    }

}
