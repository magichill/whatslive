package com.letv.whatslive.server.controller;

import com.google.common.collect.Maps;
import com.letv.whatslive.common.http.RequestHeader;
import com.letv.whatslive.common.http.ResponseBody;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.User;
import com.letv.whatslive.model.dto.FollowerDTO;
import com.letv.whatslive.model.dto.UserDTO;
import com.letv.whatslive.server.service.FriendService;
import com.letv.whatslive.server.service.MessageService;
import com.letv.whatslive.server.service.UserService;
import com.letv.whatslive.server.util.Constant;
import com.letv.whatslive.server.util.LogUtils;
import org.springframework.stereotype.Service;
import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by gaoshan on 15-7-3.
 */
@Service
public class FriendController extends BaseController{

    @Resource
    private FriendService friendService;

    @Resource
    private MessageService messageService;

    /**
     * 关注用户接口
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody focusFriends(Map<String, Object> params, String sid, RequestHeader header){
        Long fId = ObjectUtils.toLong(params.get("fId"));
        Integer status = ObjectUtils.toInteger(params.get("status"));
        Long userId = ObjectUtils.toLong(header.getUserId());
        Map<String,Object> checkParam = Maps.newHashMap();
        checkParam.put("fId",fId);
        checkParam.put("status",status);
        checkParam.put("userId",userId);
        String checkResult = validateParams(checkParam);
        if(checkResult != null){
            LogUtils.logError(checkResult);
            return getErrorResponse(sid,Constant.PARAMS_ERROR_CODE);
        }

        Map<String,Object> result = Maps.newHashMap();
        try{
            friendService.focus(userId,fId,status);
            if(status == 1) {
                messageService.userSendMessage(userId, fId, ObjectUtils.toString(header.getUserName(), "") + Constant.MESSAGE_FOCUS);
            }
            result.put("status",friendService.getFriendShipByUserId(userId,fId));
        }catch (Exception e){
            LogUtils.logError("fId =="+fId+" [focus friend api] exception ",e);
            e.printStackTrace();
            return getErrorResponse(sid,Constant.SERVER_ERROR_CODE);
        }
        return getResponseBody(sid,result);
    }

    /**
     * 获取粉丝列表接口
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody getFollowers(Map<String, Object> params, String sid, RequestHeader header){
        Long userId = ObjectUtils.toLong(header.getUserId());
        Integer start = ObjectUtils.toInteger(params.get("start"));
        Integer limit = ObjectUtils.toInteger(params.get("limit"));
        Map<String,Object> checkParam = Maps.newHashMap();
        checkParam.put("start",start);
        checkParam.put("limit",limit);
        checkParam.put("userId",userId);
        String checkResult = validateParams(checkParam);
        if(checkResult != null){
            LogUtils.logError(checkResult);
            return getErrorResponse(sid,Constant.PARAMS_ERROR_CODE);
        }
        try{
            List<FollowerDTO> users = friendService.getFollowers(userId,start,limit);
            return getResponseBody(sid,users);
        }catch (Exception e){
            LogUtils.logError("userId = "+userId+" [other follower api] exception ",e);
            e.printStackTrace();
            return getErrorResponse(sid,Constant.SERVER_ERROR_CODE);
        }


    }

    /**
     * 获取关注列表接口
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody getFocus(Map<String, Object> params, String sid, RequestHeader header){
        Long userId = ObjectUtils.toLong(header.getUserId());
        Integer start = ObjectUtils.toInteger(params.get("start"));
        Integer limit = ObjectUtils.toInteger(params.get("limit"));
        Map<String,Object> checkParam = Maps.newHashMap();
        checkParam.put("start",start);
        checkParam.put("limit",limit);
        checkParam.put("userId",userId);
        String checkResult = validateParams(checkParam);
        if(checkResult != null){
            return getErrorResponse(sid,checkResult);
        }
        try {
            List<FollowerDTO> users = friendService.getFocus(userId, start, limit);
            return getResponseBody(sid, users);
        }catch(Exception e){
            LogUtils.logError("[get focus api] exception ",e);
            e.printStackTrace();
            return getErrorResponse(sid,Constant.SERVER_ERROR_CODE);
        }
    }

    /**
     * 获取他人粉丝列表接口
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody getOtherFollowers(Map<String, Object> params, String sid, RequestHeader header){
        Long userId = ObjectUtils.toLong(header.getUserId());
        Long otherId = ObjectUtils.toLong(params.get("otherId"));
        Integer start = ObjectUtils.toInteger(params.get("start"));
        Integer limit = ObjectUtils.toInteger(params.get("limit"));
        Map<String,Object> checkParam = Maps.newHashMap();
        checkParam.put("otherId",otherId);
        checkParam.put("start",start);
        checkParam.put("limit",limit);
        String checkResult = validateParams(checkParam);
        if(checkResult != null){
            return getErrorResponse(sid,Constant.PARAMS_ERROR_CODE);
        }
        try{
            List<FollowerDTO> users = friendService.getOtherFollowers(otherId,userId,start,limit);
            return getResponseBody(sid,users);
        }catch (Exception e){
            LogUtils.logError("otherId == "+otherId+";[other followers api] exception ",e);
            return getErrorResponse(sid,Constant.SERVER_ERROR_CODE);
        }

    }

    /**
     * 获取他人关注列表接口
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody getOtherFocus(Map<String, Object> params, String sid, RequestHeader header){
        Long userId = ObjectUtils.toLong(header.getUserId());
        Long otherId = ObjectUtils.toLong(params.get("otherId"));
        Integer start = ObjectUtils.toInteger(params.get("start"));
        Integer limit = ObjectUtils.toInteger(params.get("limit"));
        Map<String,Object> checkParam = Maps.newHashMap();
        checkParam.put("otherId",otherId);
        checkParam.put("start",start);
        checkParam.put("limit",limit);
        String checkResult = validateParams(checkParam);
        if(checkResult != null){
            return getErrorResponse(sid,Constant.PARAMS_ERROR_CODE);
        }
        try {
            List<FollowerDTO> users = friendService.getOtherFocus(otherId, userId, start, limit);
            return getResponseBody(sid, users);
        }catch (Exception e){
            LogUtils.logError("otherId=="+otherId+" [other focus api] exception ",e);
            return getErrorResponse(sid,Constant.SERVER_ERROR_CODE);
        }
    }

}
