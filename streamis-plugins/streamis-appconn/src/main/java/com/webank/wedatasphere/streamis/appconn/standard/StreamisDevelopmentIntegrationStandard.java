package com.webank.wedatasphere.streamis.appconn.standard;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.standard.app.development.AbstractLabelDevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.RefOperationService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import com.webank.wedatasphere.streamis.appconn.service.StreamisCRUDService;
import com.webank.wedatasphere.streamis.appconn.service.StreamisQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * created by yangzhiyue on 2021/4/8
 * Description: streamis的开发规范是
 */
public class StreamisDevelopmentIntegrationStandard extends AbstractLabelDevelopmentIntegrationStandard {


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisDevelopmentIntegrationStandard.class);

    private AppConn appConn;

    private static StreamisDevelopmentIntegrationStandard instance;


    private AppDesc appDesc;


    private StreamisCRUDService streamisCRUDService;

    private StreamisQueryService streamisQueryService;


    private List<RefOperationService> refOperationServiceList;


    private StreamisDevelopmentIntegrationStandard(AppConn appConn){
        this.appConn = appConn;
    }

    public static StreamisDevelopmentIntegrationStandard getInstance(AppConn appConn){
        if (null == instance){
            synchronized (StreamisDevelopmentIntegrationStandard.class){
                if (null == instance){
                    instance = new StreamisDevelopmentIntegrationStandard(appConn);
                }
            }
        }
        return instance;
    }





    @Override
    protected List<RefOperationService> getRefOperationService() {
        if (null == refOperationServiceList){
            synchronized (StreamisDevelopmentIntegrationStandard.class){
                if(null == refOperationServiceList){
                    streamisCRUDService = new StreamisCRUDService(appConn);
                    streamisQueryService = new StreamisQueryService(appConn);
                    refOperationServiceList = Lists.newArrayList(streamisCRUDService, streamisQueryService);
                }
            }
        }
        return this.refOperationServiceList;
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
