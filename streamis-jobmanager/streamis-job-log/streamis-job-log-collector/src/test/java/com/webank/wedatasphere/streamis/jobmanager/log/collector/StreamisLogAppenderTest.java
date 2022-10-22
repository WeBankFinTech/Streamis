package com.webank.wedatasphere.streamis.jobmanager.log.collector;

import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamisLogAppenderTest {
    private static final Logger LOG = LoggerFactory.getLogger(StreamisLogAppenderTest.class);
    @Test
    public void appenderLog() throws InterruptedException {
        org.apache.logging.log4j.core.Logger logger = (org.apache.logging.log4j.core.Logger) LogManager.getLogger("Test: Hello-world");
        logger.info("");
        int total = 1000;
        int tps = 100;
        long timer = System.currentTimeMillis() + 1000;
        for(int i = 0; i < total; i ++){
            if (i > 0 && i % tps == 0){
                long sleep = timer - System.currentTimeMillis();
                if (sleep > 0){
                    Thread.sleep(sleep);
                }
                timer = System.currentTimeMillis() + 1000;
            }
            LOG.info("Stream Log appender test, sequence id: " + i);
        }
    }
}
