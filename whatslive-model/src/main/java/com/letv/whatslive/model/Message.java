package com.letv.whatslive.model;

import com.mongodb.DBRef;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gaoshan on 15-8-6.
 */
@Getter
@Setter
public class Message {

    private Long id;
    private Long senderId;
    private DBRef sender;  //消息发送者id，若为系统消息则为0
    private String content;
    private Integer type; //1:系统消息  2:订阅（关注）消息
    private Integer status; // 1:可用 0:不可用
    private Long createTime;
    private Long updateTime;

}
