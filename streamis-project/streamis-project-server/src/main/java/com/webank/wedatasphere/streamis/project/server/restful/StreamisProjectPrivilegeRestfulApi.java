package com.webank.wedatasphere.streamis.project.server.restful;

import com.webank.wedatasphere.streamis.project.server.entity.StreamisProjectPrivilege;
import com.webank.wedatasphere.streamis.project.server.service.StreamisProjectPrivilegeService;
import com.webank.wedatasphere.streamis.project.server.utils.StreamisProjectRestfulUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping(path = "/streamis/project/projectPrivilege")
@RestController
public class StreamisProjectPrivilegeRestfulApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisProjectPrivilegeRestfulApi.class);

    @Autowired
    private StreamisProjectPrivilegeService projectPrivilegeService;

    @RequestMapping(path = "/getProjectPrivilege", method = RequestMethod.GET)
    public Message getProjectPrivilege(HttpServletRequest request, @RequestParam(value = "projectId", required = false) Long projectId) {
        String username = SecurityFilter.getLoginUsername(request);
        try {
            List<StreamisProjectPrivilege> projectPrivileges = projectPrivilegeService.getProjectPrivilege(projectId, username);
            return StreamisProjectRestfulUtils.dealOk("Successfully obtained the projectPrivileges",
                    new Pair<>("projectPrivileges", projectPrivileges));
        } catch (Exception e) {
            LOGGER.error("failed to obtain the release privilege for user {}", username, e);
            return StreamisProjectRestfulUtils.dealError("failed to obtain the release privilege, reason is:" + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    @RequestMapping(path = "/hasReleasePrivilege", method = RequestMethod.GET)
    public Message hasReleaseProjectPrivilege(HttpServletRequest request, @RequestParam(value = "projectId", required = false) Long projectId) {
        String username = SecurityFilter.getLoginUsername(request);
        try {
            boolean hasReleaseProjectPrivilege = projectPrivilegeService.hasReleaseProjectPrivilege(projectId, username);
            return StreamisProjectRestfulUtils.dealOk("Successfully obtained the release privilege",
                    new Pair<>("releasePrivilege", hasReleaseProjectPrivilege));
        } catch (Exception e) {
            LOGGER.error("failed to obtain the release privilege for user {}", username, e);
            return StreamisProjectRestfulUtils.dealError("failed to obtain the release privilege, reason is:" + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    @RequestMapping(path = "/hasEditPrivilege", method = RequestMethod.GET)
    public Message hasEditProjectPrivilege(HttpServletRequest request, @RequestParam(value = "projectId", required = false) Long projectId) {
        String username = SecurityFilter.getLoginUsername(request);
        try {
            boolean hasEditProjectPrivilege = projectPrivilegeService.hasEditProjectPrivilege(projectId, username);
            return StreamisProjectRestfulUtils.dealOk("Successfully obtained the release privilege",
                    new Pair<>("editPrivilege", hasEditProjectPrivilege));
        } catch (Exception e) {
            LOGGER.error("failed to obtain the edit privilege for user {}", username, e);
            return StreamisProjectRestfulUtils.dealError("failed to obtain the edit privilege, reason is:" + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    @RequestMapping(path = "/hasAccessPrivilege", method = RequestMethod.GET)
    public Message hasAccessProjectPrivilege(HttpServletRequest request, @RequestParam(value = "projectId", required = false) Long projectId) {
        String username = SecurityFilter.getLoginUsername(request);
        try {
            boolean hasAccessProjectPrivilege = projectPrivilegeService.hasAccessProjectPrivilege(projectId, username);
            return StreamisProjectRestfulUtils.dealOk("Successfully obtained the release privilege",
                    new Pair<>("accessPrivilege", hasAccessProjectPrivilege));
        } catch (Exception e) {
            LOGGER.error("failed to obtain the access privilege for user {}", username, e);
            return StreamisProjectRestfulUtils.dealError("failed to obtain the access privilege, reason is:" + ExceptionUtils.getRootCauseMessage(e));
        }
    }
}