package com.webank.wedatasphere.streamis.jobmanager.log.collector.spark;

import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.StreamisLogAppenderConfig;
import com.webank.wedatasphere.streamis.jobmanager.plugin.StreamisConfigAutowired;

import java.util.Optional;

/**
 * Autoconfigure the streamis config in Spark environment
 */
public class SparkStreamisConfigAutowired implements StreamisConfigAutowired {

    private static final String APP_NAME_CONFIG = "app.name";

    private static final String SERVER_ADDRESS_CONFIG = "streamis.url";

    private static final String COLLECTOR_URI_CONFIG = "streamis.log.collector.uri";

    private static final String PROJECT_NAME_CONFIG = "project.name";

    private static final String DEFAULT_COLLECTOR_URI = "/api/rest_j/v1/streamis/streamJobManager/log/collect/events";
    @Override
    public StreamisLogAppenderConfig logAppenderConfig(StreamisLogAppenderConfig.Builder builder) throws Exception {
        // Load the config from system properties
        Optional.ofNullable(System.getProperty(APP_NAME_CONFIG)).ifPresent(appName -> {
            String projectName = System.getProperty(PROJECT_NAME_CONFIG);
            if (null != projectName && !projectName.trim().equals("")){
                appName = projectName + "." + appName;
            }
            System.out.println("Spark env to streamis: application name =>" + appName);
            builder.setAppName(appName);
        });
        String serverAddress = System.getProperty(SERVER_ADDRESS_CONFIG);
        if (null != serverAddress && !serverAddress.trim().equals("")){
            if (serverAddress.endsWith("/")){
                serverAddress = serverAddress.substring(0, serverAddress.length() - 1);
            }
            String collectorUri = System.getProperty(COLLECTOR_URI_CONFIG, DEFAULT_COLLECTOR_URI);
            if (null != collectorUri && !collectorUri.trim().equals("")){
                if (!collectorUri.startsWith("/")){
                    collectorUri = "/" + collectorUri;
                }
                serverAddress += collectorUri;
            }
            System.out.println("Spark env to streamis: server address =>" + serverAddress);
            builder.setRpcAddress(serverAddress);
        }
        String user = System.getenv("USER");
        if (null == user || user.trim().equals("")){
            user = System.getProperty("user.name", "hadoop");
        }
        System.out.println("Spark env to streamis: log user =>" + user);
        builder.setRpcAuthTokenUser(user);
        return builder.build();
    }
}
