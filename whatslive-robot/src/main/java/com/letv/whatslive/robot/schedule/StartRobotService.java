package com.letv.whatslive.robot.schedule;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.model.ActionLog;
import com.letv.whatslive.model.Program;
import com.letv.whatslive.model.RobotRule;
import com.letv.whatslive.model.User;
import com.letv.whatslive.model.convert.ActionLogConvert;
import com.letv.whatslive.model.redis.chat.protocol.ChatEvent;
import com.letv.whatslive.model.redis.chat.protocol.LoginEvent;
import com.letv.whatslive.mongo.dao.ActionLogDAO;
import com.letv.whatslive.mongo.dao.ProgramDAO;
import com.letv.whatslive.mongo.dao.UserDAO;
import com.letv.whatslive.mongo.dao.VirtualCommentDAO;
import com.letv.whatslive.redis.JedisDAO;
import com.letv.whatslive.robot.constants.RobotConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wangjian7 on 2015/9/11.
 */
@Component
public class StartRobotService {

    private static final Logger logger = LoggerFactory.getLogger(StartRobotService.class);

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    private static final Map<String, User> virtualUserList = new HashMap<String, User>();

    @Autowired
    ProgramDAO programDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    VirtualCommentDAO virtualCommentDAO;

    @Autowired
    private ActionLogDAO actionLogDAO;

    @Autowired
    JedisDAO jedisDAO;

    public void scanAllLiveToChat(){
        try{
            if(jedisDAO.getJedisReadTemplate().get(RobotConstants.ROBOT_LIVE_SWITCH)==null){
                logger.info("scan live to add robot!");
                RobotRule robotRule = new RobotRule();
                String uuid = String.valueOf(UUID.randomUUID());
                Map<String, Object> params = Maps.newHashMap();
                params.put("pType", 1);
                params.put("status", 1);
                params.put("isCarousel",false);
                List<Long> liveIdList = programDAO.getProgramIdsByParams(params, null);
//                List<Long> liveIdList = new ArrayList<Long>();
//                liveIdList.add(0L);
                for(Long pid: liveIdList){
                    Program program = programDAO.getProgramById(pid);
                    if(jedisDAO.getJedisReadTemplate().get(RobotConstants.ROBOT_LIVE_CHAT + pid)==null){
                        //启动用户加入退出机器人
                        ChatRobotThread chatRobotThread = new ChatRobotThread(uuid, pid.toString(), robotRule);
                        executorService.execute(chatRobotThread);
                        //启动点赞机器人
                        LikeRobotThread likeRobotThread = new LikeRobotThread(uuid, pid.toString(), robotRule);
                        executorService.execute(likeRobotThread);
                        //启动聊天机器人
                        CommentRobotThread commentRobotThread = new CommentRobotThread(uuid, pid.toString(), robotRule);
                        executorService.execute(commentRobotThread);
                    }
                }
            }else{
                logger.info("chat robot is off!");
            }
        }catch(Exception e){
            logger.error(e.getMessage(), e);
        }

    }
    class ChatRobotThread implements Runnable {
        private final Logger logger = LoggerFactory.getLogger(ChatRobotThread.class);
        private String pid;
        private String uuid;
        private RobotRule robotRule;
        private Timer timer;

        ChatRobotThread(String uuid, String pid, RobotRule robotRule) {
            this.uuid = uuid + "_" + pid;
            this.pid = pid;
            this.robotRule = robotRule;
            timer = new Timer();
        }

