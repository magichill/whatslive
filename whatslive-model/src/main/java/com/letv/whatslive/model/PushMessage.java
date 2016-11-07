package com.letv.whatslive.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by wangjian7 on 2015/7/28.
 */
@Getter
@Setter
public class PushMessage {
    private Long id;
    private String content;
    private String createUser;
    private Long createTime;
    private Long sendTime;
    private Integer sendType; //-1表示所有用户，0.表示普通用户，1表示认证用户
    private Integer status; //0.未发送；1.发送中；2发送成功；3.发送失败


    private String createTimeStr; //创建时间字符串，用于页面显示，显示格式"yyyy-MM-dd HH-mm-ss"
    private String sendTimeStr; //发送时间字符串，用于页面显示，显示格式"yyyy-MM-dd HH-mm-ss"
}
