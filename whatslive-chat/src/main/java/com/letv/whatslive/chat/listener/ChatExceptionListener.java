package com.letv.whatslive.chat.listener;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.ExceptionListenerAdapter;
import com.letv.whatslive.chat.server.ChatServer;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zoran on 15-8-7.
 */
@Component
public class ChatExceptionListener extends ExceptionListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger("error");

    @Autowired
    private ChatServer server;

    @Override
    public void onEventException(Exception e, List<Object> args, SocketIOClient client) {
        logError(e,"onEvent",client);
    }

    @Override
    public void onDisconnectException(Exception e, SocketIOClient client) {
        final String uid = client.getHandshakeData().getSingleUrlParam("uid");
        final String roomId = client.getHandshakeData().getSingleUrlParam("roomId");
        final String from = client.getHandshakeData().getSingleUrlParam("from");
        final String sessionId = client.getSessionId().toString();
        server.getUserCache().remove(uid);
        server.exitRoom(uid, roomId, from, sessionId);
        logError(e,"onDisconnect",client);
    }

    @Override
    public void onConnectException(Exception e, SocketIOClient client) {
        final String uid = client.getHandshakeData().getSingleUrlParam("uid");
        final String roomId = client.getHandshakeData().getSingleUrlParam("roomId");
        final String from = client.getHandshakeData().getSingleUrlParam("from");
        final String sessionId = client.getSessionId().toString();
        server.getUserCache().remove(uid);
        server.exitRoom(uid, roomId, from, sessionId);
        logError(e,"onConnect",client);
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
        logger.error(e.getMessage(), e);
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
