package com.webank.wedatasphere.streamis.errorcode.manager.cache;

import org.apache.linkis.errorcode.common.LinkisErrorCode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StreamisErrorCodeCache {
    private static Map<String, List<LinkisErrorCode>> cache = new ConcurrentHashMap<>();

    public static void put(String key, List<LinkisErrorCode> value) {
        cache.put(key, value);
    }

    public static List<LinkisErrorCode> get(String key) {
        return cache.get(key);
    }

    public static void remove(String key) {
        cache.remove(key);
    }

    public static void clear() {
        cache.clear();
    }
}
