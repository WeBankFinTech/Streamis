package com.webank.wedatasphere.streamis.appconn.action;

import com.webank.wedatasphere.linkis.httpclient.request.POSTAction;
import com.webank.wedatasphere.linkis.httpclient.request.UserAction;

/**
 * created by yangzhiyue on 2021/4/8
 * Description:
 */
public class StreamisPostAction extends POSTAction implements UserAction {

    private String url;
    private String requestPayLoad;
    private String user;


    @Override
    public String getURL() {
        return null;
    }

    @Override
    public String getRequestPayload() {
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
