package com.webank.wedatasphere.streamis.jobmanager.appconn.action;

import com.webank.wedatasphere.linkis.httpclient.request.GetAction;
import com.webank.wedatasphere.linkis.httpclient.request.UserAction;

/**
 * created by yangzhiyue on 2021/4/16
 * Description:
 */
public class JMGetAction extends GetAction implements UserAction {


    private String user;
    private String url;

    private static final String DEFAULT_USER = "hadoop";

    public JMGetAction(String url){
        this(url, DEFAULT_USER);
    }

    public JMGetAction(String url, String user){
        this.user = user;
        this.url = url;
    }

    @Override
    public String getURL() {
        return this.url;
    }

    @Override
    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String getUser() {
        return this.user;
    }
}
