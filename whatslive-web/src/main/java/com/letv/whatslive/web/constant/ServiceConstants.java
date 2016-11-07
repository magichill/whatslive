package com.letv.whatslive.web.constant;


import com.letv.whatslive.web.util.configuration.PropertyGetter;

public class ServiceConstants {

    /**
     * 上传业务编码-用户上传头像图片的前缀
     */
    public static String UPLOAD_CODE_USER_PIC = "user/picture/" ;

    /**
     * 上传业务编码-直播封面图片的前缀
     */
    public static String UPLOAD_CODE_PROGRAM_PIC = "thumnail/video/" ;

    /**
     * 上传业务编码-活动封面图片的前缀
     */
    public static String UPLOAD_CODE_ACTIVITY_PIC = "activity/picture/" ;

    /**
     * 上传业务编码-活动回放
     */
    public static String UPLOAD_CODE_PROGRAM_REPLAY_LOG = "program/replayLog/" ;



    /**
     * aws账号信息
     */
    public static String  AWS_S3_ACCESSKEYID = PropertyGetter.getString("aws.s3.accesskeyid");
    public static final String AWS_S3_SECRETKEY = PropertyGetter.getString("aws.s3.secretkey");

    /**
     * aws S3域名
     */
    public static final String AWS_S3_URL = PropertyGetter.getString("aws.s3.url");

    /**
     * aws S3桶名称
     */
    public static final String AWS_S3_BUCKETNAME = PropertyGetter.getString("aws.s3.bucketname");

    /**
     * aws S3 url前缀
     */
    public static final String AWS_S3_URL_PREX = AWS_S3_URL + AWS_S3_BUCKETNAME + "/";

    /**
     * redis中聊天室topic名称
     */
    public static final String REDIS_TOPIC_NAME_CHATROOM = "chatroom";

    /**
     * reids中强制下线聊天室的操作类型
     */
    public static final int REDIS_TOPIC_OPERATE_FORCE_OFFLINE = 7;

    /**
     * cdn接口
     */
    public static final String API_CDN_HOST = PropertyGetter.getString("api.cdn.host", "http://mcache.oss.letv.com/");
    public static final String API_CDN_SYS = PropertyGetter.getString("api.cdn.sys", "lepai");
    public static final String API_CDN_URL = API_CDN_HOST + "ext/addfile?sys=" + API_CDN_SYS + "&dstmd5=%s&sz=%s&durl=%s&dis=yes&outkey=%s";

    /**
     * 文件上传使用的服务
     * AWS_S3:Amazon_S3服务
     * LETV_CDN:Letv 云盘服务
     */
    public static final String FILE_UPLOAD_TYPE =  PropertyGetter.getString("file.upload.type");
    public static final String FILE_UPLOAD_TYPE_AWS_S3 = "AWS_S3";
    public static final String FILE_UPLOAD_TYPE_LETV_CDN ="LETV_CDN";

    /**
     * 推送配置
     *
     */
    private static final String PUSH_HOST = PropertyGetter.getString("push.host");
    public static final String PUSH_SEND_MESSAGE_TO_ALL = PUSH_HOST+"pushToAll";
    public static final String PUSH_SEND_MESSAGE_TO_TAG = PUSH_HOST+"pushToTag";
    public static final String PUSH_CREATE_TAG = PUSH_HOST+"createTag";
    public static final String PUSH_ADD_DEVICES_TO_TAG = PUSH_HOST+"addDevicesToTag";
    public static final String PUSH_DEL_DEVICES_FROM_TAG = PUSH_HOST+"delDevicesFromTag";

}
