package com.letv.whatslive.push;

/**
 * Created by zoran on 15-7-8.
 */


import com.corundumstudio.socketio.SocketIOServer;
import com.letv.whatslive.push.server.PushServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;


@Configuration
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) throws  Exception {
        final SocketIOServer server ;
        try{
            ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
            PushServer pushServer =  ctx.getBean("pushServer",PushServer.class);
            pushServer.setCtx(ctx);
            SpringApplication.run(Main.class, args);


        }catch(Exception e){
            logger.error("Push Server error! exit .... "  + e.getMessage());
            System.exit(-1);
        }


    }

}
