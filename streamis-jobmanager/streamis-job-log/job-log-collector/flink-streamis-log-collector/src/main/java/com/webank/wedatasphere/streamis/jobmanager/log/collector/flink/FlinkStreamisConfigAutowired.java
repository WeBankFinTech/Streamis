package com.webank.wedatasphere.streamis.jobmanager.log.collector.flink;

import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.StreamisLogAppenderConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.log4j2.StreamisLog4j2AppenderConfig;
import com.webank.wedatasphere.streamis.jobmanager.plugin.StreamisConfigAutowired;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.runtime.util.EnvironmentInformation;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.filter.LevelMatchFilter;
import org.apache.logging.log4j.core.filter.RegexFilter;
import org.apache.logging.log4j.core.filter.ThresholdFilter;

import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import static com.webank.wedatasphere.streamis.jobmanager.log.collector.flink.FlinkStreamisConfigDefine.*;

/**
 * Autoconfigure the streamis config inf Flink environment
 */
public class FlinkStreamisConfigAutowired implements StreamisConfigAutowired {

    /**
     * Flink configuration
     */
    private Configuration configuration;

    public FlinkStreamisConfigAutowired(){
        // First to load configuration
        // We should sleep and wait for append of the flink-yaml.conf
    }
    @Override
    public StreamisLogAppenderConfig logAppenderConfig(StreamisLogAppenderConfig.Builder builder) throws Exception{
        this.configuration = loadConfiguration();
        String applicationName  =
                this.configuration.getString(YarnConfigOptions.APPLICATION_NAME);
        if (StringUtils.isNotBlank(applicationName)){
            builder.setAppName(applicationName);
        }
        String gateway = this.configuration.getString(LOG_GATEWAY_ADDRESS);
        if (StringUtils.isNotBlank(gateway)){
            if (gateway.endsWith("/")){
                gateway = gateway.substring(0, gateway.length() - 1);
            }
            gateway += this.configuration.getString(LOG_COLLECT_PATH, "/");
            builder.setRpcAddress(gateway);
        }
        if (builder instanceof StreamisLog4j2AppenderConfig.Builder) {
            List<String> filterStrategies = this.configuration.get(LOG_FILTER_STRATEGIES);
            for (String filterStrategy : filterStrategies) {
                if ("LevelMatch".equals(filterStrategy)) {
                    ((StreamisLog4j2AppenderConfig.Builder)builder).withFilter(LevelMatchFilter.newBuilder().setOnMatch(Filter.Result.ACCEPT).setOnMismatch(Filter.Result.DENY)
                            .setLevel(Level.getLevel(this.configuration.getString(LOG_FILTER_LEVEL_MATCH))).build());
                } else if ("ThresholdFilter".equals(filterStrategy)) {
                    ((StreamisLog4j2AppenderConfig.Builder)builder).withFilter(ThresholdFilter.createFilter(Level
                            .getLevel(this.configuration.getString(LOG_FILTER_THRESHOLD_MATCH)), Filter.Result.ACCEPT, Filter.Result.DENY));
                } else if ("RegexMatch".equals(filterStrategy)) {
                    ((StreamisLog4j2AppenderConfig.Builder)builder).withFilter(RegexFilter.createFilter(this.configuration.getString(LOG_FILTER_REGEX),
                            null, true, Filter.Result.ACCEPT, Filter.Result.DENY));
                }
            }
        }
        String hadoopUser = EnvironmentInformation.getHadoopUser();
        if (hadoopUser.equals("<no hadoop dependency found>") || hadoopUser.equals("<unknown>")){
            hadoopUser = System.getProperty("user.name");
        }
        return builder.setRpcConnTimeout(this.configuration.getInteger(LOG_RPC_CONN_TIMEOUT))
                .setRpcSocketTimeout(this.configuration.getInteger(LOG_RPC_SOCKET_TIMEOUT))
                .setRpcSendRetryCnt(this.configuration.getInteger(LOG_RPC_SEND_RETRY_COUNT))
                .setRpcServerRecoveryTimeInSec(this.configuration.getInteger(LOG_RPC_SERVER_RECOVERY_TIME))
                .setRpcMaxDelayTimeInSec(this.configuration.getInteger(LOG_RPC_MAX_DELAY_TIME))
                .setRpcAuthTokenCodeKey(this.configuration.getString(LOG_RPC_AUTH_TOKEN_CODE_KEY))
                .setRpcAuthTokenUserKey(this.configuration.getString(LOG_RPC_AUTH_TOKEN_USER_KEY))
                .setRpcAuthTokenCode(this.configuration.getString(LOG_RPC_AUTH_TOKEN_CODE))
                .setRpcAuthTokenUser(this.configuration.getString(LOG_RPC_AUTH_TOKEN_USER,
                        hadoopUser))
                .setRpcCacheSize(this.configuration.getInteger(LOG_RPC_CACHE_SIZE))
                .setRpcCacheMaxConsumeThread(this.configuration.getInteger(LOG_PRC_CACHE_MAX_CONSUME_THREAD))
                .setRpcBufferSize(this.configuration.getInteger(LOG_RPC_BUFFER_SIZE))
                .setRpcBufferExpireTimeInSec(this.configuration.getInteger(LOG_RPC_BUFFER_EXPIRE_TIME)).build();
    }

    /**
     * According to :
     * String launchCommand =
     *                 BootstrapTools.getTaskManagerShellCommand(
     *                         flinkConfig,
     *                         tmParams,
     *                         ".",
     *                         ApplicationConstants.LOG_DIR_EXPANSION_VAR,
     *                         hasLogback,
     *                         hasLog4j,
     *                         hasKrb5,
     *                         taskManagerMainClass,
     *                         taskManagerDynamicProperties);
     * the configuration directory of Flink yarn container is always ".",
     * @return configuration
     */
    private synchronized Configuration loadConfiguration(){
//        String configDir = System.getenv("FLINK_CONF_DIR");
//        if (null == configDir){
//            configDir = ".";
//        }
        String configDir = ".";
        Properties properties = System.getProperties();
        Enumeration<?> enumeration = properties.propertyNames();
        Configuration dynamicConfiguration = new Configuration();
        while(enumeration.hasMoreElements()){
            String prop = String.valueOf(enumeration.nextElement());
            dynamicConfiguration.setString(prop, properties.getProperty(prop));
        }
        return GlobalConfiguration.loadConfiguration(configDir, dynamicConfiguration);
    }

}