        @Override
        public void run() {
            //放到缓存中表示已经启动了此直播聊天室的机器人功能
            jedisDAO.getJedisWriteTemplate().setex(RobotConstants.ROBOT_LIVE_CHAT+pid,"1",60*60*24);
            Program program = programDAO.getProgramById(Long.valueOf(pid));
            if(program.getPType()==1){
                logger.info("program create userConnect robot! programId:"+pid);
                UserRobot userRobot = new UserRobot(uuid, pid, robotRule);
                timer.schedule(userRobot, 1000, robotRule.getUserWatchTime()*1000);
                while(true){
                    logger.info("program userConnect robot is running! programId:"+pid);
                    try {
                        Thread.sleep(60*1000);
                        if(jedisDAO.getJedisReadTemplate().get(RobotConstants.ROBOT_LIVE_CHAT+pid)!=null
                                && jedisDAO.getJedisReadTemplate().get(RobotConstants.ROBOT_LIVE_CHAT+pid).equals("2")){
                            if(jedisDAO.getJedisReadTemplate().llen(RobotConstants.ROBOT_LIVE_USER + pid)>0){
                                logger.info("not all program robot is quit,userConnect wait for next time to end! programId:"+pid);
                            }else{
                                logger.info("program userConnect robot will end! programId:"+pid);
                                timer.cancel();
                                break;
                            }
                        }
                    } catch (InterruptedException e) {
                        logger.error("program userConnect robot thread error! programId:" + pid, e);
                        timer.cancel();
                        break;
                    }
                }
            }else {
                logger.info("program can't create robot, program is end! programId:"+pid);
            }
        }
    }

    class UserRobot extends TimerTask{
        private final Logger logger = LoggerFactory.getLogger(UserRobot.class);
        private String pid;
        private String uuid;
        private RobotRule robotRule;
        private int maxUserNum;

        UserRobot(String uuid, String pid, RobotRule robotRule) {
            this.uuid = uuid + "_" + pid;
            this.pid = pid;
            this.robotRule = robotRule;
            Random random = new Random();
            maxUserNum = random.nextInt(robotRule.getUserComeInLimitMax())%(robotRule.getUserComeInLimitMax()
                    -robotRule.getUserComeInLimitMin()+1) + robotRule.getUserComeInLimitMin();
            logger.info("user connect robot max add user num:" + maxUserNum +" programId:" + pid);
        }

