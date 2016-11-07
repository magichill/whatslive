package com.letv.whatslive.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by gaoshan on 15-7-9.
 */
@Getter
@Setter
public class Report {

    private long rId;
    private long postId; //举报人Id
    private int type;  //举报类型
    private String content; //举报内容
    private long pId;  //被举报的直播Id
    private int status;
    private long createTime;
    private long updateTime;
}
