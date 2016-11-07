package com.letv.whatslive.server.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.http.RequestHeader;
import com.letv.whatslive.common.http.ResponseBody;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.server.service.ChatService;
import com.letv.whatslive.server.util.Constant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by gaoshan on 15-7-9.
 */

@Service
public class ChatController extends BaseController {

    @Resource
    private ChatService chatService;

    /**
     * 获取聊天服务器地址
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody chatServer(Map<String, Object> params, String sid, RequestHeader header) {
        Map<String,Object> result = chatService.chatServer();
        return getResponseBody(sid,result);
    }
    /**
     * 聊天接口
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody chat(Map<String, Object> params, String sid, RequestHeader header) {
        String userId =header.getUserId();
        String chatMsg = ObjectUtils.toString(params.get("msg"));
        String roomId = ObjectUtils.toString(params.get("roomId"));
        params.put("userId",userId);
        params.put("roomId",roomId);
        String validateResult = validateParams(params);
        if(validateResult==null){
            logger.error(validateResult);
            return getErrorResponse(sid,validateResult);
        }
        try {
            chatService.chat(userId,chatMsg,roomId);
        }catch (Exception e){
            logger.error("fail to create chatroom");
            e.printStackTrace();
            return getErrorResponse(sid,"fail to create chatroom");
        }
        return getResponseBody(sid,"ok");
    }

    /**
     *
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody createRoom(Map<String, Object> params, String sid, RequestHeader header) {
        String userId = header.getUserId();
        String state = ObjectUtils.toString(params.get("state")); //聊天室状态  1:创建聊天室 0:关闭聊天室
        String roomId = ObjectUtils.toString(params.get("roomId"));
        try {
//            chatService.createRoom(userId,state,roomId);
        }catch (Exception e){
            logger.error("fail to create chatroom");
            e.printStackTrace();
            return getErrorResponse(sid,"fail to create chatroom");
        }
        return getResponseBody(sid,"ok");
    }

}
