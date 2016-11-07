package com.letv.whatslive.push.server;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.ExceptionListenerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zoran on 15-8-7.
 */
@Component
public class PushMessageExceptionListener extends ExceptionListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger(PushMessageExceptionListener.class);

    @Autowired
    private PushServer pushServer;

    @Override
    public void onEventException(Exception e, List<Object> args, SocketIOClient client) {
        logError(e,"onEvent",client);
    }

    @Override
    public void onDisconnectException(Exception e, SocketIOClient client) {
        final String uid = client.getHandshakeData().getSingleUrlParam("uid");
        final String token = client.getHandshakeData().getSingleUrlParam("deviceToken");
        try {
            if (StringUtils.isNotBlank(token) && StringUtils.isNotBlank(uid)) {
                logger.info("onDisconnectException " + uid + " " + token + " " + client.getSessionId() + " "
                        + client.getRemoteAddress());
                pushServer.disconnectUser(uid, token, client.getSessionId());
            }

        } catch (Exception e1) {
            logError(e1,
                    "onDisconnectException " + uid + " " + token + " " + client.getSessionId() + " " + client
                            .getRemoteAddress() + " "
                            + e.getMessage(), client);
        }
        logError(e,"onDisconnectException",client);
    }

    @Override
    public void onConnectException(Exception e, SocketIOClient client) {
        final String uid = client.getHandshakeData().getSingleUrlParam("uid");
        final String token = client.getHandshakeData().getSingleUrlParam("deviceToken");
        try {
            if (StringUtils.isNotBlank(token) && StringUtils.isNotBlank(uid)) {
                logger.info("onConnectException " + uid + " " + token + " " + client.getSessionId() + " "
                        + client.getRemoteAddress());
                pushServer.disconnectUser(uid, token, client.getSessionId());
            }

        } catch (Exception e1) {
            logError(e1,
                    "onConnectException " + uid + " " + token + " " + client.getSessionId() + " " + client
                            .getRemoteAddress() + " "
                            + e.getMessage(), client);
        }
        logError(e,"onConnectException",client);
    }

    @Override
    public void onMessageException(Exception e, String data, SocketIOClient client) {
        logError(e,"onMessage",client);
    }

    @Override
    public void onJsonException(Exception e, Object data, SocketIOClient client) {
        logError(e,"onJson",client);
    }

    public boolean exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        logger.error("exceptionCaught "+e.getMessage(), e);
        return true;
    }


    private void logError(Exception e, String event,SocketIOClient client){
        final String uid = client.getHandshakeData().getSingleUrlParam("uid");
        final String roomId = client.getHandshakeData().getSingleUrlParam("roomId");
        final String from = client.getHandshakeData().getSingleUrlParam("from");
        final String nickName = client.getHandshakeData().getSingleUrlParam("nickName");

        StringBuilder s = new StringBuilder();
        s.append(getBlock(event  + " exception")).append(" ");
        s.append(uid).append(" ").append(roomId).append(" ").append(from).append(" ").append(nickName).append(" ");
        s.append(":");
        s.append(getException(e));
        logger.error(s.toString(),e);


    }

    private static String getBlock(Object msg) {
        if (msg == null) {
            msg = "";
        }
        return "[" + msg.toString() + "]";
    }

    private static String getException(Throwable e){
        StackTraceElement[] ste = e.getStackTrace();
        StringBuffer sb = new StringBuffer();
        sb.append(e.getMessage() + " ");
        for (int i = 0; i < ste.length; i++) {
            sb.append(ste[i].toString() + "\n");
        }
        return sb.toString();
    }


}
