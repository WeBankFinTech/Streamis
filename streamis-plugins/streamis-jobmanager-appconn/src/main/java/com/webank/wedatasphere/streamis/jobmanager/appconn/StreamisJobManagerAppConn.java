package com.webank.wedatasphere.streamis.jobmanager.appconn;

import com.webank.wedatasphere.dss.appconn.core.ext.TransformAppConn;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.streamis.jobmanager.appconn.standard.StreamisJMDevStandard;

import java.util.Arrays;
import java.util.List;

/**
 * created by yangzhiyue on 2021/4/13
 * Description:
 */
public class StreamisJobManagerAppConn implements TransformAppConn {


    private AppDesc appDesc;



    private StreamisJMDevStandard streamisJMDevStandard = StreamisJMDevStandard.getInstance(this);



    /**
     * transform是将工作流进行转换成可以执行flinksql等
     * jobmanager拿到之后就可以进行执行
     */
    @Override
    public void transform() {

    }

    @Override
    public List<AppStandard> getAppStandards() {
        return Arrays.asList(streamisJMDevStandard);
    }

    @Override
    public AppDesc getAppDesc() {
        return this.appDesc;
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {
        this.appDesc = appDesc;
    }
}
