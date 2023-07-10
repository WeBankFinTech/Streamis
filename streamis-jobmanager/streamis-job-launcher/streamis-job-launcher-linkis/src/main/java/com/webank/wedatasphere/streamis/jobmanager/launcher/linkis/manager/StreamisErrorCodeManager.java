package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.manager;


import org.apache.linkis.errorcode.client.manager.LinkisErrorCodeManager;
import org.apache.linkis.errorcode.client.synchronizer.LinkisErrorCodeSynchronizer;
import org.apache.linkis.errorcode.common.LinkisErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamisErrorCodeManager   {
    private static StreamisErrorCodeManager streamisErrorCodeManager;

    private final LinkisErrorCodeSynchronizer linkisErrorCodeSynchronizer = LinkisErrorCodeSynchronizer.getInstance();

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
        List<LinkisErrorCode> linkisErrorCodes = linkisErrorCodeSynchronizer.synchronizeErrorCodes();
//        String regex="^(10|20|30|40)\\d+"; //comm
//        Pattern pattern= Pattern.compile(regex);
//        List<LinkisErrorCode> errorCodes = new ArrayList<>();
//        for (LinkisErrorCode item : linkisErrorCodes) {
//            Matcher matcher= pattern.matcher(item.getErrorCode());
//            if (matcher.find()) {
//                errorCodes.add(item);
//            }
//        }
        return linkisErrorCodes;
    }
}
