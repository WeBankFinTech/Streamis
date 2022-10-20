package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.http.request;

import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.http.Json;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.util.Objects;

/**
 * Post action
 */
public class PostAction extends AbstractHttpAction<HttpPost> {

    public PostAction(String uri) {
        super(uri);
    }

    /**
     * Http entity (use stringEntity and "application/json" default)
     * @return http entity
     */
    HttpEntity getHttpEntity(){
        StringEntity stringEntity;
        try{
            stringEntity = new StringEntity(Objects.requireNonNull(Json.toJson(getRequestPayload(), null)), "UTF-8");
        } catch (Exception e){
            throw new IllegalArgumentException("Cannot deserialize the requestPayLoad: " + getRequestPayload().toString(), e);
        }
        stringEntity.setContentType(ContentType.APPLICATION_JSON.toString());
        return stringEntity;
    }

    @Override
    protected HttpPost getRequestMethod() {
        HttpPost httpPost = new HttpPost();
        httpPost.setEntity(getHttpEntity());
        return httpPost;
    }
}
