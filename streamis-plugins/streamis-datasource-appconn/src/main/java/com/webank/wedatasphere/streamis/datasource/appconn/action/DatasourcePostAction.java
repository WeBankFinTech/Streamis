package com.webank.wedatasphere.streamis.datasource.appconn.action;

import com.webank.wedatasphere.linkis.httpclient.request.POSTAction;
import com.webank.wedatasphere.linkis.httpclient.request.UserAction;

/**
 * created by yangzhiyue on 2021/4/12
 * Description:
 */
public class DatasourcePostAction extends POSTAction implements UserAction {

    private String user;
    private String url;
    private String requestPayload;

    private static final String DEFAULT_USER = "hadoop";


    public DatasourcePostAction(String url){
        this(url, DEFAULT_USER);
    }

    public DatasourcePostAction(String url, String user){
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

    public void setRequestPayload(String requestPayload){
        this.requestPayload = requestPayload;
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
