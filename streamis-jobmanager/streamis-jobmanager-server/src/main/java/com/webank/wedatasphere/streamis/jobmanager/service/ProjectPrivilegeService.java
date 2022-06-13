package com.webank.wedatasphere.streamis.jobmanager.service;

import javax.servlet.http.HttpServletRequest;

/**
 * Privilege  service
 */
public interface ProjectPrivilegeService {

    /**
     * Has release privilege
     * @param req request
     * @param projectName project name
     * @return boolean
     */
    Boolean hasReleasePrivilege(HttpServletRequest req, String projectName);

    /**
     * Has edit privilege
     * @param req request
     * @param projectName project name
     * @return boolean
     */
    Boolean hasEditPrivilege(HttpServletRequest req, String projectName);

    /**
     * Has access privilege
     * @param req request
     * @param projectName project name
     * @return
     */
    Boolean hasAccessPrivilege(HttpServletRequest req, String projectName);

}
