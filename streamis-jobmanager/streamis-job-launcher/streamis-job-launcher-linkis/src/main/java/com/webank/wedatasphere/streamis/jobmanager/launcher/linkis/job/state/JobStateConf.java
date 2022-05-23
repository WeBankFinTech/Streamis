package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state;

import org.apache.linkis.common.conf.CommonVars;

public class JobStateConf {
    public static final String CHECKPOINT_PATH_PATTERN = CommonVars.apply("wds.streamis.job.state.checkpoint.path-pattern", "").getValue();
}
