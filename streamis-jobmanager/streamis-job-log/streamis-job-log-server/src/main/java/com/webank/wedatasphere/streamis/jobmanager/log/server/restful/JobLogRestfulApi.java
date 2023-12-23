package com.webank.wedatasphere.streamis.jobmanager.log.server.restful;


import com.webank.wedatasphere.streamis.jobmanager.log.entities.StreamisHeartbeat;
import com.webank.wedatasphere.streamis.jobmanager.log.server.config.StreamJobLogConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.server.entities.StreamisLogEvents;
import com.webank.wedatasphere.streamis.jobmanager.log.server.exception.StreamJobLogException;
import com.webank.wedatasphere.streamis.jobmanager.log.server.service.StreamisJobLogService;
import com.webank.wedatasphere.streamis.jobmanager.log.server.service.StreamisRegisterService;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamRegister;
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
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

@RestController
@RequestMapping(path = "/streamis/streamJobManager/log")
public class JobLogRestfulApi {

    private static final Logger LOG = LoggerFactory.getLogger(JobLogRestfulApi.class);
    @Resource
    private StreamisJobLogService streamisJobLogService;

    @Resource
    private StreamisRegisterService streamisRegisterService;

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
                    try {
                        userName = SecurityFilter.getLoginUsername(request);
                    }catch(Exception e){
                        LOG.error("获取登录用户失败. {}", e.getMessage(), e);
                    }
                    if (StringUtils.isBlank(userName)){
                        LOG.error("获取登录用户失败, 使用默认用户: hadoop");
                        userName = "hadoop";
                    }
                }
            }else {
                userName = SecurityFilter.getLoginUsername(request);
                if (StringUtils.isBlank(userName)) {
                    throw new StreamJobLogException(-1, "The request should has token user");
                }
            }
            String xForwardedForHeader = request.getHeader("X-Forwarded-For");
            if (xForwardedForHeader == null || xForwardedForHeader.isEmpty() || "unknown".equalsIgnoreCase(xForwardedForHeader)) {
                xForwardedForHeader =  request.getRemoteAddr();
            }
            String eventIp = xForwardedForHeader.split(",")[0].trim();
            Arrays.stream(events.getEvents())
                    .forEach(event -> event.setContent("ip: " +eventIp + event.getContent()));
            this.streamisJobLogService.store(userName, events);
            result = Message.ok();
        }catch (Exception e){
            String message = "Fail to collect stream log events, message: " + e.getMessage();
            result = Message.error(message);
        }
        return result;
    }

    @RequestMapping(value = "/heartbeat ", method = RequestMethod.POST)
    public Message logHeartbeat(@RequestBody StreamisHeartbeat streamisHeartbeat, HttpServletRequest request){
        ConcurrentLinkedDeque<String> registerDeque = new ConcurrentLinkedDeque<>();
        String applicationName = streamisHeartbeat.getApplicationName();
        if (streamisHeartbeat.getSign().equals("register")){
            String password =streamisHeartbeat.getPasswordOrHeartbeat();
            byte[] decodedBytes = Base64.getDecoder().decode(password);
            String decodedString = new String(decodedBytes);
            if (decodedString.equals("streamisRegister")){
                StreamRegister streamRegister = new StreamRegister();
                streamRegister.setApplicationName(applicationName);
                streamRegister.setPassword(password);
                streamRegister.setRegisterTime(new Date());
                streamRegister.setHeartbeatTime(new Date());
                StreamRegister info = streamisRegisterService.getInfoByApplicationName(applicationName);
                if (info == null){
                    streamisRegisterService.addStreamRegister(streamRegister);
                } else {
                    streamRegister.setRegisterTime(new Date());
                    streamisRegisterService.updateRegisterTime(streamRegister);
                }
                return  Message.ok();
            } else {
                return Message.error(applicationName + "Password error");
            }
        } else if (streamisHeartbeat.getSign().equals("heartbeat")){
            StreamRegister streamRegister = new StreamRegister();
            streamRegister.setApplicationName(applicationName);
            streamRegister.setHeartbeatTime(new Date());
            streamisRegisterService.updateHeartbeatTime(streamRegister);
            return Message.ok();
        } else {
            return Message.error("Unlawful request");
        }
    }

}
