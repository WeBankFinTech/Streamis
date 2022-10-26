package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.http.request;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

import java.io.IOException;
import java.util.Map;

/**
 * Http action
 */
public interface HttpAction {

    /**
     * URI path
     * @return path
     */
    String uri();

    /**
     * Request headers
     * @return map
     */
    Map<String, String> getRequestHeaders();

    /**
     * Request pay load(body)
     * @return map
     */
    Map<String, Object> getRequestPayload();

    /**
     * Execute http action
     * @return http response
     */
    HttpResponse execute(HttpClient httpClient) throws IOException;

}
