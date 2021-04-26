package com.webank.wedatasphere.streamis.appconn.standard;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.development.AbstractLabelDevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.RefOperationService;
import com.webank.wedatasphere.dss.standard.app.development.process.DevProcessService;
import com.webank.wedatasphere.dss.standard.app.development.process.ProcessService;
import com.webank.wedatasphere.dss.standard.app.development.process.ProdProcessService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.streamis.appconn.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * created by yangzhiyue on 2021/4/8
 * Description: streamis的开发规范是
 */
public class StreamisDevelopmentIntegrationStandard extends AbstractLabelDevelopmentIntegrationStandard {


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisDevelopmentIntegrationStandard.class);

    private final AppConn appConn;

    private static StreamisDevelopmentIntegrationStandard instance;


    private List<ProcessService> processServices;

    private List<RefOperationService> refOperationServiceList;

    private static final List<DSSLabel> DEV_LABEL = Lists.newArrayList(new DSSLabel("dev"));

    private static final List<DSSLabel> PROD_LABEL = Lists.newArrayList(new DSSLabel("prod"));

    private StreamisDevelopmentIntegrationStandard(AppConn appConn) {
        this.appConn = appConn;
        init();
    }

    @Override
    public void init() {
        try {
            AppInstance devAppInstance = appConn.getAppDesc().getAppInstancesByLabels(DEV_LABEL).get(0);
            AppInstance prodAppInstance = appConn.getAppDesc().getAppInstancesByLabels(PROD_LABEL).get(0);
            processServices = new ArrayList<>();
            if (devAppInstance != null) {
                LOGGER.info("dev appInstance is not null, url is {}", devAppInstance.getBaseUrl());
                StreamisFlowCRUDService streamisFlowCRUDService = new StreamisFlowCRUDService(devAppInstance);
                StreamisFlowQueryService streamisFlowQueryService = new StreamisFlowQueryService(devAppInstance);
                StreamisFlowExportService streamisFlowExportService = new StreamisFlowExportService(devAppInstance);
                DevProcessService devProcessService = new DevProcessService(Lists.newArrayList(streamisFlowCRUDService, streamisFlowQueryService, streamisFlowExportService));
                setService(devProcessService, streamisFlowCRUDService, streamisFlowQueryService, streamisFlowExportService);
                processServices.add(devProcessService);
                refOperationServiceList.addAll(Lists.newArrayList(streamisFlowCRUDService, streamisFlowQueryService, streamisFlowExportService));
            } else {
                LOGGER.info("dev appInstance in Streamis dev standard is null");
            }
            if (prodAppInstance != null) {
                LOGGER.info("prod instance is not null, url is {}", prodAppInstance.getBaseUrl());
                StreamisFlowQueryService queryService = new StreamisFlowQueryService(prodAppInstance);
                StreamisFlowPublishService publishService = new StreamisFlowPublishService(prodAppInstance);
                StreamisFlowImportService importService = new StreamisFlowImportService(prodAppInstance);
                ProdProcessService prodProcessService = new ProdProcessService(Lists.newArrayList(queryService,
                        publishService, importService));
                setService(prodProcessService, queryService, publishService, importService);
                processServices.add(prodProcessService);
                refOperationServiceList.addAll(Lists.newArrayList(queryService, publishService, importService));
            }else{
                LOGGER.info("prod app instance in streamis dev standard is null");
            }
        } catch (final Exception e) {
            LOGGER.error("failed to init StreamisDevelopmentIntegrationStandard", e);
            processServices = new ArrayList<>();
            refOperationServiceList = new ArrayList<>();
        }
    }

    public static StreamisDevelopmentIntegrationStandard getInstance(AppConn appConn) {
        if (null == instance) {
            synchronized (StreamisDevelopmentIntegrationStandard.class) {
                if (null == instance) {
                    instance = new StreamisDevelopmentIntegrationStandard(appConn);
                }
            }
        }
        return instance;
    }


    private void setService(ProcessService processService, RefOperationService... refOperationService){
        Arrays.stream(refOperationService).forEach(service -> service.setDevelopmentService(processService));
    }


    @Override
    public List<ProcessService> getProcessServices() {
        return this.processServices;
    }

    @Override
    protected List<RefOperationService> getRefOperationService() {
        return this.refOperationServiceList;
    }

    @Override
    public AppDesc getAppDesc() {
        return this.appConn.getAppDesc();
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {
        //nothing
    }


    @Override
    public void close() throws IOException {

    }

    @Override
    public String getStandardName() {
        return "StreamisDevelopmentIntegrationStandard";
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
