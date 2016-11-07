package com.letv.whatslive.common.http;

/**
 * Created by zoran on 14-9-15.
 */
public class RequestHeader {

    private String apiLevel;

    private String userName;

    private String encode;

    private String corporationId;

    private String editionId;

    private String subCoopId;

    private String platformId;

    private String operator;

    private String deviceToken;

    private String model;

    private String locale;

    private String udid;

    private String imei;

    private String imsi;

    private String timeCost;

    private String time;

    private String userId;

    private String sso_tk;

    private String from;

    private String channelId;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getEditionId() {
        return editionId;
    }

    public void setEditionId(String editionId) {
        this.editionId = editionId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getApiLevel() {
        return apiLevel;
    }

    public void setApiLevel(String apiLevel) {
        this.apiLevel = apiLevel;
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public String getCorporationId() {
        return corporationId;
    }

    public void setCorporationId(String corporationId) {
        this.corporationId = corporationId;
    }

    public String getSubCoopId() {
        return subCoopId;
    }

    public void setSubCoopId(String subCoopId) {
        this.subCoopId = subCoopId;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(String timeCost) {
        this.timeCost = timeCost;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getSso_tk() {
        return sso_tk;
    }

    public void setSso_tk(String sso_tk) {
        this.sso_tk = sso_tk;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[apiLevel:"+this.getApiLevel()+"],");
        sb.append("[imei:"+this.getImei()+"],");
        sb.append("[imsi:"+this.getImsi()+"],");
        sb.append("[deviceToken:"+this.getDeviceToken()+"],");
        sb.append("[corporationId:"+this.getCorporationId()+"],");
        sb.append("[editionId:"+this.getEditionId()+"],");
        sb.append("[encode:"+this.getEncode()+"],");
        sb.append("[locale:"+this.getLocale()+"],");
        String model  = this.getModel().replaceAll(" ", "");
        sb.append("[model:"+model+"],");
        sb.append("[platform:"+this.getPlatformId()+"],");
        sb.append("[operator:"+this.getOperator()+"],");
        sb.append("[subcoopId:"+this.getSubCoopId()+"],");
        sb.append("[udid:"+this.getUdid()+"],");
        sb.append("[userId:"+this.getUserId()+"],");
        sb.append("[userName:"+this.getUserName()+"],");
        sb.append("[time:"+this.getTime()+"],");
        sb.append("[timeCost:"+this.getTimeCost()+"],");
        sb.append("[sso_tk:"+this.getSso_tk()+"],");
        sb.append("[from:"+this.getFrom()+"],");
        return sb.toString();
    }
}