        @Override
        public void run() {
            try {
                String flag = jedisDAO.getJedisReadTemplate().get(RobotConstants.ROBOT_LIVE_CHAT+pid);
                if(flag!=null && flag.equals("3")){
                    allUserDisconnect();
                    jedisDAO.getJedisWriteTemplate().setex(RobotConstants.ROBOT_LIVE_CHAT+pid,"2",60*60*24);
                    this.cancel();
                }else{
                    Random random = new Random();
                    Program program = programDAO.getProgramById(Long.valueOf(pid));
                    if(program.getPType()==1){
                        Long totleUserNum = jedisDAO.getJedisReadTemplate().llen(Constants.LIVE_ONLINE_USER_LIST_KEY + pid);
                        int userWatchTime = (robotRule.getUserWatchTime() - 1)*1000;
                        if( totleUserNum < maxUserNum){
                            Map params = Maps.newHashMap();
                            params.put("userType", 3);
                            Long maxUserNum = userDAO.countUserByParams(params);
                            int cameNumberLimit = random.nextInt(robotRule.getUserComeInMaxNum())%(robotRule.getUserComeInMaxNum()
                                    -robotRule.getUserComeInMinNum()+1) + robotRule.getUserComeInMinNum();
                            int start = random.nextInt(Integer.valueOf(maxUserNum.toString())-cameNumberLimit-1);
                            List<User> userList = userDAO.getUserListByParams(params,null,start,cameNumberLimit);

                            List<Integer> sleepTimeList = Lists.newArrayList();
                            sleepTimeList.add(0);
                            for(int i=0; i<(userList.size()+userList.size()/3); i++){
                                sleepTimeList.add(random.nextInt(userWatchTime));
                            }
                            Collections.sort(sleepTimeList);
                            int j = 0;
                            for(int i=0;i<userList.size();i++){
                                //如果用户在观看列表中，先将用户退出
                                if(jedisDAO.getJedisWriteTemplate().lremAll(RobotConstants.ROBOT_LIVE_USER + pid
                                        ,String.valueOf(userList.get(i).getUserId()))){
                                    userDisconnect(String.valueOf(userList.get(i).getUserId()));
                                }
                                if(j+1<sleepTimeList.size()){
                                    Thread.sleep(sleepTimeList.get(j+1)-sleepTimeList.get(j));
                                    j++;
                                }
                                userConnect(String.valueOf(userList.get(i).getUserId()),
                                        userList.get(i).getPicture(), userList.get(i).getNickName());
                                if(totleUserNum>robotRule.getUserOutLimit() && (i+1)%3==0){
                                    if(j+1<sleepTimeList.size()){
                                        Thread.sleep(sleepTimeList.get(j+1)-sleepTimeList.get(j));
                                        j++;
                                    }
                                    int virtualUserNum = random.nextInt(Integer.valueOf(jedisDAO.getJedisReadTemplate()
                                            .llen(RobotConstants.ROBOT_LIVE_USER + pid).toString()));
                                    userDisconnect(jedisDAO.getJedisReadTemplate().lindex(
                                            RobotConstants.ROBOT_LIVE_USER + pid,virtualUserNum));
                                }

                            }
                            logger.info("user connect robot add user success! userList num is:" + userList.size()
                                    +" sleepTimeList num is:" + sleepTimeList.size());
                        }else{
                            if(totleUserNum>robotRule.getUserOutLimit()){
                                int outNumberLimit = random.nextInt(robotRule.getUserOutMaxNum()) % (robotRule.getUserOutMaxNum()
                                        -robotRule.getUserOutMinNum()+1) + robotRule.getUserOutMinNum();
                                for(int i=0;i<outNumberLimit;i++){
                                    Thread.sleep(userWatchTime/outNumberLimit);
                                    int virtualUserNum = random.nextInt(Integer.valueOf(jedisDAO.getJedisReadTemplate()
                                            .llen(RobotConstants.ROBOT_LIVE_USER + pid).toString()));
                                    userDisconnect(jedisDAO.getJedisReadTemplate().lindex(
                                            RobotConstants.ROBOT_LIVE_USER + pid,virtualUserNum));

                                }
                            }
                        }
                    }else{
                        logger.info("user connect robot is end, stop user connect task! programId:" + pid);
                        allUserDisconnect();
                        jedisDAO.getJedisWriteTemplate().setex(RobotConstants.ROBOT_LIVE_CHAT+pid,"2",60*60*10);
                        this.cancel();
                    }
                }
            } catch (Exception e) {
                logger.error("user connect thread run connect error! programId:"+pid, e);
                allUserDisconnect();
                jedisDAO.getJedisWriteTemplate().setex(RobotConstants.ROBOT_LIVE_CHAT+pid,"2",60*60*10);
                this.cancel();

            }
        }

        /**
         * 所有用户退出聊天室
         */
        private void allUserDisconnect(){
            logger.info("user connect remove all virtualUser from room! programId:" + pid);
            int retryNum = 0;
            Random random = new Random();
            while (jedisDAO.getJedisReadTemplate().llen(RobotConstants.ROBOT_LIVE_USER + pid)>0){
                try {
                    int virtualUserNum = random.nextInt(Integer.valueOf(jedisDAO.getJedisReadTemplate()
                            .llen(RobotConstants.ROBOT_LIVE_USER + pid).toString()));
                    Thread.sleep((virtualUserNum%5+1)*500);
                    userDisconnect(jedisDAO.getJedisReadTemplate().lindex(
                            RobotConstants.ROBOT_LIVE_USER + pid,virtualUserNum));
                } catch (Exception e) {
                    logger.error("user connect thread run disconnect error! programId:"+pid, e);
                    retryNum ++;
                    if(retryNum >5 ){
                        logger.error("user connect remove all virtualUser exception for 5 times break! programId:"+pid, e);
                        break;
                    }
                }
            }
        }

