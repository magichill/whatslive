package com.letv.whatslive.model;

import com.mongodb.DBRef;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by gaoshan on 15-7-7.
 */

@Getter
@Setter
public class Program {

    private Long id;
    private String pName;
    private String pDesc;
    private int pType;  //节目类型 1:直播 2:预约 3:录播
    private Long userId;  //发布人id
    private DBRef userRef; //发布人
    private Integer status; //状态 1:正常 0:结束
    private String location; //直播发布的地理位置
    private String province;
    private String city;
    private String picture; //封面
    private long likeNum; //点赞数
    private long watchNum; //观看次数
    private long commentNum; //评论数

    private long realLikeNum; // 真实点赞数
    private long realWatchNum; // 真实观看人数
    private long realCommentNum; //真实评论数

    private Long startTime; //直播开始时间
    private Long endTime;  //直播结束时间
    private Long createTime;
    private Long updateTime;


    private Long orderNum; //预约人数
    private Long priority; //优先级
    private int isShow = 1; //0：表示不显示，1：表示显示，是否在推荐中显示，默认为显示。
    private String primaryPName; //原始标题

    private Integer from;    //直播发起设备类型 1：ios  2：android  默认为ios

    private Integer isCarousel; //是否是轮播台，1表示是，其他表示不是
    private String liveId; //轮播台LiveID
    private String activityId; //直播活动ID
    private String videoId; //录播视频ID
    private String vuid; //录播视频唯一标识，录播播放必须参数
    private List<Long> tagList;
    private Integer isContinue; //该直播是否可持续推流 1是，0否

    /**
     * 以下字段只用于页面显示，不在数据库中存储
     */
    private long watchOnlineUserNum; //在线观看人数，用于页面显示
    private String startTimeStr; //直播开始时间字符串，用于页面显示，显示格式"yyyy-MM-dd HH-mm-ss"
    private String createTimeStr; //创建时间字符串，用于页面显示，显示格式"yyyy-MM-dd HH-mm-ss"
    private String nickName; //发布者nickName，用于页面显示。
    private Integer userLevel; //发布者等级，用于页面显示。
    private long reportNum; //举报次数，用于页面显示
    private Long tagId;//用于页面显示
    private String tagValue;//用于页面显示
    private String actionLogUrl;

}
