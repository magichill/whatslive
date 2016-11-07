package com.letv.whatslive.server.util;

import com.alibaba.fastjson.JSON;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gaoshan on 15-8-21.
 */
public class JsonUtils {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    private static final int SUCCESS_CODE = 200;
    private static final int DATA_NOT_FIND_CODE = 10;
    private static final int PARAM_ERROR_CODE = 601;
    private static final int SERVER_ERROR_CODE = 500;

    public static String STATUS_OK(String message) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", SUCCESS_CODE);
        map.put("message", message);
        return JSON.toJSONString(map);
    }


    public static String DATA_NOT_FIND() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", DATA_NOT_FIND_CODE);
        map.put("message", "no video");
        return JSON.toJSONString(map);
    }

    public static String PARAMETER_ERROR(String message) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", PARAM_ERROR_CODE);
        map.put("message", message);
        return JSON.toJSONString(map);
    }

    public static String EXCEPTION_ERROR() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", SERVER_ERROR_CODE);
        map.put("message", "server error");
        return JSON.toJSONString(map);
    }


    public static String TO_JSON(Object data) {
        return JSON.toJSONString(data);
    }

    public static Object TO_OBJ(String source, Class<?> clazz){
        return JSON.parseObject(source, clazz);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseMap(String source){
        Map<String, Object> map = null;
        try {
            map = (Map<String, Object>)TO_OBJ(source, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("token", "f4db850126b5f52647f5cb020ed794e0d6a0a070351f17720f7ebbbb");
        map.put("ip", "119.123.106.190");
        map.put("devid", "CDBBEA6A-466A-45E5-94E4-4219542EEE9A");
        map.put("sysver", "6.1.3");
        map.put("model", "iPad");
        map.put("appType", "iPad");
        map.put("appVersion", "V3.8");
        map.put("pcode", "010510000");
        map.put("isEffective", 1);
        map.put("upTime", new Date().getTime());

        String string = TO_JSON(map);
        System.out.println("string=" + string);
    }
}
