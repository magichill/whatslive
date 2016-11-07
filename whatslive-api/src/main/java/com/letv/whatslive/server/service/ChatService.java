package com.letv.whatslive.server.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.common.utils.MD5Utils;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.User;
import com.letv.whatslive.model.dto.RedisUser;
import com.letv.whatslive.redis.JedisDAO;
import com.letv.whatslive.server.util.Constant;
import com.letv.whatslive.server.util.LogUtils;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by gaoshan on 15-7-16.
 */
@Service
public class ChatService {

    private final static Logger logger = LoggerFactory.getLogger(ChatService.class);

//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//    @Resource(name = "redisTemplate")
//    private ValueOperations<String, Object> valOps;

      @Autowired
      private JedisDAO jedisDAO;

//    public void createRoom(String userId,String state,String roomId){
//        Room room = new Room();
//        room.setRoomId(roomId);
//        room.setUserId(userId);
//        room.setState(state);
//        stringRedisTemplate.convertAndSend("chatroom", ObjectUtils.toString(JSON.toJSON(room)));
//    }

    public Map chatServer(){
        Map<String,Object> result = Maps.newHashMap();
        result.put("server", Constant.CHAT_SERVER);
        return result;
    }

    public void chat(String userId,String chatMsg,String roomId){
        Chat chat = new Chat();
        chat.setUserId(userId);
        chat.setRoomId(roomId);
        chat.setMessage(chatMsg);
        jedisDAO.getJedisWriteTemplate().publish("chat", ObjectUtils.toString(JSON.toJSON(chat)));
    }

    public void generateCode(String roomId,User user){
        RedisUser redisUser = new RedisUser();
        redisUser.setUid(ObjectUtils.toString(user.getUserId()));
        redisUser.setNickName(user.getNickName());
        redisUser.setPicture(user.getPicture());
        redisUser.setDate(System.currentTimeMillis());
        String jsonUser = JSON.toJSONString(redisUser);
        LogUtils.commonLog("create a program"+jsonUser);
//        stringRedisTemplate.boundListOps(Constants.LIVE_ONLINE_USER_LIST_KEY+roomId).leftPush(ObjectUtils.toString(user.getUserId()));
        jedisDAO.getJedisWriteTemplate().set(Constants.LIVE_ONLINE_LIKE_KEY+roomId,"0");
        jedisDAO.getJedisWriteTemplate().set(Constants.LIVE_ONLINE_COMMENT_KEY+roomId,"0");
    }


    @Getter
    @Setter
    class Room {
        private String roomId;
        private String userId;
        private String state;
    }

    @Getter
    @Setter
    class Chat {
        private String roomId;
        private String userId;
        private String message;
    }
}
