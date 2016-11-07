package com.letv.whatslive.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by gaoshan on 15-6-30.
 */
@Getter
@Setter
public class User {

    private Long userId;
    private String userName;
    private String nickName;
    private Integer sex;
    private Integer userType; //1:facebook  2:twitter  3:system  4:微博 5:QQ 6:微信
    private String picture;
    private String email;
    private String phone;
    private String mobile;
    private Integer userStatus; //用户状态 1:正常 0:禁用
    private String address;
    private Long createTime;
    private Long updateTime;
    private Long lastLoginTime;
    private String accessToken; //第三方登陆的token

    private String thirdId;  //第三方账号Id
    private String sinaId;
    private String qqId;
    private String weixinId;
    private Integer sinaBind;
    private Integer qqBind;
    private Integer weixinBind;
//    private String bindId;   //绑定的第三方账号Id
//    private Integer bindType;  //绑定的第三方平台
//    private String bindToken; //绑定的第三方登陆token

    private String tokenSecret;  //twitter登陆的secret

    private String introduce; //自我介绍
    private Integer role; //用户角色，0普通用户，1认证用户，2管理员
    private Integer level; //用户等级,0为没有等级，1-5为不同的级别
    private String createTimeStr; //创建时间字符串，用于页面显示，显示格式"yyyy-MM-dd HH-mm-ss"
    private String lastLoginTimeStr; //最后登录时间字符串，用于页面显示，显示格式"yyyy-MM-dd HH-mm-ss"
    private String thirdBindUser; //绑定的第三方列表用户信息，用于页面显示
    private long broadCastNum; //直播次数
    private Long likeNum; //点赞次数
    private List<String> devIdList;
    private String ssoTk;





}
