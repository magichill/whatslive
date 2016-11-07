package com.letv.whatslive.server.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.http.RequestHeader;
import com.letv.whatslive.common.httpclient.HttpClientUtil;
import com.letv.whatslive.common.httpclient.HttpFetchResult;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Device;
import com.letv.whatslive.model.User;
import com.letv.whatslive.mongo.dao.DeviceDAO;
import com.letv.whatslive.mongo.dao.UserDAO;
import com.letv.whatslive.server.util.LogUtils;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gaoshan on 15-8-4.
 */
@Service
public class InnerUserService {

    @Resource
    private UserDAO userDAO;

    @Resource
    private DeviceDAO deviceDAO;

    @Value("${uc.authbind}")
    protected String UC_AUTHBIND;

    @Value("${uc.authstatus.uri}")
    protected String AUTH_STATUS_URL;

    public boolean saveUser(String userJson, RequestHeader header,String token){
        if(StringUtils.isBlank(userJson)){
            return false;
        }
        try{
            JSONObject userObj = JSON.parseObject(userJson);
            String status = userObj.getString("status");
            if(StringUtils.isNotBlank(status) && status.equals("1")){
                Device device = getDevicebyHeader(header);
                String devId = deviceDAO.saveOrUpdate(device);
                if(StringUtils.isNotBlank(devId)){
                    User user = getUserByUC(userJson);
                    List<String> devList = user.getDevIdList();
                    devList.add(devId);
                    user.setAccessToken(token);
                    userDAO.saveOrUpdateUser(user,1);
                }
//                User user = getUserByUC(userJson);
//                user.setAccessToken(token);
//                userDAO.saveOrUpdateUser(user,1);
                return true;
            }

        }catch(Exception e){
            LogUtils.logError("UserService saveUser : " + e.getMessage(), e);
            return false;
        }
        return false;
    }

    public User getUserByUC(String json){
        User user =  new User();
        JSONObject ucUser = JSON.parseObject(json);
        String statusStr =  ucUser.getString("status");
        if(statusStr.equals("1") && ucUser.get("bean")!=null){
            Map bean = (Map)ucUser.get("bean");
            long uid = Long.parseLong((String) bean.get("uid"));
            String username = (String)bean.get("username");
            String nickname = (String)bean.get("nickname");
//            if(nickname.length()>20){
//                nickname = nickname.substring(0,20);
//            }
            DBObject queryName = new BasicDBObject();
            queryName.put("nickName",nickname);
//            DBObject obj = userDAO.find(queryName);
            String email = (String)bean.get("email");
            String mobile = (String)bean.get("mobile");
            String picture = (String)bean.get("picture");
            int status = Integer.parseInt((String)bean.get("status"));
            if(bean.get("sex")!=null){
                int gender = Integer.parseInt((String)bean.get("gender"));
                user.setSex(gender);
            }else{
                user.setSex(0);
            }
            if(bean.get("city")!=null){
                user.setAddress((String) bean.get("city"));
            }
            if(bean.get("user_connect")!=null){
                Map connect = (Map)bean.get("user_connect");
                String avatar_large = (String)connect.get("avatar");
                int appid = Integer.parseInt((String)connect.get("appid"));
                switch (appid){
                    case 1:
                        user.setSinaId(ObjectUtils.toString(connect.get("oauth_uid"), "0"));
                        user.setUserType(4);
                        user.setSinaBind(1);
                        break;
                    case 3:
                        user.setQqId(ObjectUtils.toString(connect.get("oauth_uid"), "0"));
                        user.setUserType(5);
                        user.setQqBind(1);
                        break;
                    case 12:
                        user.setWeixinId(ObjectUtils.toString(connect.get("oauth_uid"), "0"));
                        user.setUserType(6);
                        user.setWeixinBind(1);
                        break;
                    default:
                        break;
                }
                user.setPicture(avatar_large);
            }
            user.setEmail(email);
            user.setMobile(mobile);
            user.setUserId(uid);
            user.setUserStatus(status);
            user.setNickName(nickname);
            user.setUserName(username);
            user.setPicture(picture);
            if(ucUser.getString("sso_tk")!=null){
                user.setSsoTk(ucUser.getString("sso_tk"));
            }
            user.setDevIdList(new ArrayList<String>());
        }
        return user;

    }

