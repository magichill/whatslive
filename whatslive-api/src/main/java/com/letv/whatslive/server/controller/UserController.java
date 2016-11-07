package com.letv.whatslive.server.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.http.RequestHeader;
import com.letv.whatslive.common.http.ResponseBody;
import com.letv.whatslive.common.httpclient.HttpClientUtil;
import com.letv.whatslive.common.httpclient.HttpFetchResult;
import com.letv.whatslive.common.utils.*;
import com.letv.whatslive.model.*;
import com.letv.whatslive.model.User;
import com.letv.whatslive.model.constant.ProgramConstants;
import com.letv.whatslive.model.convert.FeedBackConvert;
import com.letv.whatslive.model.convert.UserConvert;
import com.letv.whatslive.model.dto.ProgramDTO;
import com.letv.whatslive.model.dto.RedisUser;
import com.letv.whatslive.model.dto.UserDTO;
import com.letv.whatslive.redis.JedisDAO;
import com.letv.whatslive.server.mq.RedisQueue;
import com.letv.whatslive.server.service.*;
import com.letv.whatslive.server.util.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import facebook4j.*;
import facebook4j.auth.AccessToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import weibo4j.Timeline;
import weibo4j.model.Status;
import weibo4j.model.WeiboException;

import javax.annotation.Resource;
import javax.xml.ws.Response;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by gaoshan on 15-6-30.
 */
@Service
public class UserController extends BaseController {


    public static Twitter twitter;
    public static Facebook facebook = new FacebookFactory().getInstance();

    @Resource
    private UserService userService;

    @Resource
    private BindUserService bindUserService;

    @Resource
    private FriendService friendService;

    @Resource
    private ThirdFriendService thirdFriendService;

    @Resource
    private ProgramService programService;

    @Resource
    private FeedBackService feedBackService;

    @Resource
    private RedisQueue<String> redisQueue;

//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private JedisDAO jedisDAO;


    static {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(null)
                .setOAuthAccessTokenSecret(null);
        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
        facebook.setOAuthAppId(appId,appSecret);
    }

    /**
     * 用户登录接口
     * @param params
     * @param sid
     * @param header
     * @return
     * @throws FacebookException
     */
    public ResponseBody login(Map<String, Object> params, String sid, RequestHeader header) {
        Integer loginType = ObjectUtils.toInteger(params.get("type"));
        String token = ObjectUtils.toString(params.get("token"));
        if(loginType == null ){
            return getErrorResponse(sid,"login type could not be null!");
        }
        User user = null;
        if(loginType == Constant.LOGIN_TYPE_FACEBOOK){
            try {
                user = loginByFacebook(token);
            } catch (FacebookException e) {
                LogUtils.logError("fail to login facebook,[exception] "+e.getMessage());
                return getErrorResponse(sid,Constant.LOGIN_ERROR_CODE);
            }
        }else if(loginType == Constant.LOGIN_TYPE_TWITTER){
            try {
                user = loginByTwitter(token,ObjectUtils.toString(params.get("tokenSecret")));
            } catch (TwitterException e) {
                LogUtils.logError("fail to login twitter,[exception] "+e.getMessage());
                return getErrorResponse(sid,Constant.LOGIN_ERROR_CODE);
            }
        }
        User checkUser = checkRegister(user.getThirdId(),loginType);
        if(checkUser != null){
            checkUser.setAccessToken(token);
            userService.saveOrUpdateUser(checkUser);
            return getResponseBody(sid,new UserDTO(checkUser));
        }else{
            user.setAccessToken(token);
            long userId = userService.insertUser(user);
            user.setUserId(userId);
            if(userId < 0){
                LogUtils.logError("fail to save user!");
                return getErrorResponse(sid,"fail to login");
            }

            pullThirdFriend(userId,loginType,token,ObjectUtils.toString(params.get("tokenSecret")));
            return getResponseBody(sid,new UserDTO(user));
        }
    }

