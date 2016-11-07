package com.letv.whatslive.inner.push.mq;

import com.alibaba.fastjson.JSON;
import com.letv.whatslive.inner.push.service.PushService;
import com.letv.whatslive.inner.utils.String.StringUtils;
import com.letv.whatslive.model.redis.push.PushEvent;
import com.letv.whatslive.redis.JedisDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by wangjian7 on 2015/8/31.
 */




@Component
public class PushMessageListener implements MessageListener {

    private final Logger logger = LoggerFactory.getLogger(PushMessageListener.class);

    @Autowired
    PushService pushService;

    @Autowired
    JedisDAO jedisDAO;

    Timer timer = new Timer();

    private String delaySencondConf = "PUSH_LIVE_DELAY_SECOND";

    public void onMessage(Message message, byte[] bytes) {
        String uuid = String.valueOf(UUID.randomUUID());
        logger.info("PushMessageListener Received message <" + message
                + "> " + uuid);
        try {
            if (message != null && message.getBody().length > 0) {
                PushEvent event = JSON.parseObject(message.toString(), PushEvent.class);
                if (event.getAction()==1) {
                    if(event.getProgramId() != null){
                        String delayTime = jedisDAO.getJedisReadTemplate().get(delaySencondConf);
                        int delaySeconds = StringUtils.isBlank(delayTime)?10:Integer.valueOf(delayTime);
                        DelayTask delayTask = new DelayTask(pushService, uuid, event.getProgramId());
                        timer.schedule(delayTask, delaySeconds *1000);
                    }else{
                        logger.error("PushMessageListener Received push message request from whatslive-api <" + event.toLogString()
                                + "> programId  is null "+ uuid);
                        return;
                    }

                }else{
                    logger.warn("PushMessageListener Received unknow request <" + event.toLogString()
                            + "> "+ uuid);
                }
            }
        } catch (Throwable e) {
            logger.error("PushMessageListener Received error <"+ message.toString()+"> "+ uuid,e);
        }
    }
    class DelayTask extends TimerTask {
        private final Logger logger = LoggerFactory.getLogger(DelayTask.class);
        private PushService pushService;
        private String uuid;
        private Long porgramId;
        DelayTask(PushService pushService, String uuid, Long porgramId){
            this.pushService = pushService;
            this.uuid = uuid;
            this.porgramId = porgramId;
        }

        @Override
        public void run() {
            logger.info("PushMessageListener push live message start porgramId=" + porgramId
                    + " " + uuid);
            try {
                pushService.pushLiveMessage(uuid, porgramId);
                logger.info("PushMessageListener start push live message end porgramId=" + porgramId
                        + " " + uuid);
            } catch (Throwable e) {
                logger.error("PushMessageListener push live message error porgramId=" + porgramId
                        + " " + uuid, e);
            }

        }
    }
}
