package com.webank.wedatasphere.streamis.jobmanager.entrypoint;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jefftlin
 *
 * Server
 */
public class StreamJobHeartbeatListener {

     Map<String, Object> serviceAndPortMap;

    /**
     * Need to launch
     */
    public void init() {
        serviceAndPortMap = new ConcurrentHashMap<>();

        this.listenFlinkJob();
        this.listenSparkJob();
    }

    private void listenFlinkJob() {
        Timer timer = new Timer("flinkStreamJobHeartbeatListener", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
            //nothing


            }
        }, 1000, 10000);
    }

    private void listenSparkJob() {
        Timer timer = new Timer("sparkStreamJobHeartbeatListener", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
            //nothing


            }
        }, 1000, 10000);
    }

}
