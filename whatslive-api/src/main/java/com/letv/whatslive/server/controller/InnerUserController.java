package com.letv.whatslive.server.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.http.RequestHeader;
import com.letv.whatslive.common.http.ResponseBody;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.common.utils.ThreadDistribution;
import com.letv.whatslive.model.BindUser;
import com.letv.whatslive.model.ThirdFriend;
import com.letv.whatslive.model.User;
import com.letv.whatslive.server.service.*;
import com.letv.whatslive.server.util.Constant;
import com.letv.whatslive.server.util.LogUtils;
import com.letv.whatslive.server.util.PullFriendList;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by gaoshan on 15-8-4.
 */
@Service
public class InnerUserController extends BaseController {

    @Resource
    private LetvUCService letvUCService;

    @Resource
    private InnerUserService innerUserService;

    @Resource
    private ThirdFriendService thirdFriendService;

    @Resource
    private BindUserService bindUserService;

    @Resource
    private UserService userService;

    @Resource
    private FriendService friendService;

    /**
     * 微博，微信第三方登陆
     * @return
     * @throws Exception
     */
    public ResponseBody thirdAppLogin(Map<String, Object> params, String sid, RequestHeader header) throws Exception {
        ResponseBody responseBody = null;
        try {
            String act = StringUtils.trimToEmpty((String) params.get("act"));
            String uid = StringUtils.trimToEmpty((String) params.get("uid"));
            String accessToken = StringUtils.trimToEmpty((String) params.get("accessToken"));
            String ip = StringUtils.trimToEmpty((String) params.get("ip"));
            Integer from = ObjectUtils.toInteger(header.getFrom(),2);
            if (StringUtils.isBlank(uid) || StringUtils.isBlank(act) || StringUtils.isBlank(accessToken) || StringUtils.isBlank(ip)) {
                return getErrorResponse(sid,"params is invalid");
            }
            String responseString = letvUCService.thirdAppLogin(uid, accessToken, ip, act,from);
            if(responseString == null){
                LogUtils.logError("fail to get user info from uc");
                return getErrorResponse(sid, Constant.LOGIN_ERROR_CODE);
            }
            LogUtils.commonLog("login result=="+responseString);
            if (StringUtils.isNotBlank(responseString)) {
                boolean success = innerUserService.saveUser(responseString, header,accessToken);
                if (success) {
                    logger.debug("UserController thirdAppLogin success saveOrUpdateUser " + responseString);
                } else {
                    logger.debug("UserController thirdAppLogin failure saveOrUpdateUser " + responseString);
                }
            }
            Map data = JSON.parseObject(responseString, Map.class);
            Map bean = (Map)data.get("bean");
            Map connUser = (Map)bean.get("user_connect");
            DBObject user = innerUserService.findOne(ObjectUtils.toLong(bean.get("uid")));
            if(act.equals("appsina")) {
                pullWeiboFriend(ObjectUtils.toLong(user.get("_id")), Constant.LOGIN_TYPE_WEIBO, accessToken, uid);
            }
            if(user != null){
                bean.put("nickname",ObjectUtils.toString(user.get("nickName")));
                bean.put("introduce",ObjectUtils.toString(user.get("introduce")));
                bean.put("picture",ObjectUtils.toString(user.get("picture")));
                bean.put("address",ObjectUtils.toString(user.get("address")));
                bean.put("sinaBind",ObjectUtils.toString(user.get("sinaBind"),"0"));
                bean.put("qqBind",ObjectUtils.toString(user.get("qqBind"),"0"));
                bean.put("weixinBind",ObjectUtils.toString(user.get("weixinBind"),"0"));
                data.put("bean",bean);
            }

            responseBody = getResponseBody(sid, data);
        } catch (Exception e) {
            LogUtils.logError("UserController thirdAppLogin Failure : ", e);
            return getErrorResponse(sid,Constant.LOGIN_ERROR_CODE);
        }
        return responseBody;
    }

    /**
     * @param params
     * @param sid
     * @return
     */
    public ResponseBody thirdH5Login(Map<String, Object> params, String sid, RequestHeader header) throws Exception {
        ResponseBody responseBody = null;
        try {
            String act = StringUtils.trimToEmpty((String) params.get("act"));
            if (StringUtils.isBlank(act)) {
                throw new IllegalArgumentException("act is blank");
            }
            String third = letvUCService.thirdH5Login(act);
            Map data = Maps.newHashMap();
            data.put("url", third);
            data.put("act", act);
            responseBody = getResponseBody(sid, data);
        }  catch (Exception e) {
            LogUtils.logError("UserController thirdH5Login Failure : ", e);
            return getErrorResponse(sid,Constant.LOGIN_ERROR_CODE);
        }
        return responseBody;

    }

    public ResponseBody updateUserForLogin(Map<String,Object> params,String sid,RequestHeader header) {
        ResponseBody responseBody = null;
        try {
            String user = StringUtils.trimToEmpty(ObjectUtils.toString(params.get("user")));
            if (StringUtils.isBlank(user)) {
                return getErrorResponse(sid,"user is blank");
            }
            Map data = Maps.newHashMap();
            boolean success = innerUserService.updateUserForLogin(user, header);
            if (success) {
                data.put("message","success");
                LogUtils.commonLog("uploadUser success saveOrUpdateUser " + user);
                responseBody = getResponseBody(sid,data);
            } else {
                data.put("message","failed");
                LogUtils.commonLog("uploadUser failure saveOrUpdateUser " + user);
                responseBody = getResponseBody(sid,data);
            }
        } catch (Exception e) {
            LogUtils.logError("UserController uploadUser Failure : ", e);
            return getErrorResponse(sid,e.getMessage());
        }
        return responseBody;
    }

