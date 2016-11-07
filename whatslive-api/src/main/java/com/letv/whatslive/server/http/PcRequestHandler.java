package com.letv.whatslive.server.http;

import com.alibaba.fastjson.JSON;

import com.letv.whatslive.common.http.*;
import com.letv.whatslive.common.utils.ThreadDistribution;
import com.letv.whatslive.server.util.Constant;
import com.letv.whatslive.server.util.KafkaLog;
import com.letv.whatslive.server.util.LogUtils;
import io.netty.buffer.ByteBuf;
import me.zhuoran.amoeba.netty.server.http.AbstractExecutor;
import me.zhuoran.amoeba.netty.server.http.AmoebaHttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaoshan on 14-11-2.
 */
@Service("inner")
public class PcRequestHandler extends AbstractExecutor {

    private static final Logger logger = LoggerFactory.getLogger(PcRequestHandler.class);

    @Resource
    private Dispatcher dispatcher;

    private String parseAmoebaHttpRequest(AmoebaHttpRequest request) throws IllegalArgumentException {
        String requestBody = "";
        ByteBuf buffer = null;
        try{
            if (request.getHttpContent() != null) {
                buffer =request.getHttpContent();
                byte[] input = new byte[buffer.capacity()];
                for (int i = 0; i < buffer.capacity(); i ++) {
                    input[i] = buffer.getByte(i);
                }
                requestBody = new String(input);
            }
        }catch(Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
        return requestBody;
    }

    @Override
    public Object execute(final AmoebaHttpRequest request) {
//        System.out.println(request.getHttpRequest().getUri().toString());
        long startTime = System.currentTimeMillis();
        List<ResponseBody> results = new ArrayList<ResponseBody>();
        try {
            String requestBody = parseAmoebaHttpRequest(request);
            if (StringUtils.isBlank(requestBody)) {
                return null;
            }
            RequestData requestData = this.getRequestData(requestBody);
            RequestHeader header = requestData.getHeader();
            for (RequestBody cmd : requestData.getCommands()) {
                ResponseBody result = null;
                try {

                    result = dispatcher.execute(cmd.getCmd(),new Object[]{cmd.getData(), cmd.getSid(),header});
                    long execTime = System.currentTimeMillis() - startTime;
                    ThreadDistribution.getInstance().doWork(new KafkaLog(header, cmd, result, execTime));
                    if (result == null) {
                        result = writeErrorResponse(cmd, Constant.STATUS_SERVER_ERROR);
                    }
                } catch (IllegalArgumentException e) {
                    logger.error(e.getMessage());
                    result = writeErrorResponse(cmd, Constant.STATUS_CLIENT_ERROR);
                } catch (Throwable t) {
                    logger.error(t.getMessage());
                    result = writeErrorResponse(cmd, Constant.STATUS_SERVER_ERROR);
                }
                results.add(result);
            }
            return getResponse(results);
        } catch (Throwable e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    private RequestData getRequestData(String requestBody) {
        RequestData req = JSON.parseObject(requestBody, RequestData.class);
        return req;
    }

    public String getResponse(List<ResponseBody> results) {
        ResponseData responseData = new ResponseData();
        responseData.setResults(results);
        return JSON.toJSONString(responseData);
    }

    public String getJsonpResponse(List<ResponseBody> results) {
        ResponseData responseData = new ResponseData();
        responseData.setResults(results);
        return "handler"+JSON.toJSONString(responseData);
    }

    private ResponseBody writeErrorResponse(RequestBody requestBody, int ERROR_CODE) {
        ResponseBody body = new ResponseBody();
        if(requestBody ==null){
            body.setSid("null");
        }else{
            body.setSid(requestBody.getSid());
        }
        body.setData(null);
        if (ERROR_CODE == Constant.STATUS_SERVER_ERROR) {
            body.setResult("500");
        }
        if (ERROR_CODE == Constant.STATUS_CLIENT_ERROR) {
            body.setResult("601");
        }
        if (ERROR_CODE == Constant.STATUS_UNAUTHORIZED) {
            body.setResult("400");
        }
        return body;
    }
}
