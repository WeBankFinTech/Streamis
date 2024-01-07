package com.webank.wedatasphere.streamis.jobmanager.log.collector.flink;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;

import java.util.List;

/**
 * Config definition
 */
public class FlinkStreamisConfigDefine {

    private FlinkStreamisConfigDefine(){}


    private static String  error ="ERROR";

    /**
     * Gateway address of log module for streamis
     */
    public static final ConfigOption<String> LOG_GATEWAY_ADDRESS = ConfigOptions.key("stream.log.gateway.address")
            .stringType().noDefaultValue().withDescription("The gateway address ex: http://127.0.0.1:8080");

    /**
     * Entrypoint path of collecting log
     */
    public static final ConfigOption<String> LOG_COLLECT_PATH = ConfigOptions.key("stream.log.collect.path")
            .stringType().defaultValue("/api/rest_j/v1/streamis/streamJobManager/log/collect/events").withDescription("The entrypoint path of collecting log");


    public static final ConfigOption<String> LOG_HEARTBEAT_PATH = ConfigOptions.key("stream.log.heartbeat.path")
            .stringType().defaultValue("/api/rest_j/v1/streamis/streamJobManager/log/heartbeat").withDescription("The entrypoint path of heartbeat log");


    public static final ConfigOption<Integer> LOG_HEARTBEAT_INTERVAL = ConfigOptions.key("stream.log.heartbeat.interval")
            .intType().defaultValue(30 * 60 * 1000).withDescription("Heartbeat interval (ms) in log RPC module");


    public static final ConfigOption<Boolean> LOG_HEARTBEAT_ENABLE = ConfigOptions.key("stream.log.heartbeat.enable")
            .booleanType().defaultValue(true).withDescription("Heartbeat enable");

    /**
     * Connection timeout(in milliseconds) in log RPC module
     */
    public static final ConfigOption<Integer> LOG_RPC_CONN_TIMEOUT = ConfigOptions.key("stream.log.rpc.connect-timeout")
            .intType().defaultValue(3000).withDescription("Connection timeout(ms) in log RPC module");

    /**
     * Socket timeout(in milliseconds) in log RPC module
     */
    public static final ConfigOption<Integer> LOG_RPC_SOCKET_TIMEOUT = ConfigOptions.key("stream.log.rpc.socket-timeout")
            .intType().defaultValue(15000).withDescription("Socket timeout(ms) in log RPC module");

    /**
     * Max retry count of sending message in log RPC module
     */
    public static final ConfigOption<Integer> LOG_RPC_SEND_RETRY_COUNT = ConfigOptions.key("stream.log.rpc.send-retry-count")
            .intType().defaultValue(3).withDescription("Max retry count of sending message in log RPC module");

    /**
     * Server recovery time(in seconds) in log RPC module
     */
    public static final ConfigOption<Integer> LOG_RPC_SERVER_RECOVERY_TIME = ConfigOptions.key("stream.log.rpc.server-recovery-time-in-sec")
            .intType().defaultValue(5).withDescription("Server recovery time(sec) in log RPC module");

    /**
     * Max delay time(in seconds) in log RPC module. if reach the limit, the message will be dropped
     */
    public static final ConfigOption<Integer> LOG_RPC_MAX_DELAY_TIME = ConfigOptions.key("stream.log.rpc.max-delay-time")
            .intType().defaultValue(60).withDescription("Max delay time(sec) in log RPC module");

    /**
     * Token code key in log RPC auth module
     */
    public static final ConfigOption<String> LOG_RPC_AUTH_TOKEN_CODE_KEY = ConfigOptions.key("stream.log.rpc.auth.token-code-key")
            .stringType().defaultValue("Token-Code").withDescription("Token code key in log RPC auth module");

    /**
     * Token user key in log RPC auth module
     */
    public static final ConfigOption<String> LOG_RPC_AUTH_TOKEN_USER_KEY = ConfigOptions.key("stream.log.rpc.auth.token-user-key")
            .stringType().defaultValue("Token-User").withDescription("Token user key in log RPC auth module");

    /**
     * Token code in log RPC auth module
     */
    public static final ConfigOption<String> LOG_RPC_AUTH_TOKEN_CODE = ConfigOptions.key("stream.log.rpc.auth.token-code")
            .stringType().defaultValue("STREAM-LOG").withDescription("Token code in log RPC auth module");