        private void userConnect(String uid, String picture, String nickName){
            LoginEvent loginEvent = new LoginEvent(uid, picture, nickName, 0L);
            jedisDAO.getJedisWriteTemplate().lremAll(Constants.LIVE_ONLINE_USER_LIST_KEY + pid,uid);
            jedisDAO.getJedisWriteTemplate().lpush(Constants.LIVE_ONLINE_USER_LIST_KEY + pid,uid);
            jedisDAO.getJedisWriteTemplate().sadd(Constants.LIVE_ONLINE_TOTALUSER_KEY + pid, JSON.toJSONString(loginEvent));
            //以下是虚拟用户存放的缓存位置
            jedisDAO.getJedisWriteTemplate().lremAll(RobotConstants.ROBOT_LIVE_USER + pid,uid);
            jedisDAO.getJedisWriteTemplate().lpush(RobotConstants.ROBOT_LIVE_USER + pid,uid);

            ChatEvent event = ChatEvent.createChatEvent(uid, pid, 4, picture, nickName);
            jedisDAO.getJedisWriteTemplate().publish("chat", event.toString());
            logger.info("user connect to program! programId:" + pid + " userId:" + uid);

        }

        private void userDisconnect(String uid){
            ChatEvent event = ChatEvent.createChatEvent(uid, pid, 5);
            jedisDAO.getJedisWriteTemplate().publish("chat",event.toString());
            jedisDAO.getJedisWriteTemplate().lremAll(Constants.LIVE_ONLINE_USER_LIST_KEY + pid,uid);
            //以下是虚拟用户存放的缓存位置
            jedisDAO.getJedisWriteTemplate().lremAll(RobotConstants.ROBOT_LIVE_USER + pid, uid);
            logger.info("user disconnect from program! programId:" + pid + " userId:" + uid);
        }
    }

    class LikeRobotThread implements Runnable {
        private final Logger logger = LoggerFactory.getLogger(LikeRobotThread.class);
        private String pid;
        private String uuid;
        private RobotRule robotRule;
        private Timer timer;

        LikeRobotThread(String uuid, String pid, RobotRule robotRule) {
            this.uuid = uuid + "_" + pid;
            this.pid = pid;
            this.robotRule = robotRule;
            timer = new Timer();
        }

        @Override
        public void run() {
            Program program = programDAO.getProgramById(Long.valueOf(pid));
            if(program.getPType()==1){
                logger.info("program create userLike robot! programId:"+pid);
                LikeRobot likeRobot = new LikeRobot(uuid, pid, robotRule);
                timer.schedule(likeRobot, 5000, robotRule.getUserLikeTime()*1000);
                while(true){
                    logger.info("program userLike robot is running! programId:"+pid);
                    try {
                        Thread.sleep(60*1000);
                        String flag = jedisDAO.getJedisReadTemplate().get(RobotConstants.ROBOT_LIVE_CHAT+pid);
                        if(flag!=null && flag.equals("2")){
                            logger.info("program userLike robot will end! programId:"+pid);
                            timer.cancel();
                            break;
                        }
                    } catch (InterruptedException e) {
                        logger.error("program userLike robot thread error! programId:" + pid, e);
                        timer.cancel();
                        break;
                    }
                }
            }else {
                logger.info("program can't create userLike robot, program is end! programId:"+pid);
            }
            logger.info("program robot is end! programId:"+pid);
        }
    }

    class LikeRobot extends TimerTask{
        private final Logger logger = LoggerFactory.getLogger(LikeRobot.class);
        private String pid;
        private String uuid;
        private RobotRule robotRule;

        LikeRobot(String uuid, String pid, RobotRule robotRule) {
            this.uuid = uuid + "_" + pid;
            this.pid = pid;
            this.robotRule = robotRule;
        }