    /**
     * twitter h5 登录
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody loginH5(Map<String, Object> params, String sid, RequestHeader header) {
        String pin = ObjectUtils.toString(params.get("pin"));
        String token = ObjectUtils.toString(params.get("token"));
        String tokenSecret = ObjectUtils.toString(params.get("tokenSecret"));
        try {
            RequestToken requestToken = new RequestToken(token,tokenSecret);
            twitter4j.auth.AccessToken accessToken = twitter.getOAuthAccessToken(requestToken,pin);
            User user = loginByTwitter(accessToken.getToken(),accessToken.getTokenSecret());
            return getResponseBody(sid,new UserDTO(user));
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return  getResponseBody(sid,"fail to loginH5");
    }


    /**
     * 更新用户基本信息
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody updateUser(Map<String, Object> params, String sid, RequestHeader header) {
        String userId = header.getUserId();
        String introduce = ObjectUtils.toString(params.get("introduce"));
        String nickName = ObjectUtils.toString(params.get("nickName"));
        try{
            User user = userService.getUserById(ObjectUtils.toLong(userId));
            if(user!=null) {
                user.setNickName(nickName);
                user.setIntroduce(introduce);
                userService.saveOrUpdateUser(user);
            }else{
                return getErrorResponse(sid,"user is not exist");
            }
        }catch (Exception e){
            LogUtils.logError("fail to update user");
            e.printStackTrace();
            return getErrorResponse(sid,e.getMessage());
        }

        return getResponseBody(sid,"ok");

    }

    /**
     * 更新用户头像
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody updateUserIcon(Map<String, Object> params, String sid, RequestHeader header) {
        Long userId = ObjectUtils.toLong(header.getUserId());
        String amzonKey = ObjectUtils.toString(params.get("iconKey"));
        User user = userService.getUserById(userId);
        user.setPicture(amzonKey);
        Integer status = userService.saveOrUpdateUser(user);
        if(status > 0){
            return getResponseBody(sid,"ok");
        }else{
            return getErrorResponse(sid,"fail");
        }
    }
    /**
     * 绑定第三方用户
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody bindUser(Map<String, Object> params, String sid, RequestHeader header) {
        Integer loginType = ObjectUtils.toInteger(params.get("type"));
        String token = ObjectUtils.toString(params.get("token"));
        String tokenSecret = ObjectUtils.toString(params.get("tokenSecret"));
        Long userId = ObjectUtils.toLong(header.getUserId());
        if(loginType == null ){
            return getErrorResponse(sid,Constant.PARAMS_ERROR_CODE);
        }
        User user = null;
        if(loginType == Constant.LOGIN_TYPE_FACEBOOK){
            try {
                user = loginByFacebook(token);
            } catch (FacebookException e) {
                LogUtils.logError("fail to login facebook,[exception] "+e.getMessage());
                return getErrorResponse(sid,Constant.LOGIN_ERROR_CODE);
            }
        }else if(loginType == Constant.LOGIN_TYPE_TWITTER){
            try {
                user = loginByTwitter(token,tokenSecret);
            } catch (TwitterException e) {
                LogUtils.logError("fail to login twitter,[exception] " + e.getMessage());
                return getErrorResponse(sid,Constant.LOGIN_ERROR_CODE);
            }
        }
        user.setUserId(userId);
        bindUserService.saveBindUser(user);
        return getResponseBody(sid,"ok");
    }

    /**
     * 用户分享接口
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody share(Map<String, Object> params, String sid, RequestHeader header) {
        Integer type = ObjectUtils.toInteger(params.get("type"));
        String accessToken = ObjectUtils.toString(params.get("token"));
        String text = ObjectUtils.toString(params.get("text"));
        Long programId = ObjectUtils.toLong(params.get("programId"));
        Long userId = ObjectUtils.toLong(params.get("userId"));
        Program program = null;
        if(programId != null){
            program = programService.getProgramById(programId);
        }
        User user = null;
        if(userId != null){
            user = userService.getUserById(userId);
        }

        String result = "";
        if(type == null ){
            return getErrorResponse(sid,Constant.PARAMS_ERROR_CODE);
        }
        if(type == Constant.LOGIN_TYPE_FACEBOOK){
            result = shareFacebook(accessToken,text);
        }else if(type == Constant.LOGIN_TYPE_TWITTER){
            String tokenSecret = ObjectUtils.toString(params.get("tokenSecret"));
            result = shareTwitter(accessToken,tokenSecret,text);
        }else if(type == Constant.LOGIN_TYPE_WEIBO){
            String screenName = ObjectUtils.toString(params.get("screenName"));
            result = shareWeibo(screenName,accessToken,program,user);
        }
        if(result.equals("")){
            return getErrorResponse(sid,Constant.SHARE_ERROR_CODE);
        }
        return getResponseBody(sid,result);
    }

    /**
     * twitter认证地址接口
     * @param params
     * @param sid
     * @param header
     * @return
     * @throws TwitterException
     */
    public ResponseBody twitterAuthUrl(Map<String, Object> params, String sid, RequestHeader header) throws TwitterException {
        twitter.setOAuthAccessToken(null);
        RequestToken requestToken = twitter.getOAuthRequestToken();
        String url = requestToken.getAuthorizationURL();
        Map<String,String> data = Maps.newHashMap();
        data.put("authUrl",url);
        data.put("tokenSecret",requestToken.getTokenSecret());
        data.put("token",requestToken.getToken());
        return getResponseBody(sid,data);
    }


