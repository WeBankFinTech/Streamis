package com.webank.wedatasphere.streamis.errorcode.manager;


import com.webank.wedatasphere.streamis.errorcode.dao.StreamErrorCodeMapper;
import com.webank.wedatasphere.streamis.errorcode.entity.StreamErrorCode;
import com.webank.wedatasphere.streamis.errorcode.manager.cache.StreamisErrorCodeCache;
import org.apache.linkis.common.utils.Utils;
import org.apache.linkis.errorcode.common.LinkisErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class StreamisErrorCodeManager {

    private final Object lock = new Object();

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisErrorCodeManager.class);

    @Autowired
    private StreamErrorCodeMapper streamErrorCodeMapper;


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
        LOGGER.info("加载完成，加载错误码个数为: {}", linkisErrorCodes.size());
        return errorCodes;
    }

    @PostConstruct
    private void init() {
        Utils.defaultScheduler().scheduleAtFixedRate(() -> {
            LOGGER.info("start to get errorCodes");
            synchronized (this.lock) {
                StreamisErrorCodeCache.clear();
                List<LinkisErrorCode> linkisErrorCodes = getLinkisErrorCodes();
                StreamisErrorCodeCache.put("data", linkisErrorCodes);
            }
        }, 0L, 1L, TimeUnit.HOURS);
    }

}
