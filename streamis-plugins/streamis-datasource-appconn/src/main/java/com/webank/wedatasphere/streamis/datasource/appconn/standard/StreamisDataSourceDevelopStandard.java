package com.webank.wedatasphere.streamis.datasource.appconn.standard;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.standard.app.development.AbstractLabelDevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.RefOperationService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * created by yangzhiyue on 2021/4/8
 * Description:
 * this is the streamis datasource development standard.
 * it can be used to communicate with streamis datasource
 * it usually will be used in workflow, StreamisJobmanager_Appconn
 * It should be singleton
 */
public class StreamisDataSourceDevelopStandard extends AbstractLabelDevelopmentIntegrationStandard {



    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisDataSourceDevelopStandard.class);


    private static StreamisDataSourceDevelopStandard instance;

    private AppConn appConn;

    private StreamisDataSourceDevelopStandard(AppConn appConn){
        this.appConn = appConn;
    }


    public static StreamisDataSourceDevelopStandard getInstance(AppConn appConn){

        if(instance == null){
            synchronized (StreamisDataSourceDevelopStandard.class){
                if (instance == null){
                     instance = new StreamisDataSourceDevelopStandard(appConn);
                }
            }
        }
        return instance;
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
    public void init() throws AppStandardErrorException {

    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public String getStandardName() {
        return null;
    }

    @Override
    public int getGrade() {
        return 0;
    }

    @Override
    public boolean isNecessary() {
        return false;
    }
}