    /**
     * 获取第三方好友列表
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody friends(Map<String, Object> params, String sid, RequestHeader header) {
        Integer type = ObjectUtils.toInteger(params.get("type"));
        String token = ObjectUtils.toString(params.get("token"));
        String tokenSecret = ObjectUtils.toString(params.get("tokenSecret"));
        Integer start = ObjectUtils.toInteger(params.get("start"));
        Integer limit = ObjectUtils.toInteger(params.get("limit"));
//        String uid = ObjectUtils.toString(params.get("uid"));
        Long userId = ObjectUtils.toLong(header.getUserId());
        List<ThirdFriend> result = Lists.newArrayList();
        if(type == Constant.LOGIN_TYPE_FACEBOOK){
            result = getFbFriends(token,start,limit,userId);
        }else if(type == Constant.LOGIN_TYPE_TWITTER){
            try {
                result = getTwFriends(token,tokenSecret,start,limit,userId);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
//        else if(type == Constant.LOGIN_TYPE_WEIBO){
//            result = getWeiboFriends(start,limit,userId);
//        }
        if(start == 1){
//            User user = userService.getUserById(userId);
//            if(StringUtils.isNotBlank(token) && StringUtils.isNotBlank(uid)){
            pullThirdFriend(userId, type, token, tokenSecret);
//            }
        }
        return getResponseBody(sid,result);
    }

    /**
     * 获取用户相关数据
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody userAccount(Map<String, Object> params, String sid, RequestHeader header) {
        Long userId = ObjectUtils.toLong(header.getUserId());
        Long currentUserId = ObjectUtils.toLong(params.get("userId"));
        String edition = ObjectUtils.toString(header.getEditionId());

        int editionNum = 0;
        if (edition != null){
            if (edition.indexOf(".") > -1) {
                edition = edition.replace(".", "");
                edition = edition + "0";
            }
            editionNum = ObjectUtils.toInteger(edition, 0);
        }

        if(userId == null){
            LogUtils.logError("[userAccount] userId is null!");
            return getErrorResponse(sid,Constant.PARAMS_ERROR_CODE);
        }
        Map<String,Object> result = Maps.newHashMap();
        if(currentUserId != null){
            Integer status = friendService.getFriendShipByUserId(userId, currentUserId);
            result.put("status",status);
        }else{
            currentUserId = userId;
        }
        Long following = friendService.getFollowingCount(currentUserId);
        Long follower = friendService.getFollowerCount(currentUserId);
        User user = userService.getUserById(currentUserId);
        Long broadCastNum = 0l;
        if(editionNum < 1100){
            broadCastNum = programService.countReplayProgramByUserId(currentUserId,0);
        }else{
            broadCastNum = programService.countReplayProgramByUserId(currentUserId,1);
        }

        List<ProgramDTO> plans = programService.getPlanProgramByUserId(currentUserId);
        if(plans != null && plans.size()>0){
            result.put("hasPlan",1);
        }else{
            result.put("hasPlan",0);
        }
        if(user != null) {
            result.put("broadcast", broadCastNum);
            result.put("likeNum", user.getLikeNum());
            result.put("user", new UserDTO(user));
        }
        result.put("following",following);
        result.put("follower",follower);

        return getResponseBody(sid,result);
    }

    /**
     * 观看当前直播的用户列表
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody onlineUserV2(Map<String, Object> params, String sid, RequestHeader header) {
        String programId = ObjectUtils.toString(params.get("programId"));
        Integer start = ObjectUtils.toInteger(params.get("start"),0);
        if(start > 0){
            start = start -1;
        }
        Integer limit = ObjectUtils.toInteger(params.get("limit"),20);
        String key = Constants.LIVE_ONLINE_USER_LIST_KEY+programId;
//        jedisDAO.getJedisReadTemplate();
//        List<String> userList = stringRedisTemplate.boundListOps(key).range(start,limit);
        List<String> userList =jedisDAO.getJedisReadTemplate().lrange(key,start,limit);
        Long userCount = jedisDAO.getJedisReadTemplate().llen(key);
        Map<String,Object> result = Maps.newHashMap();
        List<RedisUser> data = Lists.newArrayList();
        for(String userStr : userList){
            User user = userService.getUserById(ObjectUtils.toLong(userStr));
            if(user != null) {
                RedisUser redisUser = new RedisUser();
                redisUser.setNickName(user.getNickName());
                redisUser.setUid(ObjectUtils.toString(user.getUserId()));
                String pics = user.getPicture();
                if(StringUtils.isNotBlank(pics)){
                    String[] picArg = pics.split(",");
                    pics = picArg[0];
                }else{
                    pics = Constants.USER_ICON_DEFAULT;
                }
                redisUser.setPicture(pics);
                data.add(redisUser);
            }
        }
        result.put("userList",data);
        result.put("userCount",userCount);
        return getResponseBody(sid,result);
    }

    /**
     * 观看当前直播的用户列表
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody onlineUser(Map<String, Object> params, String sid, RequestHeader header) {
        String programId = ObjectUtils.toString(params.get("programId"));
        Integer start = ObjectUtils.toInteger(params.get("start"),0);
        if(start > 0){
            start = start -1;
        }
        Integer limit = ObjectUtils.toInteger(params.get("limit"),20);
        String key = Constants.LIVE_ONLINE_USER_LIST_KEY+programId;
//        jedisDAO.getJedisReadTemplate();
//        List<String> userList = stringRedisTemplate.boundListOps(key).range(start,limit);
        List<String> userList =jedisDAO.getJedisReadTemplate().lrange(key,start,limit);
        List<RedisUser> data = Lists.newArrayList();
        for(String userStr : userList){
            User user = userService.getUserById(ObjectUtils.toLong(userStr));
            if(user != null) {
                RedisUser redisUser = new RedisUser();
                redisUser.setNickName(user.getNickName());
                redisUser.setUid(ObjectUtils.toString(user.getUserId()));
                String pics = user.getPicture();
                if(StringUtils.isNotBlank(pics)){
                    String[] picArg = pics.split(",");
                    pics = picArg[0];
                }else{
                    pics = Constants.USER_ICON_DEFAULT;
                }
                redisUser.setPicture(pics);
                data.add(redisUser);
            }
        }
        return getResponseBody(sid,data);
    }

    /**
     * 观看直播人数列表及与当前用户好友关系
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody viewers(Map<String, Object> params, String sid, RequestHeader header) {
        String programId = ObjectUtils.toString(params.get("programId"));
        Long userId = ObjectUtils.toLong(header.getUserId());
        Integer start = ObjectUtils.toInteger(params.get("start"), 1);
        Integer limit = ObjectUtils.toInteger(params.get("limit"), 20);
        String key = Constants.LIVE_ONLINE_TOTALUSER_KEY+programId;
        Set<String> redisData = jedisDAO.getJedisReadTemplate().smembers(key);

        PageBean<String> pageBean = new PageBean<String>();
        List<String> userList = pageBean.page(start,limit,new ArrayList<String>(redisData));

        List<RedisUser> data = Lists.newArrayList();
        for(String userStr : userList){
            RedisUser user = JSON.parseObject(userStr,RedisUser.class);
            Integer status = friendService.getFriendShipByUserId(userId,ObjectUtils.toLong(user.getUid()));
            user.setStatus(status);
            data.add(user);
        }
        return getResponseBody(sid,data);
    }
    /**
     * 判断用户是否注册app
     * @param thirdId
     * @param type
     * @return
     */
    private User checkRegister(String thirdId,int type){
        DBObject obj = userService.checkRegister(thirdId, type);
        if(obj != null){
            return UserConvert.castDBObjectToUser(obj);
        }else{
            BindUser bindUser = bindUserService.getUserByThirdId(thirdId,type);
            if(bindUser != null){
                return UserConvert.castDBObjectToUser(bindUser.getBindUser().fetch());
            }
            return null;
        }
    }

