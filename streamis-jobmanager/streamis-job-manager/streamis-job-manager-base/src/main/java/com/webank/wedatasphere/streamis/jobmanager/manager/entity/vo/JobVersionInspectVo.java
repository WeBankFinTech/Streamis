package com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo;

import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJobVersion;

import java.util.Locale;

/**
 * Version inspect
 */
public class JobVersionInspectVo implements JobInspectVo{

    /**
     * Current version
     */
    private StreamJobVersion now;

    /**
     * Last version
     */
    private StreamJobVersion last;

    @Override
    public String getInspectName() {
        return Types.VERSION.name().toLowerCase(Locale.ROOT);
    }


    public StreamJobVersion getNow() {
        return now;
    }

    public void setNow(StreamJobVersion now) {
        this.now = now;
    }

    public StreamJobVersion getLast() {
        return last;
    }

    public void setLast(StreamJobVersion last) {
        this.last = last;
    }
}
