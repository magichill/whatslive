package com.letv.whatslive.website.util.json;

import com.letv.whatslive.website.util.configuration.PropertyGetter;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

public class JsonUtils {

    public static final String RESPONSE_SUCCESS = "A000000"; // 可订购
    public static final String RESPONSE_PARAM_INVALID = "A000001"; // 参数无效
    public static final String RESPONSE_INVALID_APIKEY = "A000002"; // APIKEY 无效
    public static final String RESPONSE_NOT_FOND = "A000004"; // 没有数据
    public static final String RESPONSE_EXCEPTION = "E000000"; // 异常

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    public static String STATUS_OK() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", RESPONSE_SUCCESS);
        map.put("timestamp", sdf.format(new Date()));
        map.put("message", PropertyGetter.getString(RESPONSE_SUCCESS, StringUtils.EMPTY));
        return obj2Json(map);
    }

    public static String STATUS_OK(Object data) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", RESPONSE_SUCCESS);
        map.put("data", data);
        map.put("timestamp", sdf.format(new Date()));
        map.put("message", PropertyGetter.getString(RESPONSE_SUCCESS, StringUtils.EMPTY));
        return obj2Json(map);
    }

    public static String STATUS_OK(String code, Object data) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", code);
        map.put("data", data);
        map.put("timestamp", sdf.format(new Date()));
        map.put("message", PropertyGetter.getString(code, StringUtils.EMPTY));
        return obj2Json(map);
    }

    public static String DATA_NOT_FIND() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", RESPONSE_NOT_FOND);
        map.put("timestamp", sdf.format(new Date()));
        map.put("message", PropertyGetter.getString(RESPONSE_NOT_FOND, StringUtils.EMPTY));
        return obj2Json(map);
    }

    public static String PARAMETER_ERROR() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", RESPONSE_PARAM_INVALID);
        map.put("timestamp", sdf.format(new Date()));
        map.put("message", PropertyGetter.getString(RESPONSE_PARAM_INVALID, StringUtils.EMPTY));
        return obj2Json(map);
    }

    public static String EXCEPTION_ERROR() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", RESPONSE_EXCEPTION);
        map.put("timestamp", sdf.format(new Date()));
        map.put("message", PropertyGetter.getString(RESPONSE_EXCEPTION, StringUtils.EMPTY));
        return obj2Json(map);
    }

    public static String obj2Json(Object obj) {
        return JacksonJsonMapper.obj2Json(obj);
    }

    public static <T> T json2Obj(String json, Class<T> clazz) {
        return JacksonJsonMapper.json2Obj(json, clazz);
    }

    /**
     * json字符串的格式化
     *
     * @param json           需要格式的json串
     * @return
     * @author peiyuxin
     */
    public static String formatJson(String json) {
        try {
            if (StringUtils.isBlank(json)) {
                return StringUtils.EMPTY;
            }
            int fixedLength = 0;
            List<String> tokenList = new ArrayList<String>();
            String jsonTemp = json;
            // 预读取
            while (jsonTemp.length() > 0) {
                String token = getToken(jsonTemp);
                jsonTemp = jsonTemp.substring(token.length());
                token = token.trim();
                tokenList.add(token);
            }
            for (int i = 0; i < tokenList.size(); i++) {
                String token = tokenList.get(i);
                int length = token.getBytes().length;
                if (length > fixedLength && i < tokenList.size() - 1 && tokenList.get(i + 1).equals(":")) {
                    fixedLength = length;
                }
            }
            StringBuilder buf = new StringBuilder();
            int count = 0;
            for (int i = 0; i < tokenList.size(); i++) {
                String token = tokenList.get(i);
                if (token.equals(",")) {
                    buf.append(token);
                    doFill(buf, count);
                    continue;
                }
                if (token.equals(":")) {
                    buf.append(" ").append(token).append(" ");
                    continue;
                }
                if (token.equals("{")) {
                    String nextToken = tokenList.get(i + 1);
                    if (nextToken.equals("}")) {
                        i++;
                        buf.append("{ }");
                    } else {
                        count++;
                        buf.append(token);
                        doFill(buf, count);
                    }
                    continue;
                }
                if (token.equals("}")) {
                    count--;
                    doFill(buf, count);
                    buf.append(token);
                    continue;
                }
                if (token.equals("[")) {
                    String nextToken = tokenList.get(i + 1);
                    if (nextToken.equals("]")) {
                        i++;
                        buf.append("[ ]");
                    } else {
                        count++;
                        buf.append(token);
                        doFill(buf, count);
                    }
                    continue;
                }
                if (token.equals("]")) {
                    count--;
                    doFill(buf, count);
                    buf.append(token);
                    continue;
                }
                buf.append(token);
                // 左对齐
                if (i < tokenList.size() - 1 && tokenList.get(i + 1).equals(":")) {
                    int fillLength = fixedLength - token.getBytes().length;
                    if (fillLength > 0) {
                        for (int j = 0; j < fillLength; j++) {
                            buf.append(" ");
                        }
                    }
                }
            }
            return buf.toString();
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

    private static String getToken(String json) {
        StringBuilder buf = new StringBuilder();
        boolean isInYinHao = false;
        while (json.length() > 0) {
            String token = json.substring(0, 1);
            json = json.substring(1);
            if (!isInYinHao && (token.equals(":") || token.equals("{") || token.equals("}") || token.equals("[") || token.equals("]") || token.equals(","))) {
                if (buf.toString().trim().length() == 0) {
                    buf.append(token);
                }
                break;
            }
            if (token.equals("\\")) {
                buf.append(token);
                buf.append(json.substring(0, 1));
                json = json.substring(1);
                continue;
            }
            if (token.equals("\"")) {
                buf.append(token);
                if (isInYinHao) {
                    break;
                } else {
                    isInYinHao = true;
                    continue;
                }
            }
            buf.append(token);
        }
        return buf.toString();
    }

    private static void doFill(StringBuilder buf, int count) {
        buf.append("\n");
        for (int i = 0; i < count; i++) {
            buf.append(" ");
        }
    }

}
