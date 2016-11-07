package com.letv.whatslive.model.constant;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangjian7 on 2015/7/16.
 */
public class UserConstants {
    //0:普通用户 1:认证用户 2.管理员用户
    public static final Integer role_normal = 0;
    public static final Integer role_identify = 1;
    public static final Integer role_admin = 2;

    //1:facebook  2:twitter  3:system
    public static final Integer userType_facebook = 1;
    public static final Integer userType_twitter = 2;
    public static final Integer userType_system = 3;
    public static final Integer userType_weibo = 4;
    public static final Integer userType_qq = 5;
    public static final Integer userType_weixin = 6;

    public static final Integer userStatus_offLine = 0;
    public static final Integer userStatus_onLine = 1;



    public static final Map userTypeMap = new HashMap (){
        {
            put(userType_facebook, "facebook");
            put(userType_twitter, "twitter");
            put(userType_system, "系统");
            put(userType_weibo, "微博");
            put(userType_qq, "QQ");
            put(userType_weixin, "微信");

        }
    };

}
