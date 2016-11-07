package com.letv.whatslive.web.constant;


import com.letv.whatslive.web.util.configuration.PropertyGetter;

public class WebConstants {

    //不拦截的url
    public static final String[] SERVER_FILTER_EXCLUDES = PropertyGetter.getStringArray("server.filter.excludes");

    public static final String WHATSLIVE_SESSION_USERINFO_KEY = "WHATSLIVE_WEB_USERINFO";

    public static final String LOGIN_PAGE_URI = "/login";

    public static final String UPLOAD_SERVER_HOST = PropertyGetter.getString("upload.server.host");



    public static final String LOG_LOCAL_IP = PropertyGetter.getString("log.local.ip");

    /**
     * 上传根路径
     */
    public static final String UPLOAD_PATH_ROOT = PropertyGetter.getString("upload.path.root", "/letv/upload/staticfile/");
    /**
     * 图片路径
     */
    public static final String UPLOAD_PATH_IMGS = "imgs/";

    /**
     * 屏蔽关键词文件名
     */
    public static final String UPLOAD_KEYWORD = "keyword.txt";
    public static final String UPLOAD_KEYWORD_TMP = "keyword_tmp.txt";

    /**
     *回放日志文件
     */
    public static final String UPLOAD_ACTION_LOG = "actionLog/%s_%s.txt";

    /**
     * SSO 验证登陆用户HOST
     */
    public static final String SSO_CHECKUSER_HOST = PropertyGetter.getString("sso.checkUser.host");

    /**
     * SSO 验证登陆用户URL
     */
    public static final String SSO_CHECKUSER_URL = SSO_CHECKUSER_HOST + "check_user.php";

    /**
     * SSO 加解密API URL
     */
    public static final String SSO_TRANSCODE_URL = SSO_CHECKUSER_HOST + "transcode.php";

    /**
     * 网站标志符
     */
    public static final String KEY = "pf";
    /**
     * SSO 数字签名  请求参数与SIGN_KEY拼接
     */
    public static final String SIGN_KEY = "9ouj5dgfn765ttkkp";

    /**
     * 加密
     */
    public static final String SSO_ENCODE = "ENCODE";
    /**
     * 解密
     */
    public static final String SSO_DECODE = "DECODE";
    /**
     * 登录用户名
     */
    public static final String ADMIN = "admin";

    public static final String PAZZWORD = "123456";



    /**
     * 对于“可视化审核界面”需要根据是否有举报的视频在菜单后面显示是否有红点
     */
    public static final String noticeFunctionName = "可视化审核界面";
    public static final String noticeImgHTML = "<img src=\"/static/image/notice.png\"  alt=\"\"/>";

    public static final String LETV_LIVE_UUID = PropertyGetter.getString("letv.live.uuid");

    public static final String LETV_WEB_URL = PropertyGetter.getString("letv.web.url");



}
