package com.letv.whatslive.server.controller;

import com.google.common.collect.Maps;
import com.letv.whatslive.common.http.ResponseBody;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.common.utils.PropertiesGetter;
import com.letv.whatslive.model.User;
import com.letv.whatslive.server.service.UserService;
import com.letv.whatslive.server.util.Constant;
import com.letv.whatslive.server.util.LogUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by gaoshan on 15-6-30.
 */
public class BaseController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String appId = PropertiesGetter.getValue("facebook.appid");
    public static final String appSecret = PropertiesGetter.getValue("facebook.appSecret");

    public static final String consumerKey = PropertiesGetter.getValue("oauth.consumerKey");
    public static final String consumerSecret = PropertiesGetter.getValue("oauth.consumerSecret");

    @Resource
    private UserService userService;

    protected ResponseBody getResponseBody(String sid, Object data) {
        ResponseBody responseBody = new ResponseBody();
        responseBody.setSid(sid);
        responseBody.setResult(String.valueOf(Constant.STATUS_OK));
        responseBody.setData(data);
        return responseBody;
    }

    protected ResponseBody getTimeStampResponseBody(String sid, Object data,Long timestamp) {
        ResponseBody responseBody = new ResponseBody();
        responseBody.setSid(sid);
        responseBody.setResult(String.valueOf(Constant.STATUS_OK));
        responseBody.setData(data);
        responseBody.setTimestamp(timestamp);
        return responseBody;
    }

    protected ResponseBody getErrorResponse(String sid,String msg){
        ResponseBody responseBody = new ResponseBody();
        responseBody.setSid(sid);
        responseBody.setResult(String.valueOf(Constant.STATUS_CLIENT_ERROR));
        Map data = Maps.newHashMap();
        data.put("error",msg);
        responseBody.setData(data);
        return responseBody;
    }

    /**
     * 验证参数是否为空
     * @param params
     * @return
     */
    protected String validateParams(Map<String,Object> params){
        Iterator iter = params.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry)iter.next();
            if(entry.getValue() == null){
                LogUtils.logError(ObjectUtils.toString(entry.getKey())+" could not be null!");
                return ObjectUtils.toString(entry.getKey())+" could not be null!";
            }

        }
        return null;
    }
    /**
     * 校验用户是否存在
     * @param userId
     * @return
     */
    protected User validateUser(long userId){
        User user = userService.getUserById(userId);
        if(user != null){
            return user;
        }else{
            return null;
        }
    }


    protected boolean isExpired(){

        return true;
    }

    public boolean checkUser(Long uid){
        User user = userService.getUserById(uid);
        if(user == null){
            LogUtils.logError("invalid user!");
            return false;
        }else{
            return true;
        }
    }
}
