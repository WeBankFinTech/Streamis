package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.http;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Json utils
 */
public class Json {
    private static final String PREFIX = "[";
    private static final String SUFFIX = "]";

    private static ObjectMapper mapper;

    static{
        mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        //empty beans allowed
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //cancel to scape non ascii
        mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
    }
    private Json(){}

    @SuppressWarnings("unchecked")
    public static <T> T fromJson(String json, Class<?> clazz, Class<?>... parameters) throws Exception{
        if(null != json && !json.trim().equals("")){
            try{
                if(parameters.length > 0){
                    return (T)mapper.readValue(json, mapper.getTypeFactory().constructParametricType(clazz, parameters));
                }
                if(json.startsWith(PREFIX)
                        && json.endsWith(SUFFIX)){
                    JavaType javaType = mapper.getTypeFactory()
                            .constructParametricType(ArrayList.class, clazz);
                    return mapper.readValue(json, javaType);
                }
                return (T)mapper.readValue(json, clazz);
            } catch (Exception e) {
                String message = "Unable to deserialize to object from string(json) in type: [" +
                        (null != clazz ? clazz.getSimpleName() : "UNKNOWN") + "], parameters size: " + parameters.length;
                throw new RuntimeException(message, e);
            }
        }
        return null;
    }

    public static <T> T fromJson(InputStream stream, Class<?> clazz, Class<?>... parameters) throws Exception{
        StringBuilder builder = new StringBuilder();
        String jsonStr = null;
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            while((jsonStr = reader.readLine()) != null){
                builder.append(jsonStr);
            }
            reader.close();
        }catch(Exception e){
            String message = "Unable to deserialize to object from stream(json) in type: [" +
                    (null != clazz ? clazz.getSimpleName() : "UNKNOWN") + "], parameters size: " + parameters.length;
            throw new RuntimeException(message, e);
        }
        return fromJson(builder.toString(), clazz, parameters);
    }

    public static String toJson(Object obj, Class<?> model) {
        ObjectWriter writer = mapper.writer();
        if(null != obj){
            try{
                if(null != model){
                    writer = writer.withView(model);
                }
                return writer.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                String message = "Unable to serialize the object in type: ["+ (null != model ? model.getSimpleName() : "UNKNOWN") + "]";
                throw new RuntimeException(message, e);
            }
        }
        return null;
    }

    /**
     * Convert object using serialization and deserialization
     *
     * @param simpleObj simpleObj
     * @param tClass    type class
     * @param <T>       T
     * @return result
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(Object simpleObj, Class<?> tClass, Class<?>... parameters) {
        try {
            if (parameters.length > 0) {
                return mapper.convertValue(simpleObj, mapper.getTypeFactory().constructParametricType(tClass, parameters));
            }
            return (T) mapper.convertValue(simpleObj, tClass);
        } catch (Exception e) {
            String message = "Fail to process method 'convert(" + simpleObj + ": " + simpleObj.getClass().getSimpleName() +
                    ", " + tClass.getSimpleName() + ": "+ Class.class + ", ...: " + Class.class + ")";
            throw new RuntimeException(message, e);
        }
    }

    public static <T> T convert(Object simpleObj, JavaType javaType) {
        try {
            return mapper.convertValue(simpleObj, javaType);
        } catch (Exception e) {
            String message = "Fail to process method 'convert(" + simpleObj + ": " + simpleObj.getClass().getSimpleName() +
                    ", " + javaType.getTypeName() + ": "+ JavaType.class + ")";
            throw new RuntimeException(message, e);
        }
    }

    public static ObjectMapper getMapper(){
        return mapper;
    }

}

