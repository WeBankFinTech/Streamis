package com.webank.wedatasphere.streamis.jobmanager.appconn.standard;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.standard.app.development.AbstractLabelDevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.RefOperationService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;

import java.io.IOException;
import java.util.List;

/**
 * created by yangzhiyue on 2021/4/14
 * Description:
 * StreamisJMDevStandard
 */
public class StreamisJMDevStandard extends AbstractLabelDevelopmentIntegrationStandard {


    private static StreamisJMDevStandard instance;

    private AppConn appConn;

    private AppDesc appDesc;






    private StreamisJMDevStandard(AppConn appConn){
        this.appConn = appConn;
        this.appDesc = appConn.getAppDesc();
        init();
    }


    public static StreamisJMDevStandard getInstance(AppConn appConn){
        if (null == instance){
            synchronized (StreamisJMDevStandard.class){
                if (null == instance){
                    instance = new StreamisJMDevStandard(appConn);
                }
            }
        }
        return instance;
    }

    @Override
    public void init()  {

    }




    @Override
    protected List<RefOperationService> getRefOperationService() {
        return null;
    }

    @Override
    public AppDesc getAppDesc() {
        return null;
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {

    }



    @Override
    public void close() throws IOException {

    }
}
