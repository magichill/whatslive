package com.letv.whatslive.model.constant;

import com.letv.whatslive.common.configuration.PropertyGetter;
import com.letv.whatslive.common.utils.PropertiesGetter;

/**
 * Created by wangjian7 on 2015/7/14.
 */
public class ProgramConstants {
    public static final Integer pStatus_offLine = 0;
    public static final Integer pStatus_onLine = 1;

    //优先
    public static final Integer PRIORITY_OFF = 0;
    public static final Integer PRIORITY_ON = 1;

    //推荐
    public static final Integer RECOMMEND_OFF = 0;
    public static final Integer RECOMMEND_ON = 1;

    //置顶
    public static final Integer TOP_ON = 1;

    /**
     * 视频类型：0：推荐预约，1：直播，2：录播，3：预约
     */
    public static final Integer pType_order_recommend = 0;
    public static final Integer pType_live = 1;
    public static final Integer pType_end = 3;
    public static final Integer pType_order = 2;

    public static final String UUID = PropertyGetter.getString("letv.cloud.uuid","");
    public static final String SHARE_URL = PropertyGetter.getString("share.url","");

    public static final String LIVE_CLOUD_URL = PropertyGetter.getString("letv.cloud.live","");
    public static final String LIVE_CLOUD_USER_ID = PropertyGetter.getString("letv.cloud.user.id","");
    public static final String LIVE_CLOUD_USER_SECRETE = PropertyGetter.getString("letv.cloud.user.secret","");
    /**
     * 默认封面地址
     */
    public static final String P_PIC = "http://g3.letv.cn/vod/v1/MTM1LzM3LzU3L2xldHYtaXR2Mi8wL3N0YXRpY2ZpbGUvaW1ncy8yMDE1MDUwNS9jYjc3NjEwMDkzNTM2YTJhNjU1ZTEzOWI2MzdkY2U5Ni5wbmc=?b=123456&amp;platid=5&amp;splatid=500";

}