    public ResponseBody uploadUser(Map<String, Object> params, String sid, RequestHeader header) throws Exception {
        ResponseBody responseBody = null;
        try {
            String user = StringUtils.trimToEmpty((String) params.get("user"));
            if (StringUtils.isBlank(user)) {
                throw new IllegalArgumentException("user is blank");
            }
            //TODO
            boolean success = innerUserService.saveUser(user, header,"");
            if (success) {
                LogUtils.commonLog("uploadUser success saveOrUpdateUser " + user);
            } else {
                LogUtils.commonLog("uploadUser failure saveOrUpdateUser " + user);
            }
            Map data = Maps.newHashMap();
            responseBody = getResponseBody(sid, data);
        } catch (Exception e) {
            LogUtils.logError("UserController uploadUser Failure : ", e);
        }
        return responseBody;
    }
    /**
     * 获取微博好友
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody weiboFriends(Map<String, Object> params, String sid, RequestHeader header) {
        String token = ObjectUtils.toString(params.get("token"));
        Integer start = ObjectUtils.toInteger(params.get("start"));
        Integer limit = ObjectUtils.toInteger(params.get("limit"));
        String uid = ObjectUtils.toString(params.get("uid"));
        Long userId = ObjectUtils.toLong(header.getUserId());
        try {
            List<ThirdFriend> result = getWeiboFriends(start, limit, userId);
            if(StringUtils.isNotBlank(token) && StringUtils.isNotBlank(uid)) {
                if (start == 1) {
                    pullWeiboFriend(userId, Constant.LOGIN_TYPE_WEIBO, token, uid);
                }
            }
            return getResponseBody(sid, result);
        }catch (Exception e){
            LogUtils.logError("userId=="+userId+" [weibo friends api] exception:",e);
            return getErrorResponse(sid,Constant.WEIBO_ERROR_CODE);
        }
    }
    /**
     * 绑定并验证微博账号，并拉取好友列表
     * @param params
     * @param sid
     * @param header
     * @return
     */
    public ResponseBody bindWeibo(Map<String, Object> params, String sid, RequestHeader header) {
        String uid = ObjectUtils.toString(params.get("uid"));  //微博的第三方id
        String token = ObjectUtils.toString(params.get("access_token"));  //微博的登陆token
        Long userId = ObjectUtils.toLong(header.getUserId());
        User user = new User();
        user.setAccessToken(token);
        user.setThirdId(uid);
        user.setUserType(Constant.LOGIN_TYPE_WEIBO);
        user.setUserId(userId);
        try {
            DBObject dbUser = userService.getUserBySinaId(uid, "sinaBind");
            BindUser bindUser = bindUserService.getUserByThirdId(uid, Constant.LOGIN_TYPE_WEIBO);
            if (bindUser == null) {
                if (dbUser == null) {
                    try {
                        String result = innerUserService.authBind(header.getUserId(), uid, "sina", token);
                        bindUserService.saveBindUser(user);
                        LogUtils.commonLog("bindWeibo result="+result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return getErrorResponse(sid, "绑定微博失败");
                    }
                } else {
                    return getErrorResponse(sid, "该微博账号已经被注册!");
                }
            } else {
                if (ObjectUtils.toLong(bindUser.getBindUser().fetch().get("_id")) == userId) {
                    bindUserService.saveBindUser(user);
                }
            }
            pullWeiboFriend(userId, Constant.LOGIN_TYPE_WEIBO, token, uid);
            return getResponseBody(sid, "ok");
        }catch (Exception e){
            LogUtils.logError("fail to bind weibo,exception :",e);
            return getErrorResponse(sid,Constant.WEIBO_ERROR_CODE);
        }
    }

    public List<ThirdFriend> getWeiboFriends(Integer start,Integer limit,Long userId) {
        List<DBObject> list = thirdFriendService.getFriendListByUserId(start,limit,userId,4);
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

    public List<ThirdFriend> getFriendShipResult(List<ThirdFriend> list,Long userId,Integer type){
        List<ThirdFriend> result = Lists.newArrayList();
        for(ThirdFriend tf : list){
            DBObject registerUser = userService.checkWeiboRegister(tf.getThirdId());
            if(registerUser == null){
                tf.setUserId(0l); //0为未注册app用户
            }else{
                tf.setUserId(ObjectUtils.toLong(registerUser.get("_id")));
                tf.setNickName(ObjectUtils.toString(registerUser.get("nickName")));
                tf.setIntroduce(ObjectUtils.toString(registerUser.get("introduce")));
                tf.setRole(ObjectUtils.toInteger(registerUser.get("role")));
                Integer status = friendService.getFriendShipByUserId(userId,ObjectUtils.toLong(registerUser.get("_id")));
                tf.setStatus(status);
            }
            result.add(tf);
        }
        return result;
    }
    public void pullWeiboFriend(Long userId,Integer loginType,String token,String uid){
        PullFriendList pullFriendList = new PullFriendList();
        pullFriendList.setUserId(userId);
        pullFriendList.setType(loginType);
        pullFriendList.setToken(token);
        pullFriendList.setUid(uid);
        pullFriendList.setThirdFriendService(thirdFriendService);
        ThreadDistribution.getInstance().doWork(pullFriendList);
//        pullFriendList.run();
    }

}
