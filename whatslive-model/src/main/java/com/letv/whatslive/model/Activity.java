package com.letv.whatslive.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by gaoshan on 15-9-24.
 */
@Getter
@Setter
public class Activity {

    private Long id;
    private String title;
    private String tag;
    private String picture;
    private String url;
    private int status;
    private Long priority;
    private Long createTime;
    private String aDesc; //活动描述
    private int type;  // 1:视频列表 2：h5页面
    private int isRecommend; //是否推荐，0:表示不推荐，1表示推荐

    private String createTimeStr;
    private Long watchNum;
}
