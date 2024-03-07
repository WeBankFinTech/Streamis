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


    public List<StreamErrorCode> getStreamErrorCodes() {
        LOGGER.info("加载streamis错误码");
        List<StreamErrorCode> streamErrorCodes = streamErrorCodeMapper.getErrorCodeList();
        LOGGER.info("加载完成，加载错误码个数为: {}", streamErrorCodes.size());
        return streamErrorCodes;
    }

    @PostConstruct
    private void init() {
        Utils.defaultScheduler().scheduleAtFixedRate(() -> {
            LOGGER.info("start to get errorCodes");
            synchronized (this.lock) {
                List<StreamErrorCode> streamErrorCodes = getStreamErrorCodes();
                if (!streamErrorCodes.isEmpty()){
                    StreamisErrorCodeCache.clear();
                    StreamisErrorCodeCache.put("data", streamErrorCodes);
                }
            }
        }, 0L, 1L, TimeUnit.HOURS);
    }

}
