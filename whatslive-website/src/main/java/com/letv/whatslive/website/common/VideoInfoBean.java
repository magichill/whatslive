package com.letv.whatslive.website.common;

import com.mongodb.DBRef;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by haojiayao on 2015/8/12.
 */

@Getter
@Setter
public class VideoInfoBean {

    // 视频ID
    private Long id;
    // 标题
    private String title;
    // 节目类型 1:直播 2:预约 3:录播
    private int programType;
    // 发布人ID
    private Long userId;
    // 状态 1:正常 0:结束
    private Integer status;
    // 地理位置
    private String location;
    // 封面
    private String coverPicture;
    // 点赞数
    private long likeNum;
    // 观众总数
    private long watchNum;
    // 在线观看人数
    private long watchOnlineUserNum;
    // 直播开始时间
    private Long startTime;
    // 直播结束时间
    private Long endTime;
    // 优先级
    private Long priority;
    // 昵称
    private String nickName;
    // 头像
    private String headPortrait;
    // 视频时长
    private String liveTimeLength;
    // 预约人数
    private Long orderNum;
    // 直播活动ID
    private String activityId;
    // 轮播台LiveID
    private String liveId;
    // 用户唯一标识，录播播放必须参数
    private String uuid;
    // 录播视频唯一标识，录播播放必须参数
    private String vuid;
    // 分享URL
    private String shareUrl;
    // 微信公众号的唯一标识
    private String appid;
    // 生成签名的时间戳
    private String signTimestamp;
    // 生成签名的随机串
    private String signNonceStr;
    // 签名
    private String signature;
    // debug
    private String debugFlg;
}
