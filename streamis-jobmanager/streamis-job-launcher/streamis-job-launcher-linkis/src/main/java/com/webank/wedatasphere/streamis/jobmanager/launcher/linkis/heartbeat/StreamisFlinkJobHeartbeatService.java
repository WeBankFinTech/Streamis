package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.heartbeat;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jefftlin
 */

public class StreamisFlinkJobHeartbeatService {

    volatile Map<String, Object> serviceAndPortMap;

    public void init() {
        serviceAndPortMap = new ConcurrentHashMap<>();

        this.listen();
    }

    private void listen() {
        Timer timer = new Timer("streamisFlinkJobHeartbeatListener", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {



            }
        }, 1000, 10000);
    }
}

