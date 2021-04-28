package com.webank.wedatasphere.streamis.datasource.appconn.standard;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.standard.app.development.AbstractLabelDevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.RefOperationService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.streamis.datasource.appconn.service.StreamisDatasourceQueryService;
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


    private List<RefOperationService> refOperationServiceList;

    private final Object LOCK = new Object();

    private StreamisDataSourceDevelopStandard(AppConn appConn){
        this.appConn = appConn;
        init();
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
        if (null == refOperationServiceList){
            synchronized (LOCK){
                if (null == refOperationServiceList){
                    refOperationServiceList = Lists.newArrayList(new StreamisDatasourceQueryService(appConn));
                }
            }
        }
        return refOperationServiceList;
    }

    @Override
    public AppDesc getAppDesc() {
        if (appConn == null){
            LOGGER.warn("appconn in streamis datasource dev standard is null");
            return null;
        }
        return this.appConn.getAppDesc();
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {
        //do noting
    }

    @Override
    public void init()  {
        LOGGER.info("begin to init in streamis dataource develop standard");
        //do noting
    }

    @Override
    public void close() throws IOException {
        LOGGER.info("streamis dataource develop standard closes");
    }

    @Override
    public String getStandardName() {
        return "StreamisDatasourceDevelopStandard";
    }

    @Override
    public int getGrade() {
        return 0;
    }

    @Override
    public boolean isNecessary() {
        return true;
    }
}