    private User loginByFacebook(String token) throws FacebookException {
        facebook.setOAuthAccessToken(new AccessToken(token));
        facebook.setOAuthPermissions("email,publish_stream,user_friends");
        return UserConverter.facebook2User(facebook);
    }


    private User loginByTwitter(String token,String tokenSecret) throws TwitterException {
        twitter.setOAuthAccessToken(null);
        twitter4j.auth.AccessToken accessToken = new twitter4j.auth.AccessToken(token,tokenSecret);
        twitter.setOAuthAccessToken(accessToken);
        return UserConverter.twitter2User(twitter,accessToken);
    }

    private String shareFacebook(String accessToekn,String text){
        facebook.setOAuthAccessToken(new AccessToken(accessToekn));
        facebook.setOAuthPermissions("email,publish_stream");
        try {
            facebook.postStatusMessage(text);
        } catch (FacebookException e) {
            LogUtils.logError("fail to share text to facebook ,[exception] " + e.getMessage());
            return "";
        }
        return "success to share facebook";
    }

    private String shareTwitter(String accessToken,String tokenSecret,String text){
        try {
            twitter.setOAuthAccessToken(null);
            twitter4j.auth.AccessToken token = new twitter4j.auth.AccessToken(accessToken,tokenSecret);
            twitter.setOAuthAccessToken(token);
            twitter.updateStatus(text);
        } catch (TwitterException e) {
            LogUtils.logError("fail to share text to twitter,[Exception] " + e.getMessage());
            return "";
        }
        return "success to share twitter";
    }

