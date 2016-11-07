package com.letv.whatslive.server.http;

import com.alibaba.fastjson.JSON;
import com.letv.whatslive.common.http.*;
import com.letv.whatslive.common.security.Cryptos;
import com.letv.whatslive.common.utils.StringUtil;
import com.letv.whatslive.common.utils.ThreadDistribution;
import com.letv.whatslive.common.utils.ZipUtils;
import com.letv.whatslive.server.util.Constant;
import com.letv.whatslive.server.util.KafkaLog;
import com.letv.whatslive.server.util.LogUtils;
import io.netty.buffer.ByteBuf;
import me.zhuoran.amoeba.netty.server.http.AbstractExecutor;
import me.zhuoran.amoeba.netty.server.http.AmoebaHttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zoran on 14-9-11.
 */
@Service("api")
public class AppRequestHandler extends AbstractExecutor {


    private final static Logger logger = LoggerFactory.getLogger(AppRequestHandler.class);


    @Autowired
    public Dispatcher dispatcher;


    private RequestData getRequestData(String requestBody) {
        RequestData req = null;
        try {
            req = JSON.parseObject(requestBody, RequestData.class);
        } catch (Throwable e) {
            logger.error("parse requestBody error : " + requestBody, e);
        }
        return req;
    }


    /**
     * @param request Http request wrapper
     * @return responseBody json string
     */
    private String parseAmoebaHttpRequest(AmoebaHttpRequest request) throws IllegalArgumentException {
        String requestBody = "";
        ByteBuf buffer = null;
        try {
            if (request.getHttpContent() != null) {
                buffer =request.getHttpContent();
                byte[] input = new byte[buffer.capacity()];
                for (int i = 0; i < buffer.capacity(); i ++) {
                    input[i] = buffer.getByte(i);
                }
                request.setHttpContent(null);
                if (input != null && input.length > 2) {

                    byte[] contents = Arrays.copyOfRange(input, 2, input.length);
                    //first step 解密
                    try {
                        if (input[0] == (int) 0x01) {
                            requestBody = Cryptos.decrypt(new String(contents));
                        }else{
                            new IllegalArgumentException("Request params error!");
                        }
                    } catch (Exception e) {
                        logger.error("decrypt data error " + e.getMessage(), e);

                    }

                    try {
                        //second step 解压缩
                        if (input[1] == (int) 0x01) {
                            byte[] values = requestBody.getBytes("iso8859-1");
                            requestBody = new String(ZipUtils.decompress(values), "UTF-8");
                        }else{
                            new IllegalArgumentException("Request params error! ");
                        }

                        //clear utf8mb4
                        requestBody = StringUtil.filterUTF8MB4(requestBody);

                    } catch (Exception e) {
                        logger.error("decompress data error " + e.getMessage(), e);
                    }

                }else{
                    new IllegalArgumentException("Request params error!");
                }

            }
        } catch (Exception e) {
//            LogUtils.logError("Parse http request raw data error ! " + e.getMessage(), e);
            logger.error("Parse http request raw data error ! " + e.getMessage(), e);
//            throw new IllegalArgumentException(e.getMessage());
        }
        return requestBody;
    }

    /**
     * @param request
     * @return
     */
    @Override
    public Object execute(final AmoebaHttpRequest request) {
        long startTime = System.currentTimeMillis();
        List<ResponseBody> results = new ArrayList<ResponseBody>();
        try {
            String requestBody = parseAmoebaHttpRequest(request);
            if (StringUtils.isBlank(requestBody)) {
                return null;
            }
            RequestData requestData = this.getRequestData(requestBody);
            if (requestData == null) {
                return null;
            }
            RequestHeader header = requestData.getHeader();
            for (RequestBody cmd : requestData.getCommands()) {
                ResponseBody result = null;
                try {
                    ThreadDistribution.getInstance().doWork(new KafkaLog(header, cmd));
                    result = dispatcher.execute(cmd.getCmd(), new Object[]{cmd.getData(), cmd.getSid(), header});
                    long execTime = System.currentTimeMillis() - startTime;
                    ThreadDistribution.getInstance().doWork(new KafkaLog(header, cmd, result, execTime));
                    if (result == null) {
                        result = writeErrorResponse(cmd, Constant.STATUS_SERVER_ERROR);
                    }
                } catch (IllegalArgumentException e) {
                    LogUtils.logError(e.getMessage(), e);
                    result = writeErrorResponse(cmd, Constant.STATUS_CLIENT_ERROR);
                } catch (Exception e) {
                    LogUtils.logError(e.getMessage(), e);
                    result = writeErrorResponse(cmd, Constant.STATUS_SERVER_ERROR);
                }
                results.add(result);
            }
            return getResponse(results);
        } catch (Throwable e) {
            LogUtils.logError(e.getMessage(), e);
        }
        return null;
    }


    public String getResponse(List<ResponseBody> results) {
        ResponseData responseData = new ResponseData();
        responseData.setResults(results);
        return JSON.toJSONString(responseData);
    }


    private ResponseBody writeErrorResponse(RequestBody requestBody, int ERROR_CODE) {
        ResponseBody body = new ResponseBody();
        if (requestBody == null) {
            body.setSid("null");
        } else {
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