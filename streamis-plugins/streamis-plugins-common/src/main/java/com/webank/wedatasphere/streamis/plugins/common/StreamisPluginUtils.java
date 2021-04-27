package com.webank.wedatasphere.streamis.plugins.common;

import com.google.gson.*;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.linkis.common.conf.CommonVars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/21
 * Description:
 */
public class StreamisPluginUtils {


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisPluginUtils.class);

    public static final String DEV_LABEL = "dev";

    public static final String PROD_LABEL = "prod";

    public static final String CS_STR = "contextId";


    public static final String BML_RESOURCE_ID_STR = "bmlResourceId";

    public static final String BML_RESOURCE_VERSION_STR = "bmlResourceVersion";

    public static final String USER_STR = "user";

    public static final String DATA_STR = "data";

    public static final String STREAMIS_TABLE_META_ID_STR = "streamisTableMetaId";


    public static final String STREAMIS_RPC_SERVER_NAME = CommonVars.apply("wds.streamis.plugin.server.name", "streamis-server").getValue();


    public static final Gson COMMON_GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").serializeNulls()
                .registerTypeAdapter(Double.class, new JsonSerializer<Double>() {
        @Override
        public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
            if(src == src.longValue()) {
                return new JsonPrimitive(src.longValue());
            } else {
                return new JsonPrimitive(src);
            }
        }
    }).create();


    public static String getLabel(AppInstance appInstance){
        for (DSSLabel label : appInstance.getLabels()) {
            if (label.getLabel().equalsIgnoreCase(PROD_LABEL)) return PROD_LABEL;
        }
        return DEV_LABEL;
    }


    /**
     * 将
     * @param responseBody json response
     * @return linkis response中的data数据，如果有问题 则返回null
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getDataFromResponseBody(String responseBody){
        try{
            Map<String, Object> responseMap = COMMON_GSON.fromJson(responseBody, Map.class);
            Object dataObject = responseMap.get(DATA_STR);
            if (dataObject instanceof Map){
                return (Map<String, Object>)dataObject;
            }else{
                LOGGER.error("dataObject {} is not Map class, it is a {}", dataObject, dataObject.getClass());
                return null;
            }
        }catch(final Exception e){
            LOGGER.error("failed to convert a responseBody {} to Map", responseBody, e);
            return null;
        }
    }




}