    private String shareWeibo(String screenName,String accessToken,Program program,User user) {

        StringBuilder sb = new StringBuilder();
        if (StringUtils.isBlank(screenName)){
            if(program != null){
                if(user == null) {
                    switch (program.getPType()) {
                        case 1:
                            sb.append(Constant.WEIBO_SHARE_TEXT);
                            sb.append(":" + program.getPName());
                            sb.append(",赏脸来围观下吧，求评求赞!" + ProgramConstants.SHARE_URL + program.getId());
                            break;
                        case 2:
                            sb.append(DateUtils.long2YMDHMS(program.getStartTime()));
                            sb.append("我将在@乐嗨直播");
                            sb.append(program.getPName() + "走过路过表错过，快来订阅吧！");
                            sb.append(ProgramConstants.SHARE_URL + program.getId());
//                            sb.append(DateUtils.long2YMDHM(program.getStartTime()));
//                            sb.append("我会在@乐嗨直播，" + program.getPName());
//                            sb.append(",快来订阅！" + ProgramConstants.SHARE_URL + program.getId());
                            break;
                        case 3:
                            sb.append("快来@乐嗨直播，观看我的视频：");
                            sb.append(":" + program.getPName() + "!");
                            sb.append(ProgramConstants.SHARE_URL + program.getId());
                            break;
                        default:
                            break;
                    }
                }else{
                    switch (program.getPType()) {
                        case 1:
                            sb.append("我在@乐嗨直播，观看");
                            sb.append(program.getPName());
                            sb.append(",直播超棒!速来!" + ProgramConstants.SHARE_URL + program.getId());
                            break;
                        case 2:
                            sb.append(DateUtils.long2YMDHM(program.getStartTime()));
                            sb.append(user.getNickName()+"会在@乐嗨直播:" + program.getPName());
                            sb.append(",到时一起看！" + ProgramConstants.SHARE_URL + program.getId());
                            break;
                        case 3:
                            sb.append("我在@乐嗨直播：观看");
                            sb.append(program.getPName() + "，快来一起围观!");
                            sb.append(ProgramConstants.SHARE_URL + program.getId());
                            break;
                        default:
                            break;
                    }
                }
            }
        }else{
            sb.append("@"+screenName+Constant.WEIBO_INVITE_TEXT);
        }

        ShareEvent shareEvent = new ShareEvent();
        shareEvent.setAccessToken(accessToken);
        shareEvent.setShareText(sb.toString());
        redisQueue.pushFromHead(shareEvent.toString());

        return "success to share weibo";
    }