    public DBObject findOne(Long uid){
        DBObject query = new BasicDBObject();
        query.put("_id",uid);
        query.put("userStatus",1);
        return this.userDAO.find(query);
    }

    public Map<Long,DBObject> findAll(BasicDBList values){
        DBObject query = new BasicDBObject();
        query.put("_id",new BasicDBObject("$in",values));
        return this.userDAO.findAll(query);
    }

    public String authBind(String ssouid,String authUid,String type,String token) throws Exception {
        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("ssouid",ssouid));
        nvps.add(new BasicNameValuePair("oauth_uid",authUid));
        nvps.add(new BasicNameValuePair("oauth_type",type));
        nvps.add(new BasicNameValuePair("oauth_token",token));
        return HttpClientUtil.requestPost(UC_AUTHBIND,nvps).getContent();
    }

    public boolean bindStatus(String ssotk) throws Exception {
        if(StringUtils.isBlank(ssotk)){
            LogUtils.logError("ssotk could not be null!");
            return false;
        }
        Map data = Maps.newHashMap();
        StringBuilder sb = new StringBuilder();
        sb.append(AUTH_STATUS_URL);
        sb.append("?sso_tk="+ssotk);
        String result = HttpClientUtil.requestGet(sb.toString()).getContent();
        Map dataMap = JSON.parseObject(result,Map.class);
        String errorCode = ObjectUtils.toString(dataMap.get("errorCode"));
        if(!errorCode.equals("0")){
            LogUtils.logError(ObjectUtils.toString(dataMap.get("message")));
            return false;
        }
        Map bean = (Map)dataMap.get("bean");
        int sinaBind = 0;
        List<Map> connect = JSON.parseObject(ObjectUtils.toString(bean.get("connect")), List.class);
        if(connect!=null && connect.size()>0){
            for(Map map : connect){
                Integer appid = ObjectUtils.toInteger(map.get("appid"));
                switch (appid){
                    case 1:
                        sinaBind = 1;
                        break;
                    default:
                        break;
                }

            }
        }
        if(sinaBind == 1) {
            return true;
        }else{
            return false;
        }
    }
