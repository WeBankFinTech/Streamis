package com.webank.wedatasphere.streamis.jobmanager.log.collector;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamisLogAppenderTest {
    private static final Logger LOG = LoggerFactory.getLogger(StreamisLogAppenderTest.class);
    @Test
    public void appenderLog() throws InterruptedException {
        int total = 10000;
        int tps = 1000;
        long timer = System.currentTimeMillis() + 1000;
        for (int i = 0; i < total; i++) {
            if (i > 0 && i % tps == 0) {
                long sleep = timer - System.currentTimeMillis();
                if (sleep > 0) {
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }
                timer = System.currentTimeMillis() + 1000;
            }
            LOG.info("ERROR: Stream Log appender test, sequence id: " + i);
        }
    }
}
