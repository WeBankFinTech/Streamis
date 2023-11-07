package com.webank.wedatasphere.streamis.audit.log.service.impl;

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

    public List<StreamAuditLog> searchAuditLogs(String apiName, String user, String proxyUser, Date startDate, Date endDate) {
        return auditLogMapper.searchAuditLogs(apiName, user, proxyUser, startDate, endDate);
    }

    @Override
    public void saveAuditLog(StreamAuditLog auditLog) {
        auditLogMapper.saveAuditLog(auditLog);
    }

}
