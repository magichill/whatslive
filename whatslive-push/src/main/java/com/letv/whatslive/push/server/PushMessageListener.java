package com.letv.whatslive.push.server;

import com.alibaba.fastjson.JSON;
import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.model.redis.chat.protocol.ChatEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by zoran on 15-7-13.
 */
@Component
public class PushMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PushMessageListener.class);

    @Autowired
    private PushServer server;

    @Override
    public void onMessage(Message message, byte[] bytes) {
        try {
            if (message != null && message.getBody().length > 0) {
                Map map =  JSON.parseObject(message.getBody(),  HashMap.class);
                if(map.get("token")!=null){
                    String token = (String)map.get("token");
                    String msg = (String)map.get("message");
                    UUID uuid = server.getUserCache().get(token);
                    if(uuid!=null){
                        LOGGER.info("PushMessageListener onMessage send to "+ token + " <" + msg + ">");
                        server.getServer().getClient(uuid).sendEvent(Constants.DEFAULT_PUSH_EVENT,msg);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("PushMessageListener onMessage error! " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

}
