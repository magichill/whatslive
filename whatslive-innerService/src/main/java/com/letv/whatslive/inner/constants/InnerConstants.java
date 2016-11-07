package com.letv.whatslive.inner.constants;


import com.letv.whatslive.inner.utils.PropertyGetter;

public class InnerConstants {

    /**
     * cdn接口
     */
    public static final String API_CDN_HOST = PropertyGetter.getString("api.cdn.host", "http://mcache.oss.letv.com/");
    public static final String API_CDN_SYS = PropertyGetter.getString("api.cdn.sys", "whatslive");
    public static final String API_CDN_URL = API_CDN_HOST + "ext/addfile?sys=" + API_CDN_SYS + "&dstmd5=%s&sz=%s&durl=%s&dis=yes&outkey=%s";


    /**
     * CDN业务编码分隔符
     */
    public static final String CDN_CODE_SEPARATOR = "_";

    /**
     * CDN业务编码-用户上传头像图片
     */
    public static String CDN_CODE_USER_PIC = "userpicture";

    /**
     * CDN业务编码-直播截图
     */
    public static String CDN_CODE_PROGRAM_PIC = "thumnailvideo";

    /**
     * CDN业务编码-活动封面
     */
    public static String CDN_CODE_ACTIVITY_PIC = "activitypicture";

    /**
     * CDN业务编码-直播回放历史
     */
    public static String CDN_CODE_PROGRAM_REPLAY_LOG = "programreplayLog";

    public static final String PUSH_CHANNEL_FILE_PATH = PropertyGetter.getString("push.channel.file.path");


}
