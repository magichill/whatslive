package com.letv.whatslive.server.controller;

import com.alibaba.fastjson.JSON;
import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.server.util.LogUtils;
import com.letv.whatslive.server.util.ShareEvent;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import weibo4j.Timeline;
import weibo4j.model.Status;
import weibo4j.model.WeiboException;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by gaoshan on 15-9-1.
 *
 */
@Deprecated
public class ShareMessageListener  implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] bytes) {
        try {

            if (message != null && message.getBody().length > 0) {
                ShareEvent event = JSON.parseObject(message.getBody(), ShareEvent.class);
                LogUtils.commonLog("Received ShareEvent <" + event.toLogString() + ">");
                final String accessToken = event.getAccessToken();
                final String shareText = event.getShareText();

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    public void run() {
                        Timeline tm = new Timeline(accessToken);
                        try {
                            Status status = tm.updateStatus(shareText);
                            LogUtils.commonLog("Successfully upload the status to ["+status.getText()+"].");
                        } catch (WeiboException e) {
                            LogUtils.logError("fail to share weibo,[exception] "+e.getMessage());
                        }
                    }
                }, 3000 * 5);

            }
        } catch (Throwable e) {
            LogUtils.logError("<" + message.toString() + "> " + e.getMessage(), e);
        }
    }
}
