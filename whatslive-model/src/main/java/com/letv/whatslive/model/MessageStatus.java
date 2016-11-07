package com.letv.whatslive.model;

import com.mongodb.DBRef;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gaoshan on 15-8-6.
 */
@Getter
@Setter
public class MessageStatus {

    private Long id;
    private DBRef mid;  //消息id
    private Long recId;  //消息接收用户id
    private Integer status;  //消息的阅读状态 0:未读  1:已读  2:删除
    private Long createTime;
    private Long updateTime;

}
