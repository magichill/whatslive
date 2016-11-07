package com.letv.whatslive.model.push.message.pass;

import com.alibaba.fastjson.JSON;
import com.letv.whatslive.model.push.message.AbstractPushMessge;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 透传消息
 * Created by wangjian7 on 2015/8/14.
 */
@Setter
@Getter
public class PassThroughMessage  extends AbstractPushMessge {
    private int areaCode;
    private String msg;
    private long expirydate;
    private int isActivate;
    private int isOnDeskTop;
    private int isSound;
    private int isVibrate;
    private long msgid;
    private int msgtype;
    private String picUrl;
    private long resid;
    private String title;
    private int type;
    private long id;

    public PassThroughMessage(String title){
        this.title = title;
    }

    public String toJsonString(){
        return JSON.toJSONString(this);
    }

}
