package com.letv.whatslive.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 推送明细
 * Created by wangjian7 on 2015/8/18.
 */
@Setter
@Getter
public class PushDetail {
    private Long id;
    private Long pushId;
    private boolean result = true; //发送到百度云推送的结果
    private int plantform; //设备的系统，4：IOS，3：Android
    private String msgId; //推送消息的ID，第三方推送接口返回
    private Long sendTime; //unix timestamp，消息发送时间，定时消息为消息待发送时间。
    private Integer status; //消息处理状态 0：已发送 1：待发送 2：正在发送 3：失败
    private int messageType = 1; //设置消息类型,0表示透传消息,1表示通知,默认为0.

    private List<String> channels;
    private Long requestId; //发送请求ID
    private Integer errorCode; //错误代码
    private String errorMsg;
    private String exceptionClassName;
    private String timerId;
    private long startTime;
    private long endTime;
    private int retryNum;
    private String fileName; //channelId生成的文件

    private Long createTime;
    private Long updateTime;

    public PushDetail(){

    }

    private PushDetail(Long pushId, int plantform){
        this.pushId = pushId;
        this.plantform = plantform;
        this.setStartTime(System.currentTimeMillis());
        this.setCreateTime(this.getStartTime());
    }

    private PushDetail(Long pushId, int plantform ,int messageType){
        this.pushId = pushId;
        this.plantform = plantform;
        this.messageType = messageType;
        this.setStartTime(System.currentTimeMillis());
        this.setCreateTime(this.getStartTime());
    }

    private PushDetail(Long pushId, int plantform,int messageType, List<String> channels){
        this.pushId = pushId;
        this.plantform = plantform;
        this.setStartTime(System.currentTimeMillis());
        this.messageType = messageType;
        this.channels = channels;
        this.setCreateTime(this.getStartTime());
        this.setStatus(2);
    }

    public static PushDetail getAndroidPushDetail(Long pushId){
        return new PushDetail(pushId, 3);
    }
    public static PushDetail getAndroidPassThroughDetail(Long pushId){
        return new PushDetail(pushId, 3, 0);
    }
    public static PushDetail getIOSPushDetail(Long pushId){
        return new PushDetail(pushId, 4);
    }

    public static PushDetail getPushDetailByPlantform(Long pushId, int plantform){
        return new PushDetail(pushId, plantform);
    }

}
