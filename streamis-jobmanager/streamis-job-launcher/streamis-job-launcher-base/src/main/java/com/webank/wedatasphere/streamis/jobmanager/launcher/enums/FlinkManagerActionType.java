package com.webank.wedatasphere.streamis.jobmanager.launcher.enums;

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf.StreamJobLauncherConf;
import org.apache.linkis.governance.common.enums.OnceJobOperationBoundary;

public enum FlinkManagerActionType {

    STATUS("status"),
    KILL("kill"),
    SAVE("doSavepoint");

    private String name;

    FlinkManagerActionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static OnceJobOperationBoundary getOperationBoundary(FlinkManagerActionType actionType) {
        if (StreamJobLauncherConf.isPrivateAction(actionType)) {
            return OnceJobOperationBoundary.EC;
        } else {
            return OnceJobOperationBoundary.ECM;
        }
    }
}
