package com.letv.whatslive.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by wangjian7 on 15-10-13.
 */
@Getter
@Setter
public class ActionLog {

//    private int version;  //协议版本 初始值是 1
    private Long id; //_id
    private String uid; // 用户ID
    private String roomId; // 直播ID
    private int action; // 参考ChatEvent.java
    private int type;// 参考ChatEvent.java
    private String content;//消息正文
    private String voice;//语音消息
    private long date; //日期  精确到毫秒

}
