package com.letv.whatslive.server.controller;

import com.letv.whatslive.common.http.RequestHeader;
import com.letv.whatslive.common.http.ResponseBody;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.server.service.DeviceService;
import com.letv.whatslive.server.util.Constant;
import com.letv.whatslive.server.util.LogUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by gaoshan on 15-8-28.
 */
@Service
public class DeviceController extends BaseController {

    @Resource
    private DeviceService deviceService;


    /**
     * 更新设备的channelId
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody updateChannelId(Map<String, Object> params, String sid, RequestHeader header){
        String channelId = ObjectUtils.toString(params.get("channelId"));
        String udid = header.getUdid();
        String userId = header.getUserId();
        if(channelId == null || udid == null){
            return getErrorResponse(sid, Constant.PARAMS_ERROR_CODE);
        }
        try {
            boolean result = deviceService.updateChannelId(udid, channelId);
        }catch (Exception e){
            LogUtils.logError("fail to update channelid,userId = "+userId,e);
            return getErrorResponse(sid,Constant.OTHER_ERROR_CODE);
        }
        return getResponseBody(sid,"ok");
    }
}
