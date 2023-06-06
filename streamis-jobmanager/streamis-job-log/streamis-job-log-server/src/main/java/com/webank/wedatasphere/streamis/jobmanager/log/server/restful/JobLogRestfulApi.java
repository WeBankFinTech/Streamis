package com.webank.wedatasphere.streamis.jobmanager.log.server.restful;

import com.webank.wedatasphere.streamis.jobmanager.log.server.config.StreamJobLogConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.server.entities.StreamisLogEvents;
import com.webank.wedatasphere.streamis.jobmanager.log.server.exception.StreamJobLogException;
import com.webank.wedatasphere.streamis.jobmanager.log.server.service.StreamisJobLogService;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/streamis/streamJobManager/log")
public class JobLogRestfulApi {


    @Resource
    private StreamisJobLogService streamisJobLogService;

    @RequestMapping(value = "/collect/events", method = RequestMethod.POST)
    public Message collectEvents(@RequestBody StreamisLogEvents events, HttpServletRequest request){
        Message result;
        try{
            if (StringUtils.isBlank(events.getAppName())){
                return Message.ok("Ignore the stream log events without application name");
            }
            String userName;
            if (StreamJobLogConfig.NO_AUTH_REST.getValue()){
                userName = request.getHeader("Token-User");
                if (StringUtils.isBlank(userName)){
                    userName = SecurityFilter.getLoginUsername(request);
                    if (StringUtils.isBlank(userName)){
                        userName = "hadoop";
                    }
                }
            }else {
                userName = SecurityFilter.getLoginUsername(request);
                if (StringUtils.isBlank(userName)) {
                    throw new StreamJobLogException(-1, "The request should has token user");
                }
            }
            this.streamisJobLogService.store(userName, events);
            result = Message.ok();
        }catch (Exception e){
            String message = "Fail to collect stream log events, message: " + e.getMessage();
            result = Message.error(message);
        }
        return result;
    }

}
