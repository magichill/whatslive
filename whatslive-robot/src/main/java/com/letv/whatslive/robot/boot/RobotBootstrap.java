package com.letv.whatslive.robot.boot;

import com.letv.whatslive.model.redis.chat.protocol.ChatEvent;
import com.letv.whatslive.robot.utils.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by wangjian7 on 2015/9/11.
 */
public class RobotBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(RobotBootstrap.class);

    public static void main(String[] args) throws Exception {
        logger.info("---whatslive-robot begin to start---");
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/applicationContext.xml");
        ctx.start();
        SpringUtils.setApplicationContext(ctx);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        logger.info("whatslive-robot is working hard……");
                        Thread.sleep(120 * 1000L);
                    } catch (Exception e) {
                        logger.error("thread sleep error", e);
                    }
                }
            }
        }, "whatslive-robot");
        thread.start();
        logger.info("---whatslive-robot start success---");
    }
}
