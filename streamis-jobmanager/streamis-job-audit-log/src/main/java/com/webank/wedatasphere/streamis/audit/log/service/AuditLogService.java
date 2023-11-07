package com.webank.wedatasphere.streamis.audit.log.service;

import com.webank.wedatasphere.streamis.audit.log.entity.StreamAuditLog;


import java.util.Date;
import java.util.List;

public interface AuditLogService {

    List<StreamAuditLog> searchAuditLogs(String apiName, String user, String proxyUser, Date startDate, Date endDate);

    void  saveAuditLog(StreamAuditLog auditLog);
}
