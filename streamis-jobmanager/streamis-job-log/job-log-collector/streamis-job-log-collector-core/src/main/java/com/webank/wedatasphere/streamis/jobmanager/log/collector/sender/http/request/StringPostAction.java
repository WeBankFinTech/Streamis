package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.http.request;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

/**
 * Use string to request
 */
public class StringPostAction extends AbstractHttpAction<HttpPost> {

    /**
     * Raw string value
     */
    private final String rawString;
    public StringPostAction(String uri, String rawString) {
        super(uri);
        this.rawString = rawString;
    }

    @Override
    protected HttpPost getRequestMethod() {
        HttpPost httpPost = new HttpPost();
        StringEntity stringEntity = new StringEntity(rawString, "UTF-8");
        stringEntity.setContentType(ContentType.APPLICATION_JSON.toString());
        httpPost.setEntity(stringEntity);
        return httpPost;
    }
}
