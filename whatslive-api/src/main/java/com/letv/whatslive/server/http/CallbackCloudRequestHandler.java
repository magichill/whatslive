package com.letv.whatslive.server.http;

import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.server.live.ApplyLive;
import com.letv.whatslive.server.service.ProgramService;
import com.letv.whatslive.server.util.CloudUtils;
import com.letv.whatslive.server.util.Constant;
import com.letv.whatslive.server.util.JsonUtils;
import com.letv.whatslive.server.util.LogUtils;
import com.mongodb.DBObject;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.HttpPostMultipartRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import me.zhuoran.amoeba.netty.server.http.AbstractExecutor;
import me.zhuoran.amoeba.netty.server.http.AmoebaHttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import io.netty.handler.codec.http.multipart.Attribute;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by gaoshan on 15-10-14.
 */
@Service("cloudcallback")
public class CallbackCloudRequestHandler extends AbstractExecutor {

    private final StringBuilder responseContent = new StringBuilder();
    private final Map result = Maps.newHashMap();

    @Resource
    private ProgramService programService;

    private Map<String,String> parseAmoebaHttpRequest(AmoebaHttpRequest request) throws IllegalArgumentException, UnsupportedEncodingException {
        if(request.getMethod().equalsIgnoreCase("post")){
            HttpPostRequestDecoder postRequestDecoder = new HttpPostRequestDecoder(request.getHttpRequest());
            if(postRequestDecoder.isMultipart()){
                try {
                    while (postRequestDecoder.hasNext()) {
                        InterfaceHttpData data = postRequestDecoder.next();
                        if (data != null) {
                            try {
                                writeHttpData(data);
                            } finally {
                                data.release();
                            }
                        }
                    }
                } catch (HttpPostRequestDecoder.EndOfDataDecoderException e1) {
                    return null;
                }
            }else{
                LogUtils.logError("非form-data请求方式");
                return null;
            }
            return result;
        }else{
            LogUtils.logError("非post请求方式");
            return null;
        }
    }

    private Map<String,String> writeHttpData(InterfaceHttpData data) {
        /**
         * HttpDataType有三种类型
         * Attribute, FileUpload, InternalAttribute
         */
        if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
            Attribute attribute = (Attribute) data;
            try {
                result.put(attribute.getName(),attribute.getValue());
            } catch (IOException e1) {
                e1.printStackTrace();
                responseContent.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ":"
                        + attribute.getName() + " Error while reading value: " + e1.getMessage() + "\r\n");
                return null;
            }
        }
        return result;
    }

//    private Map<String,String> conver(Map<String ,List<String>> params){
//        Map<String,String> result = new HashMap<String, String>();
//        for (Map.Entry<String, List<String>> map : params.entrySet()) {
//            result.put(map.getKey(), map.getValue().get(0));
//        }
//        return result;
//
//    }

    @Override
    public Object execute(AmoebaHttpRequest request) {
        String result = StringUtils.EMPTY;
        try {
            Map<String,String> paramsMap = parseAmoebaHttpRequest(request);
            if(paramsMap == null){
                return JsonUtils.PARAMETER_ERROR("请求方式错误");
            }else{
                String type = paramsMap.get("type");
                String activeId = paramsMap.get("activeId");
                String eventTime = paramsMap.get("eventTime");
                String streamId = paramsMap.get("streamId");

//                DBObject program = programService.getProgramByActivityId(activeId);
                //推流开始
                if(type.equals(Constant.STREAM_START_CODE)){
//                    programService.update
                }
                //活动结束
                if(type.equals(Constant.ACTIVITY_END_CODE)){

                }
                //推流中断
                if(type.equals(Constant.STREAM_INTERUPT_CODE)){

                }

                //TODO
                LogUtils.commonLog("type:"+type+",activeId:"+activeId+",eventTime:"+eventTime+",streamId:"+streamId);
            }
            return JsonUtils.STATUS_OK("cloud callback success");
        } catch (Exception e) {
            LogUtils.logError("[callback exception]:" + e.getMessage());
            result = JsonUtils.EXCEPTION_ERROR();
        }
        return result;
    }
}