    private List<ThirdFriend> getFbFriends(String token,Integer start,Integer limit,Long userId){
        List<DBObject> list = thirdFriendService.getFriendListByUserId(start,limit,userId,1);
        List<ThirdFriend> friendList = Lists.newArrayList();
        for(DBObject obj : list){
            ThirdFriend thirdFriend = new ThirdFriend();
            thirdFriend.setNickName(ObjectUtils.toString(obj.get("nickName")));
            thirdFriend.setIntroduce(ObjectUtils.toString(obj.get("introduce")));
            thirdFriend.setUserIcon(ObjectUtils.toString(obj.get("userIcon")));
            thirdFriend.setThirdId(ObjectUtils.toString(obj.get("thirdId")));
            friendList.add(thirdFriend);
        }
        return getFriendShipResult(friendList,userId,1);
    }

    private List<ThirdFriend> getTwFriends(String token,String tokenSecret,Integer start,Integer limit,Long userId) throws TwitterException {
        List<DBObject> list = thirdFriendService.getFriendListByUserId(start,limit,userId,2);
        List<ThirdFriend> friendList = Lists.newArrayList();
        for(DBObject obj : list){
            ThirdFriend thirdFriend = new ThirdFriend();
            thirdFriend.setNickName(ObjectUtils.toString(obj.get("nickName")));
            thirdFriend.setIntroduce(ObjectUtils.toString(obj.get("introduce")));
            thirdFriend.setUserIcon(ObjectUtils.toString(obj.get("userIcon")));
            thirdFriend.setThirdId(ObjectUtils.toString(obj.get("thirdId")));
            friendList.add(thirdFriend);
        }
        return getFriendShipResult(friendList,userId,2);
    }

//    public List<ThirdFriend> getWeiboFriends(Integer start,Integer limit,Long userId) {
//        List<DBObject> list = thirdFriendService.getFriendListByUserId(start,limit,userId,4);
//        List<ThirdFriend> friendList = Lists.newArrayList();
//        for(DBObject obj : list){
//            ThirdFriend thirdFriend = new ThirdFriend();
//            thirdFriend.setNickName(ObjectUtils.toString(obj.get("nickName")));
//            thirdFriend.setIntroduce(ObjectUtils.toString(obj.get("introduce")));
//            thirdFriend.setUserIcon(ObjectUtils.toString(obj.get("userIcon")));
//            thirdFriend.setThirdId(ObjectUtils.toString(obj.get("thirdId")));
//            friendList.add(thirdFriend);
//        }
//        return getFriendShipResult(friendList,userId,2);
//    }