//    public String updateUserInfo(Map<String,Object> params){
//        Long uid = ObjectUtils.toLong(params.get("uid"));
//        DBObject condition = new BasicDBObject();
//        String nickName = ObjectUtils.toString(params.get("nickName"));
//        Integer gender = ObjectUtils.toInteger(params.get("gender"));
//        String city = ObjectUtils.toString(params.get("city"));
//        String address = ObjectUtils.toString(params.get("address"));
//
//        StringBuilder sb = new StringBuilder();
//        sb.append(UC_UPDATE_USERINFO_URI);
//        sb.append("?");
//        sb.append("uid="+uid);
//        sb.append("&nickname="+nickName);
//        sb.append("&gender="+gender);
//        sb.append("&city="+city);
//        sb.append("&address="+address);
//
//        try {
//            HttpFetchResult result = HttpClientUtil.requestGet(sb.toString());
//            Map bean = JSON.parseObject(result.getContent(),Map.class);
//            String errorCode = ObjectUtils.toString(bean.get("errorCode"));
//            if(errorCode.equals("1000")){
//                LogUtils.logError("uid is not exist");
//                return "用户ID不存在!";
//            }else if(errorCode.equals("1013")){
//                LogUtils.logError("nickName in uc is duplicate");
//                return "昵称已存在!";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        condition.put("nickName",nickName);
//        condition.put("_id",new BasicDBObject("$ne",uid));
//        DBObject nickUser = this.userDAO.find(condition);
//        if(nickUser != null){
//            LogUtils.logError("nickName is duplicate");
//            return "昵称已存在!";
//        }
//        DBObject user = this.userDAO.find(new BasicDBObject("_id",uid));
//        if(user == null){
//            LogUtils.logError("user is not exists");
//            return "user is not exists";
//        }
//        DBObject update = new BasicDBObject();
//
//        String pics = ObjectUtils.toString(params.get("pics"));
//        if( gender != null){
//            update.put("sex",gender);
//        }
//        if(!StringUtils.isBlank(nickName)){
//            update.put("nickName",nickName);
//        }
//        if(!StringUtils.isBlank(city)){
//            update.put("city",city);
//        }
//        if(!StringUtils.isBlank(address)){
//            update.put("address",address);
//        }
//        if(!StringUtils.isBlank(pics)){
//            update.put("picture",pics);
//            String[] picArg = pics.split(",");
//            update.put("avatarLarge",picArg[0]);
//        }
//        this.userDAO.update(user, new BasicDBObject("$set",update));
//        return "";
//    }

    private Device getDevicebyHeader(RequestHeader header){
        Device device = new Device();
        device.setId(header.getUdid());
        device.setCorporationId(header.getCorporationId());
        device.setSubCoopId(header.getSubCoopId());
        device.setDevModel(header.getModel());
        device.setDevStatus("1");
        device.setType(ObjectUtils.toInteger(header.getFrom(),0));
        if(StringUtils.isNotBlank(header.getEditionId())){
            device.setEditionId(Integer.parseInt(header.getEditionId().replace(".","")));
        }else{
            device.setEditionId(0);
        }

        if(StringUtils.isNotBlank(header.getFrom())){
            device.setPlatformId(Integer.parseInt(header.getFrom()));
        }else{
            device.setPlatformId(1);
        }
        device.setChannelId(header.getChannelId());
        device.setDevToken(header.getDeviceToken());
        device.setImei(header.getImei());
        device.setImsi(header.getImsi());
        device.setLastActiveTime(System.currentTimeMillis());
        device.setUpdateTime(System.currentTimeMillis());
        device.setCreateTime(System.currentTimeMillis());
        return device;
    }

    public boolean updateUserForLogin(String userJson,RequestHeader header){
        if(StringUtils.isBlank(userJson)){
            return false;
        }
        try{
            JSONObject userObj = JSON.parseObject(userJson);
            Device device = getDevicebyHeader(header);
            String devId = deviceDAO.saveOrUpdate(device);
            if(StringUtils.isNotBlank(devId)){
                User user = getUserByMobile(userJson);
                if(user != null){
                    List<String> devList = user.getDevIdList();
                    devList.add(devId);
                    userDAO.saveOrUpdateUser(user,1);
                }else{
                    return false;
                }
            }
            return true;
        }catch(Exception e){
            LogUtils.logError("UserService saveUser : " + e.getMessage(), e);
            return false;
        }
    }

    public User getUserByMobile(String json){
        User user = new User();
        JSONObject mobileUser = JSON.parseObject(json);
        long uid = Long.parseLong( mobileUser.getString("uid"));
        String username = mobileUser.getString("username");
        String nickname = mobileUser.getString("nickname");
        String email = mobileUser.getString("email");
        String mobile = mobileUser.getString("mobile");
        String picture = mobileUser.getString("picture");
        String address = mobileUser.getString("address");
        int gender = Integer.parseInt(mobileUser.getString("gender"));
        user.setUserId(uid);
        user.setUserName(username);
        user.setNickName(nickname);
        user.setEmail(email);
        user.setMobile(mobile);
        user.setPicture(picture);
        user.setSex(gender);
        user.setAddress(address);
        user.setDevIdList(new ArrayList<String>());
        return user;
    }
}
