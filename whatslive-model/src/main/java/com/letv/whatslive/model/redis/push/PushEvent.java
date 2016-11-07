package com.letv.whatslive.model.redis.push;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


/**
 * Created by wangjian7 on 2015/8/31.
 */

public class PushEvent {

    private int action; //1.直播消息推送推送到所有预约用户
    private Long programId;

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public PushEvent() {
    }

    public PushEvent(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        this.action = jsonObject.getInteger("action");
        this.programId = jsonObject.getLong("programId");
    }

    @Override public String toString() {
        return JSON.toJSONString(this);
    }

    public String toLogString(){
        StringBuffer sb = new StringBuffer();
        sb.append("PushEvent{");
        sb.append("action=").append(action);
        sb.append(", programId=").append(programId);
        return sb.toString();
    }

}
