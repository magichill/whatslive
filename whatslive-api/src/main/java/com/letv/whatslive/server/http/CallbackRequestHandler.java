package com.letv.whatslive.server.http;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.http.*;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.common.utils.ThreadDistribution;
import com.letv.whatslive.server.live.ApplyLive;
import com.letv.whatslive.server.service.ProgramService;
import com.letv.whatslive.server.util.*;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.QueryStringDecoder;
import me.zhuoran.amoeba.netty.server.http.AbstractExecutor;
import me.zhuoran.amoeba.netty.server.http.AmoebaHttpRequest;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gaoshan on 15-8-21.
 */
@Service("videocallback")
public class CallbackRequestHandler extends AbstractExecutor {

    @Resource
    private ProgramService programService;

    private Map parseAmoebaHttpRequest(AmoebaHttpRequest request) throws IllegalArgumentException, UnsupportedEncodingException {
        Map<String,List<String>> paramsMap = null;
        if(request.getMethod().equalsIgnoreCase("post")){
            String postParams = request.getContent();
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri()+"?"+postParams);
            paramsMap = queryStringDecoder.parameters();
        }else{
            paramsMap = request.getParams();
        }
        return paramsMap;
    }

    private Map<String,String> conver(Map<String ,List<String>> params){
        Map<String,String> result = new HashMap<String, String>();
        for (Map.Entry<String, List<String>> map : params.entrySet()) {
            result.put(map.getKey(), map.getValue().get(0));
        }
        return result;

    }

    @Override
    public Object execute(AmoebaHttpRequest request) {
        String result = StringUtils.EMPTY;
        try {
            Map params = parseAmoebaHttpRequest(request);
            if(params==null || params.size()==0){
                return JsonUtils.PARAMETER_ERROR("参数不能为空！");
            }
            params = conver(params);
            String status = ObjectUtils.toString(params.get("status"));
            String videoId = ObjectUtils.toString(params.get("video_id"));
            Map<String,String> signParam = Maps.newHashMap();
            signParam.put("timestamp", ObjectUtils.toString(params.get("timestamp")));
            signParam.put("user_unique", ObjectUtils.toString(params.get("user_unique")));
            signParam.put("format","json");
            signParam.put("ver","2.0");
            signParam.put("status",status);
            signParam.put("video_id",videoId);
            String sign = CloudUtils.generateSign(params);
            String checkSign = CloudUtils.generateCheckSign(params);
            if(checkSign.equals(sign)){

                if(status.equals("OK")){
                    LogUtils.commonLog("转码回调成功，videoId=="+videoId);
                    ApplyLive applyLive = new ApplyLive();
                    String activityId = applyLive.getActivityId(videoId);
                    if(StringUtils.isBlank(activityId)){
                        activityId = applyLive.getActivityId(videoId);
                    }
                    LogUtils.commonLog("转码回调成功，活动id=="+activityId);
                    CloudUtils cloudUtils = new CloudUtils();
                    Map<String,String> videoInfo = cloudUtils.getVideoInfo(videoId,Constant.USER_UNIQUE);
                    String vuid = videoInfo.get("video_unique");
                    long duration = ObjectUtils.toLong(videoInfo.get("duration"),0l);
                    LogUtils.commonLog("转码回调成功，视频时长=="+duration+"秒");
                    programService.updateProgramByVideoId(activityId,videoId,vuid,duration);
                }

                return JsonUtils.STATUS_OK("ok");
            }else{
                return JsonUtils.PARAMETER_ERROR("sign签名校验失败");
            }

        } catch (Exception e) {
            LogUtils.logError("[callback exception]:"+e.getMessage());
            result = JsonUtils.EXCEPTION_ERROR();
        }
        return result;
    }

}