    /**
     * Token user in log RPC auth module
     */
    public static final ConfigOption<String> LOG_RPC_AUTH_TOKEN_USER = ConfigOptions.key("stream.log.rpc.auth.token-user")
            .stringType().defaultValue(System.getProperty("user.name")).withDescription("Token user in log RPC auth module");

    /**
     * Cache size in log RPC module
     */
    public static final ConfigOption<Integer> LOG_RPC_CACHE_SIZE = ConfigOptions.key("stream.log.rpc.cache.size")
            .intType().defaultValue(150).withDescription("Cache size in log RPC module");

    /**
     * Max cache consume threads in log RPC module
     */
    public static final ConfigOption<Integer> LOG_PRC_CACHE_MAX_CONSUME_THREAD = ConfigOptions.key("stream.log.rpc.cache.max-consume-thread")
            .intType().defaultValue(2).withDescription("Max cache consume threads in log RPC module");

    /**
     * If discard the useless log
     */
    public static final ConfigOption<Boolean> LOG_RPC_CACHE_DISCARD = ConfigOptions.key("stream.log.rpc.cache.discard")
            .booleanType().defaultValue(true).withDescription("If discard the useless log");

    /**
     * The window size of discarding
     */
    public static final ConfigOption<Integer> LOG_RPC_CACHE_DISCARD_WINDOW = ConfigOptions.key("stream.log.rpc.cache.discard-window")
            .intType().defaultValue(2).withDescription("The window size of discarding");
    /**
     * Buffer size in log RPC module
     */
    public static final ConfigOption<Integer> LOG_RPC_BUFFER_SIZE = ConfigOptions.key("stream.log.rpc.buffer.size")
            .intType().defaultValue(50).withDescription("Buffer size in log RPC module");

    /**
     * Buffer expire time(sec) in log RPC module
     */
    public static final ConfigOption<Integer> LOG_RPC_BUFFER_EXPIRE_TIME = ConfigOptions.key("stream.log.rpc.buffer.expire-time-in-sec")
            .intType().defaultValue(2).withDescription("Buffer expire time (sec) in log RPC module");

    /**
     * Log filter strategy list
     */
    public static final ConfigOption<List<String>> LOG_FILTER_STRATEGIES = ConfigOptions.key("stream.log.filter.strategies")
            .stringType().asList().defaultValues("Keyword").withDescription("Log filter strategy list");

    /**
     * Level value of LevelMatch filter strategy
     */
    public static final ConfigOption<String> LOG_FILTER_LEVEL_MATCH = ConfigOptions.key("stream.log.filter.level-match.level")
            .stringType().defaultValue(error).withDescription("Level value of LevelMatch filter strategy");


    /**
     * Level value of ThresholdMatch filter strategy
     */
    public static final ConfigOption<String> LOG_FILTER_THRESHOLD_MATCH = ConfigOptions.key("stream.log.filter.threshold.level")
            .stringType().defaultValue(error).withDescription("Level value of ThresholdMatch filter strategy");
    /**
     * Regex value of RegexMatch filter strategy
     */
    public static final ConfigOption<String> LOG_FILTER_REGEX = ConfigOptions.key("stream.log.filter.regex.value")
            .stringType().defaultValue(".*").withDescription("Regex value of RegexMatch filter strategy");

    /**
     * Accept keywords of Keyword filter strategy
     */
    public static final ConfigOption<String> LOG_FILTER_KEYWORDS = ConfigOptions.key("stream.log.filter.keywords")
            .stringType().defaultValue(error).withDescription("Accept keywords of Keyword filter strategy");

    /**
     * Exclude keywords of Keyword filter strategy
     */
    public static final ConfigOption<String> LOG_FILTER_KEYWORDS_EXCLUDE = ConfigOptions.key("stream.log.filter.keywords.exclude")
            .stringType().defaultValue("").withDescription("Exclude keywords of Keyword filter strategy");

    /**
     * Debug mode
     */
    public static final ConfigOption<Boolean> DEBUG_MODE = ConfigOptions.key("stream.log.debug")
            .booleanType().defaultValue(false).withDescription("Debug mode");
}
