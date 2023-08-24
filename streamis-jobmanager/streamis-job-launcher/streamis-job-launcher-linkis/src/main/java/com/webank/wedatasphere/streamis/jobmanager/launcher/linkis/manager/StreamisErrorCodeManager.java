package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.manager;


import com.webank.wedatasphere.streamis.jobmanager.launcher.dao.StreamErrorCodeMapper;
import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.StreamErrorCode;
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.conf.JobLauncherConfiguration;
import org.apache.linkis.common.utils.RetryHandler;
import org.apache.linkis.errorcode.client.manager.LinkisErrorCodeManager;
import org.apache.linkis.errorcode.client.synchronizer.LinkisErrorCodeSynchronizer;
import org.apache.linkis.errorcode.common.LinkisErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class StreamisErrorCodeManager   {
    private static StreamisErrorCodeManager streamisErrorCodeManager;


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisErrorCodeManager.class);

    @Autowired
    private StreamErrorCodeMapper streamErrorCodeMapper;



    private StreamisErrorCodeManager() {}

    public static StreamisErrorCodeManager getInstance() {
        if (streamisErrorCodeManager == null) {
            synchronized (LinkisErrorCodeManager.class) {
                if (streamisErrorCodeManager == null) {
                    streamisErrorCodeManager = new StreamisErrorCodeManager();
                }
            }
        }
        return streamisErrorCodeManager;
    }

    public List<LinkisErrorCode> getLinkisErrorCodes() {
        LOGGER.info("加载linkis错误码");
        List<StreamErrorCode> linkisErrorCodes = streamErrorCodeMapper.getErrorCodeList();
        List<LinkisErrorCode> errorCodes = new ArrayList<>();
        for (StreamErrorCode item : linkisErrorCodes) {
            LinkisErrorCode errorCode = new LinkisErrorCode();
            errorCode.setErrorCode(item.getErrorCode());
            errorCode.setErrorDesc(item.getErrorDesc());
            errorCode.setErrorRegexStr(item.getErrorRegex());
            errorCode.setErrorType(item.getErrorType());
            errorCodes.add(errorCode);
        }
        LOGGER.info("加载完成，加载错误码个数为: {}",linkisErrorCodes.size());
        return errorCodes;
    }
}
