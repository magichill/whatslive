package com.letv.whatslive.server.controller;

import com.google.common.collect.Maps;
import com.letv.whatslive.common.http.RequestHeader;
import com.letv.whatslive.common.http.ResponseBody;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.server.service.UploadService;
import com.letv.whatslive.server.util.Constant;
import com.letv.whatslive.server.util.LogUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by gaoshan on 15-8-7.
 */
@Service
public class UploadController extends BaseController {

    @Resource
    private UploadService uploadService;

    /**
     * 获取上传token接口
     * @param params
     * vType = 1 为视频封面  2为用户头像
     * @param sid
     * @return
     */
    public ResponseBody getUpToken(Map<String, Object> params, String sid,RequestHeader header){
        String vType = ObjectUtils.toString(params.get("vType"));
        if(vType == null){
            return getErrorResponse(sid,Constant.PARAMS_ERROR_CODE);
        }
        Map result = Maps.newHashMap();
        try {
            result = uploadService.getToken(params);
            return getResponseBody(sid, result.get("list"));
        } catch (Exception e) {
            LogUtils.logError("UploadController uptoken Failure : ", e);
            return getErrorResponse(sid,Constant.GET_TOKEN_ERROR_CODE);
        }
    }


    /**
     * 图片压缩接口
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody compressPic(Map<String,Object> params,String sid,RequestHeader header){
        Map result = Maps.newHashMap();
        try {
            result = uploadService.compressPic(params);
            return getResponseBody(sid,result);
        } catch (Exception e){
            LogUtils.logError("UploadController compressPic Failure : " , e);
            return getErrorResponse(sid, Constant.COMPRESS_PIC_ERROR_CODE);
        }
    }

}
