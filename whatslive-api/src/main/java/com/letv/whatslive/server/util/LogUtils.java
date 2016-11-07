package com.letv.whatslive.server.util;

import com.alibaba.fastjson.JSON;
import com.letv.whatslive.common.http.RequestBody;
import com.letv.whatslive.common.http.RequestHeader;
import com.letv.whatslive.common.http.ResponseBody;
import com.letv.whatslive.common.utils.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 日志记录工具类
 *
 * @author zoran
 */
public class LogUtils {

    public static final Logger ERROR_LOG = LoggerFactory.getLogger("err");
    public static final Logger SERVER_LOG = LoggerFactory.getLogger("api");


    public static void logRequest(RequestHeader header, RequestBody body) {
        if (header != null && body != null) {
            StringBuilder buffer = new StringBuilder();
            String cmd = body.getCmd();
            String udid = header.getUdid();
            String userName = header.getUserName();
            String editionId = header.getEditionId();
            String corporationId = header.getCorporationId();
            String model = header.getModel();
            String platformId = header.getPlatformId();
            if (StringUtils.isBlank(udid)) {
                udid = "NULL";
            }
            if (StringUtils.isBlank(userName)) {
                userName = "UID_" + header.getUserId();
            }
            if (StringUtils.isBlank(platformId)) {
                platformId = "NULL";
            }
            if (StringUtils.isBlank(corporationId)) {
                corporationId = "NULL";
            }
            if (StringUtils.isBlank(editionId)) {
                editionId = "NULL";
            }
            if (StringUtils.isBlank(model)) {
                model = "NULL";
            }else{
                model= StringUtils.trim(model);
                model = model.replaceAll(" ", "");
            }
            buffer.append("CMD").append(" ").append(cmd).append(" ").append(userName).append(" ").append(udid);
            buffer.append(" ").append(platformId).append(" ").append(editionId).append(" ").append(corporationId).append(" ");
            buffer.append(model);

            SERVER_LOG.info(buffer.toString());
        }
    }

    public static void logParams(RequestHeader header, RequestBody body) {
        StringBuilder buffer = new StringBuilder();
        String cmd = body.getCmd();
        String userName = header.getUserName();
        buffer.append("PARAM").append(" ").append(cmd).append(" ").append(userName).append(" ");
        Set<Map.Entry<String, Object>> paraSet = body.getData().entrySet();
        for (Map.Entry<String, Object> entry : paraSet) { //Nexus 5]
            String value =  StringUtils.trim(ObjectUtils.toString(entry.getValue()));
            value = value.replaceAll("\\s*", "");
            buffer.append(entry.getKey()).append(":").append(value).append(" ");
        }
        buffer.append(header.toString());
        SERVER_LOG.info(buffer.toString());
    }


    public static void logResponse(RequestHeader header, RequestBody body, ResponseBody response, long execTime) {
        StringBuilder buffer = new StringBuilder();
        String cmd = body.getCmd();
        String udid = header.getUdid();
        String lastExecTime = header.getTimeCost();
        if (StringUtils.isBlank(lastExecTime)) {
            lastExecTime = "-1";
        }
        String userName = header.getUserName();
        buffer.append("RESULT").append(" ").append(cmd).append(" ").append(userName).append(" ").append(udid).append(" ");
        buffer.append(execTime).append(" ").append(lastExecTime).append(" ");


//        if (response.getData() != null) {
//            Object result = response.getData();
//            if (result instanceof Map) {
//                Map map = (Map) result;
//                Object object = map.get("dataList");
//                if(object!= null && object instanceof List){
//                    List list = (List)object;
//                    if(list.size()>0){
//                        buffer.append("size:").append(list.size());
//                    }else{
//                        buffer.append("size:0");
//                    }
//                }
//            }
//
//            if (result instanceof List) {
//                List list = (List) result;
//                buffer.append("size:").append(list.size());
//            }
//        }
        buffer.append(" ").append(JSON.toJSONString(response));
        SERVER_LOG.info(buffer.toString());
    }

    public static void commonLog(String message) {
        SERVER_LOG.info(message);
    }

    /**
     * 记录异常错误 格式 [exception]
     *
     * @param message
     * @param e
     */
    public static void logError(String message, Throwable e) {
        StringBuilder s = new StringBuilder();
        s.append(getBlock("exception")).append(" ");
        s.append(message).append(":");
        s.append(getException(e));
        ERROR_LOG.error(s.toString(),e);
    }

    public static void logError(String msg){
        StringBuilder s = new StringBuilder();
        s.append(getBlock("exception")).append(" ");
        s.append(msg);
        ERROR_LOG.error(msg);
    }
    public static String getBlock(Object msg) {
        if (msg == null) {
            msg = "";
        }
        return "[" + msg.toString() + "]";
    }


    public static Logger getErrorLog() {
        return ERROR_LOG;
    }

    public static String getException(Throwable e){
        StackTraceElement[] ste = e.getStackTrace();
        StringBuffer sb = new StringBuffer();
        sb.append(e.getMessage() + " ");
        for (int i = 0; i < ste.length; i++) {
            sb.append(ste[i].toString() + "\n");
        }
        return sb.toString();
    }

}