        @Override
        public void run() {
            logger.info("user like robot start to send like to program! programId:" + pid);
            try {
                String flag = jedisDAO.getJedisReadTemplate().get(RobotConstants.ROBOT_LIVE_CHAT+pid);
                if(flag!=null && !flag.equals("2")){
                    Random random = new Random();
                    int likeUserNumLimit = random.nextInt(robotRule.getUserLikeMaxNum())%(robotRule.getUserLikeMaxNum()
                            -robotRule.getUserLikeMinNum()+1) + robotRule.getUserLikeMinNum();
                    int userSleepTime = 300;
                    int everyUserLikeTime = ((robotRule.getUserLikeTime()-1)*1000-userSleepTime*likeUserNumLimit)/likeUserNumLimit;
                    for(int i=0; i< likeUserNumLimit; i++){
                        if(jedisDAO.getJedisReadTemplate().llen(RobotConstants.ROBOT_LIVE_USER + pid)>0){
                            int virtualUserNum = random.nextInt(Integer.valueOf(jedisDAO.getJedisReadTemplate()
                                    .llen(RobotConstants.ROBOT_LIVE_USER + pid).toString()));
                            sendLike(jedisDAO.getJedisReadTemplate().lindex(RobotConstants.ROBOT_LIVE_USER + pid,
                                    virtualUserNum),everyUserLikeTime, virtualUserNum%likeUserNumLimit+1);
                        }
                        Thread.sleep(userSleepTime);
                    }
                }else{
                    logger.info("user like robot is end, stop user like task! programId:" + pid);
                    this.cancel();

                }
            } catch (Exception e) {
                logger.error("user like thread run error! programId:"+pid, e);
                this.cancel();
            }
        }

        private void sendLike(String uid, long everyUserLikeTime, int repeat){
            for(int i=0; i<repeat; i++){
                try {
                    Thread.sleep(everyUserLikeTime/repeat);
                    jedisDAO.getJedisWriteTemplate().incr(Constants.LIVE_ONLINE_LIKE_KEY + pid);

                    ChatEvent event = ChatEvent.createChatEvent(uid,pid,2);
                    jedisDAO.getJedisWriteTemplate().publish("chat", event.toString());
                } catch (InterruptedException e) {
                    logger.error("user like robot like error! programId:" + pid + " userId:" + uid + " likeNum:" + repeat);
                }
            }
            logger.info("user like robot send like! programId:" + pid + " userId:" + uid + " likeNum:" + repeat);
        }

    }

    class CommentRobotThread implements Runnable {
        private final Logger logger = LoggerFactory.getLogger(CommentRobotThread.class);
        private String pid;
        private String uuid;
        private RobotRule robotRule;
        private Timer timer;

        CommentRobotThread(String uuid, String pid, RobotRule robotRule) {
            this.uuid = uuid + "_" + pid;
            this.pid = pid;
            this.robotRule = robotRule;
            timer = new Timer();
        }

        @Override
        public void run() {
            Program program = programDAO.getProgramById(Long.valueOf(pid));
            if(program.getPType()==1){
                logger.info("program create userComment robot! programId:"+pid);
                List<String> commentList = virtualCommentDAO.getAllVirtualComment();
                CommentRobot commentRobot = new CommentRobot(uuid, pid, robotRule, commentList);
                timer.schedule(commentRobot, (60+program.getId()%60)*1000, robotRule.getUserCommentTime()*1000);
                while(true){
                    logger.info("program userComment robot is running! programId:"+pid);
                    try {
                        Thread.sleep(60*1000);
                        String flag = jedisDAO.getJedisReadTemplate().get(RobotConstants.ROBOT_LIVE_CHAT+pid);
                        String commentflag = jedisDAO.getJedisReadTemplate().get(RobotConstants.ROBOT_LIVE_USER_COMMENT_SWITCH);
                        if((flag!=null&&flag.equals("2"))||commentflag!=null){
                            logger.info("program userComment robot will end! programId:"+pid);
                            timer.cancel();
                            break;
                        }
                    } catch (InterruptedException e) {
                        logger.error("program userComment robot thread error! programId:" + pid, e);
                        timer.cancel();
                        break;
                    }
                }
            }else {
                logger.info("program can't create userComment robot, program is end! programId:"+pid);
            }
            logger.info("program robot is end! programId:"+pid);
        }
    }

    class CommentRobot extends TimerTask{
        private final Logger logger = LoggerFactory.getLogger(LikeRobot.class);
        private String pid;
        private String uuid;
        private RobotRule robotRule;
        private List<String> commentList;

