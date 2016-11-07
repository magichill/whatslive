package com.letv.whatslive.chat.listener;

import com.alibaba.fastjson.JSON;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.letv.whatslive.chat.server.ChatServer;
import com.letv.whatslive.chat.server.Constat;
import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.model.User;
import com.letv.whatslive.model.redis.chat.protocol.ChatEvent;
import com.letv.whatslive.model.redis.chat.protocol.LoginEvent;
import com.letv.whatslive.mongo.dao.UserDAO;
import com.letv.whatslive.redis.JedisDAO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.util.UUID;

/**
 * Created by zoran on 15-7-16.
 */
@Service
public class UserConnectListener implements ConnectListener, DisconnectListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserConnectListener.class);

    @Autowired
    private ChatServer server;

    @Autowired
    private JedisDAO jedisDAO;

    @Autowired
    private UserDAO userDAO;

    @Override
    public void onConnect(SocketIOClient client) {
        String uid = "";
        String roomId = "";
        String picture = "";
        String nickName = "";
        String from = "";
        try {
            uid = client.getHandshakeData().getSingleUrlParam("uid");
            roomId = client.getHandshakeData().getSingleUrlParam("roomId");
            picture = client.getHandshakeData().getSingleUrlParam("picture");
            nickName = client.getHandshakeData().getSingleUrlParam("nickName");
            from = client.getHandshakeData().getSingleUrlParam("from");

            if(StringUtils.isNotBlank(picture)){
                picture = URLDecoder.decode(picture,"UTF-8");
            }
            if (authenticateUser(uid, roomId) && StringUtils.isNotBlank(from)) {
                LOGGER.info(
                        "Connected " + client.getSessionId() + " " + client.getRemoteAddress() + " "
                                + uid + " " + roomId + " " + from + " " + nickName + " " + picture);
                UUID clientId = server.getUserCache().get(uid);
                try{
                    LOGGER.info("Connected real IP:" + client.getHandshakeData().getHeaders().get("X-Forwarded-For")
                            + client.getSessionId() + " " + uid + " " + roomId);
                }catch(Exception e){
                    LOGGER.error("Connected real IP get error!" + client.getSessionId() + " " + uid + " " + roomId, e);
                }
                if (clientId != null) {
                    SocketIOClient lastClient = server.getServer().getClient(clientId);

                    LOGGER.info( "Connected lastClient status " + uid + " " + roomId + " " + lastClient.isChannelOpen() +
                            " sessionId:" + lastClient.getSessionId());
                    if(lastClient!=null && lastClient.isChannelOpen()){
                        LOGGER.warn("Connected refuse " + client.getSessionId() + " " + client
                                .getRemoteAddress()+ " " + uid + " " + roomId + " duplicate entries");
                        client.disconnect();
                        return;
                    }
                }

                client.joinRoom(roomId);
                server.getUserCache().put(uid, client.getSessionId());
                jedisDAO.getJedisWriteTemplate().setex(roomId+"_"+uid, client.getSessionId().toString(), 60*60*24);

                if(StringUtils.isNotBlank(from) && from.equals("1")){
                    jedisDAO.getJedisWriteTemplate().del(Constat.LIVER_BREAK_KEY + roomId);
                }else{
                    LoginEvent loginEvent = new LoginEvent(uid, picture, nickName, 0L);
                    ChatEvent chatEvent = ChatEvent.createChatEvent(uid, roomId, 4, picture, nickName);
                    //非主播用户执行以下的操作
                    jedisDAO.getJedisWriteTemplate().lremAll(Constants.LIVE_ONLINE_USER_LIST_KEY + roomId,uid);
                    jedisDAO.getJedisWriteTemplate().lpush(Constants.LIVE_ONLINE_USER_LIST_KEY + roomId,uid);
                    jedisDAO.getJedisWriteTemplate().sadd(Constants.LIVE_ONLINE_TOTALUSER_KEY+roomId,JSON.toJSONString(loginEvent));
                    jedisDAO.getJedisWriteTemplate().publish("chat",chatEvent.toString());

                    jedisDAO.getJedisWriteTemplate().lremAll(Constants.LIVE_ONLINE_REAL_USER_LIST_KEY + roomId,uid);
                    jedisDAO.getJedisWriteTemplate().lpush(Constants.LIVE_ONLINE_REAL_USER_LIST_KEY + roomId,uid);
                    jedisDAO.getJedisWriteTemplate().sadd(Constants.LIVE_ONLINE_REAL_TOTALUSER_KEY + roomId, JSON.toJSONString(loginEvent));
                }

            } else {
                LOGGER.warn(
                        "Connected Refuse Unauthorized User " + uid + " " + roomId + " " + client.getSessionId()
                                + " " + client.getRemoteAddress());
                client.disconnect();
            }
        } catch (Exception e) {
            LOGGER.error("Connected refuse " + uid + " " + roomId + " " + client.getSessionId()
                    + " " + client.getRemoteAddress() + " " + e.getMessage(),e);
        }

    }

    private boolean authenticateUser(String uid, String roomId) {
        //TODO 直播状态的room 才能通过认证 否则拒绝连接
        if (StringUtils.isBlank(uid) || StringUtils.isBlank(roomId)) {
            return false;
        }else{
            User user = userDAO.getUserById(Long.parseLong(uid));
            if (user == null) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onDisconnect(SocketIOClient client) {
        final String uid = client.getHandshakeData().getSingleUrlParam("uid");
        final String roomId = client.getHandshakeData().getSingleUrlParam("roomId");
        final String from = client.getHandshakeData().getSingleUrlParam("from");
        final String nickName = client.getHandshakeData().getSingleUrlParam("nickName");
        final String sessionId = client.getSessionId().toString();
        try {
            LOGGER.info("Disconnect " + uid  + " " + nickName + " " + roomId +  " " + from + " " + sessionId
                    + " " + client.getRemoteAddress());
            if(server.getUserCache().containsKey(uid)&&client.getSessionId().equals(server.getUserCache().get(uid))){
                LOGGER.info("Disconnect remove user from cache " + uid  + " " + nickName + " " + roomId +  " " + from
                        + " " + sessionId + " " + client.getRemoteAddress());
                server.getUserCache().remove(uid);
                server.exitRoom(uid, roomId, from, sessionId);
            }else{
                LOGGER.info("Disconnect cache have no data or sessionId not equal ! " + uid  + " " + nickName + " " + roomId +  " " + from
                        + " " + sessionId + " " + client.getRemoteAddress()+ " cache sessionId:" + server.getUserCache().get(uid));
            }

        } catch (Exception e) {
            LOGGER.error("Disconnect exception" + uid  + " " + nickName + " " + roomId + " " + sessionId + " " + client
                    .getRemoteAddress() + " " + e.getMessage(),e);
        }

    }
}
