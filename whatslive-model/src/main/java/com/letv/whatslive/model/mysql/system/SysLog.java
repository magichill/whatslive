package com.letv.whatslive.model.mysql.system;

import java.util.Date;

public class SysLog {

    /**
     * 主键
     */
    private Integer id;
    /**
     * 操作用户ID
     */
    private Integer opUserid;

    /**
     * 操作用户名  add by:pangjie1@letv.com
     */
    private String opUserName;

    /**
     * 客户端IP
     */
    private String clientIp;
    /**
     * 应用服务器IP
     */
    private String systemId;
    /**
     * 操作时间
     */
    private Date opTime;
    /**
     * 操作类型
     */
    private String opType;
    /**
     * 修改前内容
     */
    private String opBefore;
    /**
     * 修改后内容
     */
    private String opAfter;
    /**
     * 操作对象ID
     */
    private Integer opObjectid;


    //-----显示用属性
    private String opTimes;

    public String getOpTimes() {
        return opTimes;
    }

    public void setOpTimes(String opTimes) {
        this.opTimes = opTimes;
    }

    public Integer getOpUserid() {
        return opUserid;
    }

    public void setOpUserid(Integer opUserid) {
        this.opUserid = opUserid;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClientIp() {
        return this.clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public Date getOpTime() {
        return this.opTime;
    }

    public void setOpTime(Date opTime) {
        this.opTime = opTime;
    }

    public String getOpType() {
        return this.opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public String getOpBefore() {
        return this.opBefore;
    }

    public void setOpBefore(String opBefore) {
        this.opBefore = opBefore;
    }

    public String getOpAfter() {
        return this.opAfter;
    }

    public void setOpAfter(String opAfter) {
        this.opAfter = opAfter;
    }

    public Integer getOpObjectid() {
        return this.opObjectid;
    }

    public void setOpObjectid(Integer opObjectid) {
        this.opObjectid = opObjectid;
    }

    public String getOpUserName() {
        return opUserName;
    }

    public void setOpUserName(String opUserName) {
        this.opUserName = opUserName;
    }
}
