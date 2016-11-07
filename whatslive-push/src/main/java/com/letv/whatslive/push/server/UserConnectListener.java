package com.letv.whatslive.push.server;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.model.User;
import com.letv.whatslive.mongo.dao.UserDAO;
import com.letv.whatslive.redis.JedisDAO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Created by zoran on 15-7-16.
 */
@Service
public class UserConnectListener implements ConnectListener, DisconnectListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserConnectListener.class);

    @Autowired
    private JedisDAO jedisDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PushServer pushServer;


    @Override
    public void onConnect(SocketIOClient client) {
        String uid = "";
        String token = "";
        try {
            uid = client.getHandshakeData().getSingleUrlParam("uid");
            token = client.getHandshakeData().getSingleUrlParam("deviceToken");

            if (authenticateUser(uid, token)) {
                LOGGER.info(
                    "Connected " + client.getSessionId() + " " + client
                        .getRemoteAddress()
                        + " "
                        + uid + " " + token);

                UUID clientId = pushServer.getUserCache().get(token);

                if (clientId != null) {
                    SocketIOClient lastClient = pushServer.getServer().getClient(clientId);
                    if (lastClient != null && lastClient.isChannelOpen()) {
                        LOGGER.warn("Connected again " + client.getSessionId() + " " + client
                            .getRemoteAddress() + " " + uid + " " + token);
//                        client.disconnect();
//                        return;
                        lastClient.disconnect();
                    }else if(lastClient != null && !lastClient.isChannelOpen()){
                        lastClient.disconnect();
                    }
                }
                pushServer.getUserCache().put(token, client.getSessionId());
                jedisDAO.getJedisWriteTemplate().set(Constants.LIVE_PUSH_ONLINE_USER_KEY + uid,
                    String.valueOf(System.currentTimeMillis()));
                LOGGER.info("Connected sucess! " + client.getSessionId() + " " + client
                        .getRemoteAddress() + " " + uid + " " + token);
            } else {
                LOGGER.warn(
                        "Connected Refuse Unauthorized User " + uid + " " + token + " " + client
                                .getSessionId()
                                + " " + client.getRemoteAddress());
                client.disconnect();
            }
        } catch (Exception e) {
            LOGGER.error(
                "Connected refuse " + uid + " " + token + " " + client.getSessionId() + " "
                    + client.getRemoteAddress() + " " + e.getMessage(), e);
        }

    }

    private boolean authenticateUser(String uid, String token) {
        if (StringUtils.isBlank(uid) || StringUtils.isBlank(token)) {
            return false;
        } else {
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
        final String token = client.getHandshakeData().getSingleUrlParam("deviceToken");
        try {
            if (StringUtils.isNotBlank(token) && StringUtils.isNotBlank(uid)) {
                LOGGER.info("Disconnect " + uid + " " + token + " " + client.getSessionId() + " "
                    + client.getRemoteAddress());
                pushServer.disconnectUser(uid, token, client.getSessionId());
            }

        } catch (Exception e) {
            LOGGER.error(
                "Disconnect " + uid + " " + token + " " + client.getSessionId() + " " + client
                    .getRemoteAddress() + " "
                    + e.getMessage(), e);
            e.printStackTrace();
        }

    }
}
