package com.webank.wedatasphere.streamis.jobmanager.appconn.action;

import com.webank.wedatasphere.linkis.httpclient.request.POSTAction;
import com.webank.wedatasphere.linkis.httpclient.request.UserAction;

/**
 * created by yangzhiyue on 2021/4/16
 * Description:
 */
public class JMPostAction extends POSTAction implements UserAction {

    private String url;

    private String user;

    private static final String DEFAULT_USER = "hadoop";

    private String requestPayload;

    public JMPostAction(String url){
        this(url, DEFAULT_USER);
    }

    public JMPostAction(String url, String user){
        this.url = url;
        this.user = user;
    }

    @Override
    public String getURL() {
        return this.url;
    }

    @Override
    public String getRequestPayload() {
        return this.requestPayload;
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
