package com.letv.whatslive.chat.mq;

import com.alibaba.fastjson.JSON;
import com.letv.whatslive.chat.server.ChatServer;
import com.letv.whatslive.chat.server.Constat;
import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.model.redis.chat.protocol.ChatEvent;
import com.letv.whatslive.mongo.dao.ProgramDAO;
import com.letv.whatslive.mongo.dao.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zoran on 15-7-13.
 */

@Component
public class ChatRoomMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatRoomMessageListener.class);

    @Autowired
    private ChatServer server;

    @Autowired
    private ProgramDAO programDAO;

    @Autowired
    private UserDAO userDAO;


    @Override
    public void onMessage(Message message, byte[] bytes) {
        try {
            if (message != null && message.getBody().length > 0) {

                final ChatEvent event = JSON.parseObject(message.getBody(), ChatEvent.class);
                LOGGER.info("Received System ChatEvent <" + event.toLogString() + ">");
                final String roomId = event.getRoomId();
                String userId = event.getUid();

                if (StringUtils.isEmpty(roomId) || StringUtils.isEmpty(userId)) {
                    LOGGER.error("Received room close message from inner <" + event.toLogString()
                        + "> roomId or uid is null");
                    return;
                }

                if (event.getAction() == 3 || event.getAction() == 7) {
                    int clientSize =
                        server.getServer().getRoomOperations(roomId).getClients().size();
                    LOGGER.info("Ready to close " + roomId + " size " + String.valueOf(clientSize));

                    //关闭房间并广播通知其他用户
                    server.getServer().getRoomOperations(roomId).sendEvent(Constants.DEFAULT_CHAT_EVENT, event);


                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        public void run() {
                            ChatEvent ce = ChatEvent.createChatEvent(event.getUid(),event.getRoomId(),7);
                            LOGGER.info("Broadcast to client closed room message <" +  ce.toLogString() + ">" );
                            server.getServer().getRoomOperations(roomId).sendEvent(Constants.DEFAULT_CHAT_EVENT, ce);
                        }
                    }, 60000 * 5);
                }
            }
        } catch (Throwable e) {
            LOGGER.error("<"+ message.toString()+"> " + e.getMessage(),e);
        }
    }



}
