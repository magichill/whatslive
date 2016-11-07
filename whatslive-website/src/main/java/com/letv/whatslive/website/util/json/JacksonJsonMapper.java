package com.letv.whatslive.website.util.json;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Title: Jackson包装类
 * Desc: JacksonJson简单封装
 */
public class JacksonJsonMapper {

    private static volatile ObjectMapper objectMapper = null;

    private JacksonJsonMapper() {

    }

    /**
     * 获取唯一的ObjectMapper对象
     *
     * @return
     */
    public static ObjectMapper getInstance() {
        if (objectMapper == null) {
            synchronized (ObjectMapper.class) {
                if (objectMapper == null) {
                    objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                }
            }
        }
        return objectMapper;
    }

    public static <T> T json2Obj(String json, Class<T> clazz) {
        try {
            return getInstance().readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String obj2Json(Object obj) {
        try {
            return getInstance().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
