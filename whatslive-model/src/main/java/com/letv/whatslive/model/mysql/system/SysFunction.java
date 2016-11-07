package com.letv.whatslive.model.mysql.system;

import java.util.Date;

public class SysFunction {

    private Integer id;
    private String funcName;
    private Integer parentFuncId;
    private Integer isLeaf;
    private String actionUrl;
    private String iconUrl;
    private Integer funcOrder;
    private Date createdTime;
    private Date updatedTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public Integer getParentFuncId() {
        return parentFuncId;
    }

    public void setParentFuncId(Integer parentFuncId) {
        this.parentFuncId = parentFuncId;
    }

    public Integer getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(Integer isLeaf) {
        this.isLeaf = isLeaf;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public Integer getFuncOrder() {
        return funcOrder;
    }

    public void setFuncOrder(Integer funcOrder) {
        this.funcOrder = funcOrder;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

}
