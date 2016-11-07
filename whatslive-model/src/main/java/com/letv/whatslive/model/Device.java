package com.letv.whatslive.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by gaoshan on 15-7-21.
 */
@Getter
@Setter
public class Device {

    /**
     * 设备id==udid
     */
    private String id;

    /**
     * 设备关联用户Id
     */
    private Long userId;

    /**
     * 平台（1-ios，2-android）
     */
    private Integer platformId;

    /**
     *用户设备系统版本:操作系统+版本号（Android 4.2 或则 IOS 6.0）
     */
    private String  osVersion;

    /**
     * 设备型号
     */
    private String devModel;

    /**
     * 设备地址
     */
    private String addrMac;

    /**
     * 设备安装应用版本
     */
    private Integer editionId;

    /**
     * 渠道
     */
    private String corporationId;

    /**
     * 子渠道
     */
    private String subCoopId;

    /**
     * 手机Imei
     */
    private String imei;

    /**
     * imsi
     */
    private String imsi;

    /**
     * 设备推送token
     */
    private String devToken;

    /**
     * 设备状态?
     */
    private String devStatus;

    /**
     * 设备gpu类型
     */
    private String gpuModel;

    /**
     * 设备内存
     */
    private String memory;

    /**
     * 设备显存
     */
    private String gmem;

    /**
     * gpu核数
     */
    private String core;

    /**
     * 显卡类型
     */
    private String gcard;

    /**
     * 最近活跃时间
     */
    private Long lastActiveTime;

    /**
     * 最近活跃时间（用于页面显示）
     */
    private String lastActiveTimeStr;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 创建时间(用于页面显示)
     */
    private String createTimeStr;

    /**
     * 修改时间(用于页面显示)

     */
    private String updateTimeStr;
    /**
     * 修改时间

     */
    private Long updateTime;

    /**
     * 手机类型
     */
    private Integer type;

    /**
     * 百度推送Id
     */
    private String channelId;
}
