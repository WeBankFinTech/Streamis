package com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo;

import java.util.Locale;

public class JobSnapshotInspectVo implements JobInspectVo{
    /**
     * Path
     */
    private String path;

    @Override
    public String getInspectName() {
        return Types.SNAPSHOT.name().toLowerCase(Locale.ROOT);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
