package com.letv.whatslive.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by gaoshan on 15-7-9.
 */

@Getter
@Setter
public class Comment {

    private Long cId;
    private Long postId; //发表人Id
    private String content;  //评论内容
    private Long pId;  //评论直播的Id
    private int status; //评论状态 1:正常 0:删除
    private Long createTime;
    private Long updateTime;

}
