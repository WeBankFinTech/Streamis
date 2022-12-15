package com.webank.wedatasphere.streamis.jobmanager.log.collector.flink;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.GlobalConfiguration;
import org.junit.Test;

import java.util.Enumeration;
import java.util.Objects;
import java.util.Properties;

public class FlinkConfigurationLoadTest {
    @Test
    public void loadConfiguration() {
        String configDir = Objects.requireNonNull(FlinkConfigurationLoadTest.class.getResource("/")).getFile();
        Properties properties = System.getProperties();
        Enumeration<?> enumeration = properties.propertyNames();
        Configuration dynamicConfiguration = new Configuration();
        while(enumeration.hasMoreElements()){
            String prop = String.valueOf(enumeration.nextElement());
            dynamicConfiguration.setString(prop, properties.getProperty(prop));
        }
        GlobalConfiguration.loadConfiguration(configDir, dynamicConfiguration);
    }
}
