package com.letv.whatslive.model.redis.chat.protocol;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by zoran on 15-7-24.
 */
public class LoginEvent {

    private String uid;//用户uid

    private String picture;//用户头像

    private String nickName;//用户昵称

    private long date;//精确到毫秒时间戳

    public LoginEvent(){

    }

    public LoginEvent(String uid, String picture, String nickName, long date) {
        this.uid = uid;
        this.picture = picture;
        this.nickName = nickName;
        this.date = date;
    }
    public LoginEvent(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        this.uid = jsonObject.getString("uid");
        this.picture = jsonObject.getString("picture");
        this.nickName = jsonObject.getString("nickName");
        this.date = jsonObject.getLongValue("date");
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override public String toString() {
        return JSON.toJSONString(this);
    }
}
