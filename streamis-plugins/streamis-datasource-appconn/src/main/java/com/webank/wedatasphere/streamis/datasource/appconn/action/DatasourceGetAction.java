package com.webank.wedatasphere.streamis.datasource.appconn.action;

import com.webank.wedatasphere.linkis.httpclient.request.GetAction;
import com.webank.wedatasphere.linkis.httpclient.request.UserAction;

/**
 * created by yangzhiyue on 2021/4/12
 * Description:
 */
public class DatasourceGetAction extends GetAction implements UserAction {


    private String url;

    private String user;

    private static final String DEFAULT_USER = "hadoop";


    public DatasourceGetAction(String url){
        this(url, DEFAULT_USER);
    }

    public DatasourceGetAction(String url, String user){
        this.url = url;
        this.user = user;
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
