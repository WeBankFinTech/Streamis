package com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo;

import java.util.Locale;

public class JobHighAvailableVo implements JobInspectVo{
    private boolean highAvailable;

    private String msg;

    public JobHighAvailableVo() {
    }

    public JobHighAvailableVo(boolean highAvailable, String msg) {
        this.highAvailable = highAvailable;
        this.msg = msg;
    }

    public boolean isHighAvailable() {
        return highAvailable;
    }

    public void setHighAvailable(boolean highAvailable) {
        this.highAvailable = highAvailable;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String getInspectName() {
        return Types.HIGHAVAILABLE.name().toLowerCase(Locale.ROOT);
    }
}
