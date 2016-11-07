package com.letv.whatslive.model.mysql.system;

import java.util.Date;
import java.util.Set;

public class SysUser {

    private Integer id;
    private String loginName;
    private String pazzword;
    private String userName;
    private Integer isEffective;
    private Date createdTime;
    private Date updatedTime;

    //页面显示属性
    private Set<SysUserRoleRelation> sysUserRoleRelationList;

    public Set<SysUserRoleRelation> getSysUserRoleRelationList() {
        return sysUserRoleRelationList;
    }

    public void setSysUserRoleRelationList(Set<SysUserRoleRelation> sysUserRoleRelationList) {
        this.sysUserRoleRelationList = sysUserRoleRelationList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPazzword() {
        return pazzword;
    }

    public void setPazzword(String pazzword) {
        this.pazzword = pazzword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getIsEffective() {
        return isEffective;
    }

    public void setIsEffective(Integer isEffective) {
        this.isEffective = isEffective;
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