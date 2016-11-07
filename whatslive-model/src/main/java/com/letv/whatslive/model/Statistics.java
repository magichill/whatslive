package com.letv.whatslive.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 直播统计
 * Created by wangjian7 on 15-10-15.
 */
@Getter
@Setter
public class Statistics {

    private Long startTime;
    private Long endTime;
    private Long liveCount; //直播统计数字
    private Long replayTransedCount; //录播已经生成回放
    private Long replayNotTransCount; //录播还未生成回放
    private Long liveTooShortCount; //直播时间过短不生成回放
    private Long offlineCount; //被下线的直播

    private String dateStr;
}
