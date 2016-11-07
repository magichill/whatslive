package com.letv.whatslive.server.mq;

import java.util.Timer;

/**
 * Created by gaoshan on 15-9-6.
 */
public interface RedisQueueListener<T> {

    public static Timer timer = new Timer();
    public void onMessage(T value);
}
