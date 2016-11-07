package com.letv.whatslive.push.server;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import com.corundumstudio.socketio.protocol.JacksonJsonSupport;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.common.utils.ForbiddenWordUtils;
import com.letv.whatslive.mongo.dao.ProgramDAO;
import com.letv.whatslive.mongo.dao.UserDAO;
import com.letv.whatslive.redis.JedisDAO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by zoran on 15-8-18.
 */

@Component
@Lazy(value = true)
public class PushServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PushServer.class);

    private Configuration config;

    private SocketIOServer server;

    private static ConcurrentMap<String, UUID> userCache = Maps.newConcurrentMap();

    @Autowired
    private UserConnectListener userConnectListener;

    @Autowired
    private PushMessageExceptionListener pushMessageExceptionListener;

    @Autowired
    private JedisDAO jedisDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ProgramDAO programDAO;


    private ApplicationContext ctx;

    @PostConstruct
    public void init() {
        try {
            Properties pros = PropertiesLoaderUtils.loadAllProperties("server.properties");
            config = new Configuration();
            SocketConfig sockConfig = new SocketConfig();
            sockConfig.setReuseAddress(true);
            config.setSocketConfig(sockConfig);

            String hostname = pros.getProperty("hostname");
            String port = pros.getProperty("server.port");
            String workerThreads = pros.getProperty("server.workerThreads");
            String maxFramePayloadLength = pros.getProperty("server.maxFramePayloadLength");
            String maxHttpContentLength = pros.getProperty("server.maxHttpContentLength");
            String pingInterval = pros.getProperty("server.pingInterval");
            String pingTimeout = pros.getProperty("server.pingTimeout");
            config.setPingInterval(StringUtils.isBlank(pingInterval)?3*60*1000:Integer.valueOf(pingInterval));
            config.setPingTimeout(StringUtils.isBlank(pingTimeout) ? 60*1000 : Integer.valueOf(pingTimeout));
            config.setTransports(Transport.WEBSOCKET);
            if(pushMessageExceptionListener!=null){
                config.setExceptionListener(pushMessageExceptionListener);
            }
            if (StringUtils.isBlank(hostname)) {
            } else {
                config.setHostname(hostname);
            }

            if (StringUtils.isBlank(port)) {
                config.setPort(9093);
            } else {
                config.setPort(Integer.parseInt(port));
            }

            if (StringUtils.isBlank(workerThreads)) {
                config.setWorkerThreads(50);
            } else {
                config.setWorkerThreads(Integer.parseInt(workerThreads));

            }

            if (StringUtils.isBlank(maxFramePayloadLength)) {
                config.setMaxFramePayloadLength(64000);
            } else {
                config.setMaxFramePayloadLength(Integer.parseInt(maxFramePayloadLength));
            }

            if (StringUtils.isBlank(maxHttpContentLength)) {
                config.setMaxHttpContentLength(64000);
            } else {
                config.setMaxHttpContentLength(Integer.parseInt(maxHttpContentLength));
            }
            server = new SocketIOServer(config);

            startServer();
            //clear user cache
        } catch (Exception e) {
            LOGGER.error("ChatServer init error! Server exit...." + e.getMessage());
            System.exit(0);
        }
    }

    public void startServer() throws Exception {
        if (server != null) {
            try {

                server.addDisconnectListener(userConnectListener);
                server.addConnectListener(userConnectListener);
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        LOGGER.info("Stopping Server .... " + userCache.size() + " users online !");
                        //logger.info(MessageService.ip,"[netty server]","token cached size: {}",messageService.channelIDCache.size());
                        server.stop();
                        SpringApplication.exit(ctx);
                    }
                });


            } catch (Exception e) {
                LOGGER.error("Chat Server startServer failure ! " + e.getMessage());
                e.printStackTrace();
            }
        }
        server.start();
    }

    public void disconnectUser(String uid, String token, UUID sessionId){
        if(userCache.containsKey(token) && userCache.get(token).equals(sessionId)){
            userCache.remove(token);
            jedisDAO.getJedisWriteTemplate().set(Constants.LIVE_PUSH_ONLINE_USER_KEY + uid, "0");
        }

    }



    public Map<String, UUID> getUserCache() {

        return Collections.synchronizedMap(userCache);
    }

    public SocketIOServer getServer() {
        return server;
    }

    public void setCtx(ApplicationContext ctx) {
        this.ctx = ctx;
    }
}