    public List<ThirdFriend> getFriendShipResult(List<ThirdFriend> list,Long userId,Integer type){
        List<ThirdFriend> result = Lists.newArrayList();
        for(ThirdFriend tf : list){
            DBObject registerUser = userService.checkRegister(tf.getThirdId(),type);
            if(registerUser == null){
                tf.setUserId(0l); //0为未注册app用户
            }else{
                tf.setUserId(ObjectUtils.toLong(registerUser.get("_id")));
                tf.setNickName(ObjectUtils.toString(registerUser.get("nickName")));
                tf.setIntroduce(ObjectUtils.toString(registerUser.get("introduce")));
                Integer status = friendService.getFriendShipByUserId(userId,ObjectUtils.toLong(registerUser.get("_id")));
                tf.setStatus(status);
            }
            result.add(tf);
        }
        return result;
    }



    public void pullThirdFriend(Long userId,Integer loginType,String token,String tokenSecret){
        PullFriendList pullFriendList = new PullFriendList();
        pullFriendList.setUserId(userId);
        pullFriendList.setType(loginType);
        pullFriendList.setToken(token);
        pullFriendList.setTokenSecret(tokenSecret);
        pullFriendList.setFacebook(facebook);
        pullFriendList.setTwitter(twitter);
//        pullFriendList.setUid(uid);
        pullFriendList.setThirdFriendService(thirdFriendService);
        ThreadDistribution.getInstance().doWork(pullFriendList);
//        pullFriendList.run();
    }


    /**
     * 用户反馈接口
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody feedBack(Map<String, Object> params, String sid, RequestHeader header) {
        Long uid = ObjectUtils.toLong(params.get("userId"),0l);
        String programId = ObjectUtils.toString(params.get("programId"));
        String network = ObjectUtils.toString(params.get("network"));
        String feedback = ObjectUtils.toString(params.get("feedback"), "");
        String ip = ObjectUtils.toString(params.get("ip"), "");
        String imei = ObjectUtils.toString(params.get("imei"), "");
        String urlTime = ObjectUtils.toString(params.get("lastGetPlayUrlTime"),"");
        String playTime = ObjectUtils.toString(params.get("lastPlayTime"),"");
        String totalPalyTime = ObjectUtils.toString(params.get("lastTotalPlayTime"),"");
        if(urlTime.indexOf(".")>-1){
            urlTime = urlTime.substring(0,urlTime.indexOf("."));
        }
        if(playTime.indexOf(".")>-1){
            playTime = playTime.substring(0,playTime.indexOf("."));
        }
        if(totalPalyTime.indexOf(".")>-1){
            totalPalyTime = totalPalyTime.substring(0,totalPalyTime.indexOf("."));
        }
        Long lastGetPlayUrlTime = ObjectUtils.toLong(urlTime, 0l);
        Long lastPlayTime = ObjectUtils.toLong(playTime, 0l);
        Long lastTotalPlayTime = ObjectUtils.toLong(totalPalyTime, 0l);
        String phone = ObjectUtils.toString(params.get("phone"));

        if(feedback.length()>140){
            return getErrorResponse(sid,"content length could not more than 140 characters!");
        }
        try{
            FeedBack feedBack = new FeedBack();
            feedBack.setUserId(uid);
            feedBack.setFeedback(feedback);
            feedBack.setImei(imei);
            feedBack.setIp(ip);
            feedBack.setLastGetPlayUrlTime(lastGetPlayUrlTime);
            feedBack.setLastPlayTime(lastPlayTime);
            feedBack.setLastTotalPlayTime(lastTotalPlayTime);
            feedBack.setPhone(phone);
            feedBack.setProgramId(programId);
            feedBack.setNetwork(network);
            feedBackService.submitFeedBack(FeedBackConvert.castFeedBackToDBObject(feedBack));
            return getResponseBody(sid,"ok");
        }catch (Exception e){
            LogUtils.logError("UserController feedback Failure:",e);
            return getErrorResponse(sid,"feedback failed");
        }
    }

    /**
     * 获取用户基本信息
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody getUserInfo(Map<String, Object> params, String sid, RequestHeader header) {
        Long userId = ObjectUtils.toLong(header.getUserId());
        if(userId == null){
            LogUtils.logError("getUserInfo api params error,userId is null!");
            return getErrorResponse(sid,Constant.PARAMS_ERROR_CODE);
        }
        User user = userService.getUserById(userId);
        return getResponseBody(sid,new UserDTO(user));

    }
}
