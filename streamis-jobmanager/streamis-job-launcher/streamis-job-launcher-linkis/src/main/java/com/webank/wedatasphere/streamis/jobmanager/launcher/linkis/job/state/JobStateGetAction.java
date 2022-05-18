package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state;

import org.apache.linkis.httpclient.request.GetAction;
import org.apache.linkis.httpclient.request.UserAction;

public class JobStateGetAction extends GetAction implements UserAction {
    @Override
    public String getURL() {
        return null;
    }

    @Override
    public void setUser(String user) {

    }

    @Override
    public String getUser() {
        return null;
    }
}
