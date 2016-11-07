package com.letv.whatslive.server.mq;

import com.alibaba.fastjson.JSON;
import com.letv.whatslive.server.util.LogUtils;
import com.letv.whatslive.server.util.ShareEvent;
import weibo4j.Timeline;
import weibo4j.model.Status;
import weibo4j.model.WeiboException;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gaoshan on 15-9-6.
 */
public class QueueListener<String> implements RedisQueueListener<String> {

    @Override
    public void onMessage(String value) {
        try {

            if (value != null ) {
                ShareEvent event = JSON.parseObject(value.toString(), ShareEvent.class);
                LogUtils.commonLog("Received ShareEvent <" + event.toLogString() + ">");
                final java.lang.String accessToken = event.getAccessToken();
                final java.lang.String shareText = event.getShareText();

//                Timer timer = new Timer();
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
            LogUtils.logError("<" + value.toString() + "> " + e.getMessage(), e);
        }
    }

}