        CommentRobot(String uuid, String pid, RobotRule robotRule, List<String> commentList) {
            this.uuid = uuid + "_" + pid;
            this.pid = pid;
            this.robotRule = robotRule;
            this.commentList = commentList;
        }

        @Override
        public void run() {
            logger.info("user comment robot start to send comment to program! programId:" + pid);
            try {
                String flag = jedisDAO.getJedisReadTemplate().get(RobotConstants.ROBOT_LIVE_CHAT+pid);
                String commentflag = jedisDAO.getJedisReadTemplate().get(RobotConstants.ROBOT_LIVE_USER_COMMENT_SWITCH);
                if((commentflag==null)&&(flag!=null&&!flag.equals("2"))){
                    Random random = new Random();
                    for(int i=0;i<robotRule.getUserCommentNum();i++){
                        if(commentList.size()==0){
                            logger.info("user comment robot all comments had been send, stop user comment task! programId:" + pid);
                            this.cancel();
                            break;
                        }else{
                            String comment = commentList.remove(random.nextInt(commentList.size()));
                            if(jedisDAO.getJedisReadTemplate().llen(RobotConstants.ROBOT_LIVE_USER + pid)>0){
                                int virtualUserNum = random.nextInt(Integer.valueOf(jedisDAO.getJedisReadTemplate()
                                        .llen(RobotConstants.ROBOT_LIVE_USER + pid).toString()));
                                sendComment(jedisDAO.getJedisReadTemplate().lindex(
                                        RobotConstants.ROBOT_LIVE_USER + pid, virtualUserNum), comment);
                            }
                            //评论间隔2-40秒
                            Thread.sleep(2+(random.nextInt(38))*1000);
                        }
                    }
                }else{
                    logger.info("user comment robot is end or comment robot is closed, stop user comment task! programId:" + pid);
                    commentList = null;
                    this.cancel();
                }
            } catch (Exception e) {
                logger.error("user comment thread run error! programId:"+pid, e);
                commentList = null;
                this.cancel();
            }
        }

        private void sendComment(String uid, String comment){
            try {
                User user = null;
                if(virtualUserList.get(uid)!=null){
                    user = virtualUserList.get(uid);
                }else {
                    user = userDAO.getUserById(Long.valueOf(uid));
                    virtualUserList.put(user.getUserId().toString(), user);
                }
                if(user == null){
                    logger.error("user comment robot user is null! programId:" + pid + " userId:" + uid + " comment:" + comment);
                }
                ChatEvent chatEvent = ChatEvent.createChatEvent(uid, pid.toString(), 1);
                chatEvent.setType(1);
                if(comment.startsWith("\\")){
                    chatEvent.setContent(convertUnicodeToString(comment));
                }else{
                    chatEvent.setContent(comment);
                }

                chatEvent.setNickName(user.getNickName());
                chatEvent.setPicture(user.getPicture());
                jedisDAO.getJedisWriteTemplate().incr(Constants.LIVE_ONLINE_COMMENT_KEY + pid);
                jedisDAO.getJedisWriteTemplate().publish("chat", chatEvent.toString());
                //TODO 记录日志
                ActionLog actionLog = ActionLogConvert.castChatEventToActionLog(chatEvent);
                actionLogDAO.insertActionLog(actionLog);
                logger.info("user comment robot send comment success! programId:" + pid + " userId:" + uid + " comment:" + comment);
            } catch (Exception e) {
                logger.error("user comment robot send comment error! programId:" + pid + " userId:" + uid + " comment:" + comment,e);
            }
        }

        public String convertUnicodeToString(String utfString){
            StringBuilder sb = new StringBuilder();
            int i = -1;
            int pos = 0;

            while((i=utfString.indexOf("\\u", pos)) != -1){
                sb.append(utfString.substring(pos, i));
                if(i+5 < utfString.length()){
                    pos = i+6;
                    sb.append((char)Integer.parseInt(utfString.substring(i+2, i+6), 16));
                }
            }

            return sb.toString();
        }

    }
}
