package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.http.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.http.Json;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.http.entities.Resource;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Post action for entity
 */
public class EntityPostAction<T> extends PostAction{

    private static final String RESOURCE_FIELD_NAME = "resources";

    private T entity;

    public EntityPostAction(String uri, T entity) {
        super(uri);
        if (Objects.isNull(entity)){
            throw new IllegalArgumentException("Entity cannot be null in action");
        }
        this.entity = entity;
    }

    @Override
    HttpEntity getHttpEntity() {
        try {
            if (entity instanceof Resource) {
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                String entityStr = Json.toJson(entity, null);
                JsonNode rootNode = Json.getMapper().readTree(entityStr);
                addEntityBody(entityBuilder, "", rootNode);
                Optional.ofNullable(((Resource) entity).getResources()).ifPresent(resources -> {
                    for(int i = 0; i < resources.size(); i ++){
                        entityBuilder.addBinaryBody(RESOURCE_FIELD_NAME + "[" + i +"]", resources.get(i));
                    }
                });
                return entityBuilder.build();
            } else {
                StringEntity stringEntity;
                stringEntity = new StringEntity(Objects.requireNonNull(Json.toJson(entity, null)), "UTF-8");
                stringEntity.setContentType(ContentType.APPLICATION_JSON.toString());
                return stringEntity;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot deserialize the entity: " + entity.getClass().getName(), e);
        }
    }

    private void addEntityBody(MultipartEntityBuilder builder, String prefix, JsonNode node){
        if (node instanceof ObjectNode){
            if (null != prefix && !prefix.trim().equals("")){
                prefix += ".";
            }
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()){
                Map.Entry<String, JsonNode> entry = fields.next();
                addEntityBody(builder, prefix + entry.getKey(), entry.getValue());
            }
        } else if (node instanceof ArrayNode){
            ArrayNode arrayNode = ((ArrayNode)node);
            for (int i = 0 ; i < arrayNode.size(); i ++){
                addEntityBody(builder, prefix + "[" + i + "]", arrayNode.get(i));
            }
        } else if (node instanceof ValueNode){
            ContentType strContent = ContentType.create("text/plain", StandardCharsets.UTF_8);
            builder.addTextBody(prefix, node.asText(), strContent);
        }
    }

}