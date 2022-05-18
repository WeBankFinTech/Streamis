package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state;

import org.apache.linkis.common.conf.CommonVars;

public class ClientConf {
    public static final String GATEWAY_ADDRESS = CommonVars.apply("wds.streamis.client.gatewayAddress", "").getValue();
    public static final Integer CONNECTION_TIMEOUT = CommonVars.apply("wds.streamis.client.connectionTimeout", 5 * 60 * 1000).getValue();
    public static final Integer MAX_CONNECTION = CommonVars.apply("wds.streamis.client.maxConnection", 100).getValue();
    public static final Integer READ_TIMEOUT = CommonVars.apply("wds.streamis.client.readTimeout", 10 * 60 * 1000).getValue();
    public static final String TOKEN_KEY = CommonVars.apply("wds.streamis.client.token.key", "Validation-Code").getValue();
    public static final String TOKEN_VALUE = CommonVars.apply("wds.streamis.client.token.value", "WS-AUTH").getValue();
    public static final String DWS_VERSION = CommonVars.apply("wds.streamis.client.dws-version", "v1").getValue();
    public static final String DWS_CLIENTNAME = CommonVars.apply("wds.streamis.client.dws-clientName", "Streamis-Client").getValue();
    public static final String PATH_PREFIX = CommonVars.apply("wds.streamis.client.checkpoint.path-prefix", "").getValue();

}
