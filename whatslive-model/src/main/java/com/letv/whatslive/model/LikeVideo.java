package com.letv.whatslive.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by gaoshan on 15-7-14.
 */
@Getter
@Setter
public class LikeVideo {

    private long id;
    private long userId;
    private long vid;
    private int status; //点赞状态 1：点赞 0：取消
    private long createTime;
    private long updateTime;
}
