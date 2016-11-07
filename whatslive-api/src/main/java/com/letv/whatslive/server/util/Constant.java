package com.letv.whatslive.server.util;

import com.letv.whatslive.common.utils.PropertiesGetter;
import com.letv.whatslive.model.constant.ProgramConstants;

/**
 * Created by zoran on 14-9-16.
 */
public class Constant {


    public static final int STATUS_SERVER_ERROR = 500;

    public static final int STATUS_CLIENT_ERROR = 601;

    public static final int STATUS_OK = 200;

    public static final int STATUS_UNAUTHORIZED  = 401;

    public static final int LOGIN_TYPE_FACEBOOK = 1;

    public static final int LOGIN_TYPE_TWITTER = 2;

    public static final int LOGIN_TYPE_WEIBO = 4;

    public static final int LOGIN_TYPE_QQ = 4;

    public static final int LOGIN_TYPE_WEIXIN = 6;

    public static final int LOGIN_TYPE_SYSTEM = 3;

    public static final int INDEX_RECOMMEND = 1;  //首页推荐
    public static final int INDEX_FOLLOW = 2;     //首页订阅
    public static final int INDEX_SUBSCRIBE = 3;  //首页预约

    public static final int VIDEO_RECOMMAND = 0; //运营预约类型
    public static final int VIDEO_LIVE = 1;      //直播类型
    public static final int VIDEO_SUBSCRIBE = 2; //预约类型
    public static final int VIDEO_RECORD = 3;    //录播类型

    /**
     * 默认视频图片
     */
    public static final String VIDEO_DEFAULT_PIC = "http://g3.letv.cn/vod/v1/MTM1LzM3LzU3L2xldHYtaXR2Mi8wL3N0YXRpY2ZpbGUvaW1ncy8yMDE1MDUwNS9jYjc3NjEwMDkzNTM2YTJhNjU1ZTEzOWI2MzdkY2U5Ni5wbmc=?b=123456&platid=5&splatid=500";


    public static final String PUSH_STREAM_URL = "rtmp://pushlive.lepai.letv.com/whatslive/";
    public static final String PULL_STREAM_URL = "rtmp://pulllive.lepai.letv.com/whatslive/";

    public static final String CHAT_SERVER = PropertiesGetter.getValue("chat.server");
    public static final String LIVE_CLOUD_URL = PropertiesGetter.getValue("letv.cloud.live");
    public static final String LIVE_CLOUD_USER_ID = PropertiesGetter.getValue("letv.cloud.user.id");
    public static final String LIVE_CLOUD_USER_SECRETE = PropertiesGetter.getValue("letv.cloud.user.secret");

    //点播api接口
    public static final String LETV_CLOUD_OPEN_URL = PropertiesGetter.getValue("letv.cloud.openurl");

    public static final String AMAZON_URL = "https://s3.amazonaws.com/whatslivetest/";

    public static final String MESSAGE_SUBSCRIBE = "预约了您的直播";
    public static final String MESSAGE_FOCUS = "刚刚订阅了您";

    public static final Long REMIND_TIME = 10*60*1000l;

    public static final String WEIBO_INVITE_TEXT = ",我在用乐嗨直播！你也试试看，能发现无限精彩噢！http://lehi.letv.com";
    public static final String WEIBO_SHARE_TEXT = "我正在@乐嗨直播";
//    public static final String SHARE_URL = PropertiesGetter.getValue("share.url");


    /**
     * 分享本人视频的文案
     */
    public static final String FACEBOOK_SHARE_LIVE_TEXT = "【LIVE NOW】#WhatsLive ${1} ${2} ${3}";  //facebook直播分享文案1.直播标题 2。直播链接 3.图片
    public static final String FACEBOOK_SHARE_PLAN_TEXT = "At scheduled time:${1} on #WhatsLIVE ${2} ${3}";  //facebook预约分享文案
    public static final String FACEBOOK_SHARE_DEMAND_TEXT = "Watch me on#WhatsLIVE"; //facebook点播分享文案

    public static final String TWITTER_SHARE_LIVE_TEXT = "【LIVE NOW】#WhatsLive ${1} ${2}";  //twitter直播分享文案
    public static final String TWITTER_SHARE_PLAN_TEXT = "At scheduled time:${1} on #WhatsLIVE ${2} ${3}";  //twitter预约分享文案
    public static final String TWITTER_SHARE_DEMAND_TEXT = "Watch me on#WhatsLIVE";  //twitter点播分享文案


//    public static final String USER_UNIQUE = "cd5f283012";  //云平台用户唯一标识
    public static final String USER_UNIQUE = PropertiesGetter.getValue("letv.cloud.uuid");  //云平台用户唯一标识

    //错误码
    public static final String OTHER_ERROR_CODE = "E100";  //未知错误
    public static final String SERVER_ERROR_CODE = "E500"; //服务器异常错误

    public static final String PLAN_STARTTIME_ERROR_CODE = "E001";  //开始时间不合法
    public static final String PLAN_ENDTIME_ERROR_CODE = "E002";  //结束时间不合法
    public static final String PLAN_REPEAT_ERROR_CODE = "E003";  //预约时间重复

    public static final String LIVE_NOT_END_ERROR_CODE = "E004";  //直播流未结束
    public static final String LIVE_NULL_ERROR_CODE = "E005";  //直播不存在或已下线

    public static final String SUBSCRIBE_ERROR_CODE = "E006"; //预约失败
    public static final String CREATE_PROGRAM_ERROR_CODE = "E007"; //创建直播失败

    public static final String PARAMS_ERROR_CODE = "E008"; //参数不合法
    public static final String USER_INVALID_ERROR_CODE = "E009"; //用户不存在或被禁止

    public static final String PUSH_URL_ERROR_CODE = "E010"; //获取推流地址失败
    public static final String LOGIN_ERROR_CODE = "E011"; //登陆失败
    public static final String WEIBO_ERROR_CODE = "E012"; //获取微博好友失败
    public static final String SHARE_ERROR_CODE = "E013"; //分享失败
    public static final String GET_TOKEN_ERROR_CODE = "E014"; //获取上传token失败
    public static final String COMPRESS_PIC_ERROR_CODE = "E015"; //压缩图片失败
    public static final String REPORT_ERROR_CODE = "E016"; //举报视频失败
    public static final String MESSAGE_ERROR_CODE = "E017"; //获取个人消息失败
    public static final String REPLAY_ERROR_CODE = "E018"; //获取录播列表失败

    /**
     * 云计算推流回调常量
     */
    public static final String STREAM_START_CODE = "101"; //推流开始
    public static final String STREAM_INTERUPT_CODE = "102";  //推流中断
    public static final String ACTIVITY_START_CODE = "001";  //活动开始
    public static final String ACTIVITY_END_CODE = "002";  //活动结束


}
