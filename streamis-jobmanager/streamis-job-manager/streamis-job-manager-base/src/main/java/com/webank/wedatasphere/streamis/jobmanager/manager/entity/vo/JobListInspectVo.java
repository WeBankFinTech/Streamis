package com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo;

import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.vo.YarnAppVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JobListInspectVo implements JobInspectVo {

    private List<YarnAppVo> list;

    @Override
    public String getInspectName() {
        return Types.LIST.name().toLowerCase(Locale.ROOT);
    }

    public List<YarnAppVo> getList() {
        return list;
    }

    public void setList(List<YarnAppVo> list) {
        this.list = list;
    }

    public void addOneUrl(String appId, String url, String state) {
        if (null == list) {
            list = new ArrayList<>();
        }
        list.add(new YarnAppVo(appId, url, state));
    }

    public void addYarnApp(YarnAppVo app) {
        if (null == list) {
            list = new ArrayList<>();
        }
        list.add(app);
    }
}