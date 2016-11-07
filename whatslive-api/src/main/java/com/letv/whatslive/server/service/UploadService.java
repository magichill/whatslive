package com.letv.whatslive.server.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.httpclient.HttpClientUtil;
import com.letv.whatslive.common.httpclient.HttpFetchResult;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Program;
import com.letv.whatslive.model.TokenVo;
import com.letv.whatslive.server.util.LogUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gaoshan on 15-8-7.
 */
@Service
public class UploadService {


    @Value("${up.token.uri}")
    protected String UP_TOKEN_URI;

    @Value("${up.compress.uri}")
    protected String UP_COMPRESS_URI;

    @Value("${up.compress.callback.uri}")
    protected String UP_COMPRESS_CALLBACK_URI;

    @Value("${up.icon.compress.callback.uri}")
    protected String UP_ICON_COMPRESS_CALLBACK_URI;

    @Value("${up.appkey}")
    protected String UP_APPKEY;

    @Resource
    private ProgramService programService;


    public Map getToken(Map<String,Object> params) throws Exception {
        Integer vType = ObjectUtils.toInteger(params.get("vType"));  //图片上传类型 1：封面图片 2：用户头像
        String  outkey = ObjectUtils.toString(params.get("outkey"));
        List<TokenVo> tokenList = Lists.newArrayList();
        if(vType == 1){
            tokenList.add(new TokenVo(UP_APPKEY,outkey));
        }else{
            tokenList.add(new TokenVo(UP_APPKEY,"iconKey"+outkey));
        }
        Map paramMap = Maps.newHashMap();
        paramMap.put("list",tokenList);
        System.out.println(JSON.toJSONString(paramMap));
        return getResult(UP_TOKEN_URI,JSON.toJSONString(paramMap));
    }


    /**
     * 图片压缩接口
     * @param params
     * @return
     * @throws Exception
     */
    public Map compressPic(Map<String,Object> params) throws Exception {
        String outkey = ObjectUtils.toString(params.get("outkey"));
        if(StringUtils.isBlank(outkey)){
            LogUtils.logError("outkey could not be null");
            return getErrorResult("outkey could not be null");
        }
        String fileUrl = ObjectUtils.toString(params.get("fileUrl"));

        if(outkey.indexOf("iconKey")>=0){
            params.put("callback",UP_ICON_COMPRESS_CALLBACK_URI);
        }else{
            Program program = programService.getProgramById(ObjectUtils.toLong(outkey));
            if(program != null) {
//                program.setPicture(fileUrl);
                programService.updateProgramPic(program);
                params.put("callback", UP_COMPRESS_CALLBACK_URI);
            }else{
                Map result = Maps.newHashMap();
                result.put("error","program is not exist");
                return result;
            }
        }
        String compressParam = paramWrapper(params);
        return getResult(UP_COMPRESS_URI,compressParam);
    }


    protected Map getResult(String uri,String params) throws Exception {
        Map resultMap = Maps.newHashMap();
        try{
            HttpFetchResult result = HttpClientUtil.requestPost(uri, params);
            if(null != result){
                return JSON.parseObject(result.getContent(), HashMap.class);
            }
        }catch(Exception e){
            LogUtils.logError("Request " + uri + " params " + params + " ", e);
        }
        return resultMap;
    }

    protected Map getErrorResult(String msg){
        Map resultMap = Maps.newHashMap();
        resultMap.put("msg",msg);
        return resultMap;
    }

    protected String paramWrapper(Map<String,Object> params){
        return JSON.toJSONString(params);
    }
}
