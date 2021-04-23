package com.webank.wedatasphere.streamis.plugins.common;

import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.linkis.common.conf.CommonVars;

/**
 * created by yangzhiyue on 2021/4/21
 * Description:
 */
public class StreamisPluginUtils {


    public static final String DEV_LABEL = "dev";

    public static final String PROD_LABEL = "prod";

    public static final String CS_STR = "contextId";



    public static final String STREAMIS_RPC_SERVER_NAME = CommonVars.apply("wds.streamis.plugin.server.name", "streamis-server").getValue();


    public static String getLabel(AppInstance appInstance){
        for (DSSLabel label : appInstance.getLabels()) {
            if (label.getLabel().equalsIgnoreCase(PROD_LABEL)) return PROD_LABEL;
        }
        return DEV_LABEL;
    }




}
