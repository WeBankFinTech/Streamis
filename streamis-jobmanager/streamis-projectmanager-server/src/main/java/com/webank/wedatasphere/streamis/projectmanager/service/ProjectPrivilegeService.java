package com.webank.wedatasphere.streamis.projectmanager.service;

import javax.servlet.http.HttpServletRequest;

public interface ProjectPrivilegeService {

    Boolean hasReleasePrivilege(HttpServletRequest req, String projectId);

    Boolean hasEditPrivilege(HttpServletRequest req, String projectId);

    Boolean hasAccessPrivilege(HttpServletRequest req, String projectId);

}
