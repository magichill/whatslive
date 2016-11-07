package com.letv.whatslive.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by gaoshan on 15-7-28.
 * 第三方好友对象
 */
@Getter
@Setter
public class ThirdFriend {

    private Long userId;  //id为0，表示第三方用户未注册，可邀请
    private String thirdId;
    private String nickName;
    private String introduce;
    private String userIcon;
    private Integer role;  //用户认证
    private Integer status; //0：未关注 1：粉丝 2：互粉
}
