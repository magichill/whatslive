package com.letv.whatslive.common.utils;

/**
 * Created by gaoshan on 15-8-1.
 */
public class Constants {


    public static final String ENCODING = "UTF-8";

    //聊天消息默认事件
    public static final String DEFAULT_CHAT_EVENT = "message";

    //推送消息默认事件
    public static final String DEFAULT_PUSH_EVENT = "push";

    //推送通知默认事件
    public static final String DEFAULT_PUSH_MESSSAGE_EVENT = "pushMessage";

    //乐嗨推送服务在线用户key 后面需要拼上UID
    public static final String LIVE_PUSH_ONLINE_USER_KEY = "live_push_online_user_";

    //某直播的在线观看用户list，list结构只存uid，需要拼上直播ID
    public static final String LIVE_ONLINE_USER_LIST_KEY = "live_online_user_";

    //某个直播的赞数缓存，需要拼上直播ID
    public static final String LIVE_ONLINE_LIKE_KEY = "live_online_likenum_";

    //某个直播的评论数缓存，需要拼上直播ID
    public static final String LIVE_ONLINE_COMMENT_KEY = "live_online_commentnum_";

    //观看过当前直播的所有用户列表，使用set结构，需要拼上直播ID
    public static final String LIVE_ONLINE_TOTALUSER_KEY = "live_online_totaluserlist_";

    //某直播的真实在线观看用户list，list结构只存uid，需要拼上直播ID
    public static final String LIVE_ONLINE_REAL_USER_LIST_KEY = "live_online_real_user_";

    //某个直播的真实用户赞数缓存，需要拼上直播ID
    public static final String LIVE_ONLINE_REAL_LIKE_KEY = "live_online_real_likenum_";

    //某个直播的真实用户评论数缓存，需要拼上直播ID
    public static final String LIVE_ONLINE_REAL_COMMENT_KEY = "live_online_real_commentnum_";

    //观看过当前直播的所有真实用户列表，使用set结构，需要拼上直播ID
    public static final String LIVE_ONLINE_REAL_TOTALUSER_KEY = "live_online_real_totaluserlist_";

    //用户消息阅读状态
    public static final int MESSAGE_READ_NO = 0;
    public static final int MESSAGE_READ_ALREADY = 1;
    public static final int MESSAGE_DELETE = 2;

    //用户默认头像
    public static final String USER_ICON_DEFAULT = "http://g3.letv.cn/vod/v1/MTUyLzI1LzM1L2xldHYtaXR2Mi8wL3N0YXRpY2ZpbGUvbWF0ZXJpYWxzLzIwMTUwODEzL2ZlNmJmNmMxMWIwOWRkMjI1MWViMTY5MzYzZDMwNWIxLnBuZw==?b=123456&platid=5&splatid=500";

}
