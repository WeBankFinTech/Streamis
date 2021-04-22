package com.webank.wedatasphere.streamis.datasource.transfer.conf;

import com.webank.wedatasphere.linkis.common.conf.CommonVars;

public class TransferConfiguration {
    public static final CommonVars<String> SERVER_URL = CommonVars.apply("wds.streamis.datasource.client.serverurl", "");
    public static final CommonVars<Long> CONNECTION_TIMEOUT = CommonVars.apply("wds.streamis.datasource.client.connection.timeout", 30000L);
    public static final CommonVars<Boolean> DISCOVERY_ENABLED = CommonVars.apply("wds.streamis.datasource.client.discovery.enabled", true);
    public static final CommonVars<Long> DISCOVERY_FREQUENCY_PERIOD = CommonVars.apply("wds.streamis.datasource.client.discoveryfrequency.period", 1L);
    public static final CommonVars<Boolean> LOAD_BALANCER_ENABLED = CommonVars.apply("wds.streamis.datasource.client.loadbalancer.enabled", true);
    public static final CommonVars<Integer> MAX_CONNECTION_SIZE = CommonVars.apply("wds.streamis.datasource.client.maxconnection.size", 5);
    public static final CommonVars<Boolean> RETRY_ENABLED = CommonVars.apply("wds.streamis.datasource.client.retryenabled", false);
    public static final CommonVars<Long> READ_TIMEOUT = CommonVars.apply("wds.streamis.datasource.client.readtimeout", 30000L);

    public static final CommonVars<String> AUTHTOKEN_KEY = CommonVars.apply("wds.streamis.datasource.client.authtoken.key", "");
    public static final CommonVars<String> AUTHTOKEN_VALUE = CommonVars.apply("wds.streamis.datasource.client.authtoken.value", "");
    public static final CommonVars<String> DWS_VERSION = CommonVars.apply("wds.streamis.datasource.client.dws.version", "");






}
