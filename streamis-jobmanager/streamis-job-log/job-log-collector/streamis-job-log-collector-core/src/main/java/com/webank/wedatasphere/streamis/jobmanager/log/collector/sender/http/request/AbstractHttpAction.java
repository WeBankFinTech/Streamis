package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.http.request;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract implement
 * @param <T>
 */
public abstract class AbstractHttpAction<T extends HttpRequestBase> implements HttpAction {

    protected String uri;

    protected String user;

    protected AbstractHttpAction(String uri){
        this.uri = uri;
    }

    @Override
    public String uri() {
        return uri;
    }

    /**
     * Request method
     * @return method
     */
    protected abstract T getRequestMethod();

    private Map<String, String> requestHeaders = new HashMap<>();

    private Map<String, Object> requestPayload = new HashMap<>();

    @Override
    public Map<String, String> getRequestHeaders() {
        return this.requestHeaders;
    }

    @Override
    public Map<String, Object> getRequestPayload() {
        return this.requestPayload;
    }

    @Override
    public HttpResponse execute(HttpClient httpClient) throws IOException {
        HttpRequestBase requestBase = getRequestMethod();
        try{
            requestBase.setURI(new URI(uri));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URI maybe has wrong format", e);
        }
        requestHeaders.forEach(requestBase::setHeader);
        return httpClient.execute(requestBase);
    }
}
