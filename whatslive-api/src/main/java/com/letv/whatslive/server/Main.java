package com.letv.whatslive.server;

import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.common.utils.PropertiesGetter;
import com.letv.whatslive.server.mq.RedisQueue;
import me.zhuoran.amoeba.netty.server.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;


@Configuration
public class Main extends HttpServer{

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    private static final int DEFAULT_PORT = 8001;

	public static int port = DEFAULT_PORT;

    public static void main(String[] args) throws Exception {

        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
            // port = StringUtils.toInt(args[0], DEFAULT_PORT);
        }

		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

//        String[] beanNames = ctx.getBeanDefinitionNames();
//        Arrays.sort(beanNames);
        SpringApplication.run(Main.class, args);

        RedisQueue<String> redisQueue = (RedisQueue)ctx.getBean("redisQueue");

        int eventLoopThreads = ObjectUtils.toInteger(PropertiesGetter.getValue("server.eventLoopThreads"), 200);     // ;

        int maxContentLength = ObjectUtils.toInteger(PropertiesGetter.getValue("server.maxContentLength"),65535);

        logger.info("Server already startting on " + port + " eventLoopThreads=" + eventLoopThreads + " maxContentLength="+ maxContentLength);
        new HttpServer(port,ctx).run(eventLoopThreads,maxContentLength);

	}

}
