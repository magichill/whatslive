package com.letv.whatslive.chat.server;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import com.corundumstudio.socketio.protocol.JacksonJsonSupport;
import com.google.common.collect.Maps;
import com.letv.whatslive.chat.listener.ChatEventListener;
import com.letv.whatslive.chat.listener.ChatExceptionListener;
import com.letv.whatslive.chat.listener.UserConnectListener;
import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.common.utils.ForbiddenWordUtils;
import com.letv.whatslive.model.redis.chat.protocol.ChatEvent;
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
import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by zoran on 15-7-16.
 */
@Component
@Lazy(value = true)
public class ChatServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatServer.class);

    private Configuration config;

    private SocketIOServer server;

    public static String MEDIA_URL= "http://media.lehi.letv.com/audio.do?voiceId=";

    private static ConcurrentMap<String, UUID> userCache = Maps.newConcurrentMap();

    @Autowired
    private ChatEventListener chatEventListener;

    @Autowired
    private UserConnectListener userConnectListener;

    @Autowired
    private JedisDAO jedisDAO;

    @Autowired
    private ChatExceptionListener chatExceptionListener;

    private static ApplicationContext ctx;

    @PostConstruct
    public void init() {
        try {
            Properties pros = PropertiesLoaderUtils.loadAllProperties("server.properties");
            config = new Configuration();
            SocketConfig sockConfig = new SocketConfig();
            sockConfig.setReuseAddress(true);
            config.setSocketConfig(sockConfig);
            config.setJsonSupport(new JacksonJsonSupport());
            if(chatExceptionListener!=null){
                config.setExceptionListener(chatExceptionListener);
            }
            config.getJsonSupport()
                .addEventMapping("", Constants.DEFAULT_CHAT_EVENT, ChatEvent.class);
            String hostname = pros.getProperty("hostname");
            String port = pros.getProperty("server.port");
            String workerThreads = pros.getProperty("server.workerThreads");
            String maxFramePayloadLength = pros.getProperty("server.maxFramePayloadLength");
            String maxHttpContentLength = pros.getProperty("server.maxHttpContentLength");
            String forbiddenWordFetchURL = pros.getProperty("forbiddenWordFetchURL");
            String reloadInterval = pros.getProperty("reloadInterval");
            MEDIA_URL = pros.getProperty("media.url");

            config.setPingInterval(10 * 1000);
            config.setPingTimeout(10*1000);
            config.setTransports(Transport.WEBSOCKET);

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

            if (StringUtils.isNotBlank(forbiddenWordFetchURL)) {
                ForbiddenWordUtils.setForbiddenWordFetchURL(forbiddenWordFetchURL);
                if (StringUtils.isNotBlank(reloadInterval)) {
                    ForbiddenWordUtils.setReloadInterval(Integer.parseInt(reloadInterval));
                }
                ForbiddenWordUtils.initRemoteFetch();
            }

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
                server.addEventListener(Constants.DEFAULT_CHAT_EVENT, ChatEvent.class,
                    chatEventListener);
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
            }
        }
        server.start();
    }


    public void exitRoom(final String uid, final String roomId, final String from, final String sessionId) {
        try{
            if (StringUtils.isNotBlank(uid) && StringUtils.isNotBlank(roomId) && StringUtils
                .isNotBlank(from)) {
                //通过redis判断客户端是否存在连到不同sever上的情况
                if(sessionId.equals(jedisDAO.getJedisReadTemplate().get(roomId + "_" + uid))){
                    //主播断线
                    if (from.equals("1")) {
                        LOGGER.info("Ready to close live ! " + uid + " " + sessionId + " " + roomId + " "  + from + " " + sessionId);
                        jedisDAO.getJedisWriteTemplate().setex(Constat.LIVER_BREAK_KEY + roomId,
                                String.valueOf(System.currentTimeMillis()),600);
                        final long delayTime = 10*1000;//延迟10秒再关闭直播
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            public void run() {
                                try{
                                    LiveOperate liveOperate = (LiveOperate)ctx.getBean("liveOperate");
                                    liveOperate.endLive(uid, roomId, from, sessionId, delayTime);
                                }catch (Exception e){
                                    LOGGER.error("live user exit chat room failure ! uid=" + uid  + " " + sessionId + " roomId=" + roomId + " from=" +from+ " ", e);
                                }
                            }
                        }, delayTime);
                    }else{
                        jedisDAO.getJedisWriteTemplate().del(roomId+"_"+uid);
                        ChatEvent event = ChatEvent.createChatEvent(uid, roomId, 5);
                        jedisDAO.getJedisWriteTemplate().lremAll(Constants.LIVE_ONLINE_USER_LIST_KEY + roomId,uid);
                        jedisDAO.getJedisWriteTemplate().lremAll(Constants.LIVE_ONLINE_REAL_USER_LIST_KEY + roomId,uid);
                        jedisDAO.getJedisWriteTemplate().publish("chat",event.toString());
                    }
                }else{
                    LOGGER.info("user have connect to another server ! uid=" + uid + " roomId=" + roomId + " from=" + from+ " " + sessionId);
                }

            }
        }catch(Exception e){
            LOGGER.error("user exit chat room failure ! uid=" + uid  + " roomId=" + roomId + " from=" +from+ " " + sessionId, e);

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
