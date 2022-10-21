package com.webank.wedatasphere.streamis.jobmanager.log.collector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamisLogAppenderTest {
    private static final Logger LOG = LoggerFactory.getLogger(StreamisLogAppenderTest.class);
    public static void main(String[] args) throws InterruptedException {
        while(true){
            for(int i = 0; i < 100; i ++){
                LOG.info("Stream Log appender test");
            }
            Thread.sleep(1000);
        }
    }
}
