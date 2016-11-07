package com.letv.whatslive.chat.mq;

import com.alibaba.fastjson.JSON;
import com.letv.whatslive.chat.server.ChatServer;
import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.model.redis.chat.protocol.ChatEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Created by zoran on 15-7-13.
 */
@Component
public class ChatMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatMessageListener.class);

    @Autowired
    private ChatServer server;

    @Override
    public void onMessage(Message message, byte[] bytes) {
        try {
            if (message != null && message.getBody().length > 0) {
                ChatEvent data = JSON.parseObject(message.getBody(), ChatEvent.class);
                if(data.getAction()!=2){
                    LOGGER.info("Received ChatEvent from ChatMessageListener onMessage <" + data.toLogString() + ">");
                }
                if (data != null && org.apache.commons.lang3.StringUtils
                    .isNotBlank(data.getRoomId())) {
                    server.getServer().getRoomOperations(data.getRoomId())
                        .sendEvent(Constants.DEFAULT_CHAT_EVENT, data);
                }
            }
        } catch (Exception e) {
            LOGGER.error("ChatMessageListener onMessage error! " + e.getMessage(), e);
        }
    }

}
