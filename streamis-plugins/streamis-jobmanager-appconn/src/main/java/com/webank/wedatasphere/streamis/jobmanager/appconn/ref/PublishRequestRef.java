package com.webank.wedatasphere.streamis.jobmanager.appconn.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractRequestRef;

/**
 * created by yangzhiyue on 2021/4/16
 * Description:
 */
public class PublishRequestRef extends AbstractRequestRef {



    private String type;

    /**
     * 提交到jobmanager的执行代码
     */
    private String executionCode;




    public PublishRequestRef(String type, String executionCode){
        this.type = type;
        this.executionCode = executionCode;
    }





}
