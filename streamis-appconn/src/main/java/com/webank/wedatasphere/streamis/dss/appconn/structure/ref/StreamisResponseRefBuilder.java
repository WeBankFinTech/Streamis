package com.webank.wedatasphere.streamis.dss.appconn.structure.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefBuilder;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefImpl;

import java.util.Map;

import static com.webank.wedatasphere.streamis.dss.appconn.utils.NumberUtils.getInt;

public class StreamisResponseRefBuilder extends ResponseRefBuilder.ExternalResponseRefBuilder<StreamisResponseRefBuilder, ResponseRefImpl> {

    @Override
    public StreamisResponseRefBuilder setResponseBody(String responseBody) {
        // TODO reconstruct the response structure
        Map<String, Object> headerMap = (Map<String, Object>) responseMap.get("header");
        if (headerMap.containsKey("code")) {
            status = getInt(headerMap.get("code"));
            if (status != 0 && status != 200) {
                errorMsg = headerMap.get("msg").toString();
            }
        }
        Object payload = responseMap.get("payload");
        if(payload instanceof Map) {
            setResponseMap((Map<String, Object>) payload);
        }
        return super.setResponseBody(responseBody);
    }
}
