package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.manager;


import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.conf.JobLauncherConfiguration;
import org.apache.linkis.common.utils.RetryHandler;
import org.apache.linkis.errorcode.client.manager.LinkisErrorCodeManager;
import org.apache.linkis.errorcode.client.synchronizer.LinkisErrorCodeSynchronizer;
import org.apache.linkis.errorcode.common.LinkisErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamisErrorCodeManager   {
    private static StreamisErrorCodeManager streamisErrorCodeManager;


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisErrorCodeManager.class);



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
        List<LinkisErrorCode> linkisErrorCodes = LinkisErrorCodeSynchronizer.getInstance().synchronizeErrorCodes();
        String regex = JobLauncherConfiguration.LINKIS_LOG_Match().getHotValue();
        Pattern pattern= Pattern.compile(regex);
        List<LinkisErrorCode> errorCodes = new ArrayList<>();
        for (LinkisErrorCode item : linkisErrorCodes) {
            Matcher matcher= pattern.matcher(item.getErrorCode());
            if (matcher.find()) {
                errorCodes.add(item);
            }
        }
        return errorCodes;
    }
}
