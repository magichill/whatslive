package com.letv.whatslive.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by gaoshan on 15-7-9.
 */
@Getter
@Setter
public class Friend {

    private Long id;
    private User follower;  //关注人 用户的id
    private User user;   //被关注人
    private Integer status;  //关注状态 1:关注 0:未关注
    private Long createTime;
    private Long updateTime;
    private Friend relatedFriend; //关联关系friend对象

}
