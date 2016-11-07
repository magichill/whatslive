package com.letv.whatslive.model.mysql.system;


public class SysRoleProductRelation {

    /**
     * 权限ID
     */
    private Long id;
    /**
     * 角色ID
     */
    private Long roleId;
    /**
     * 产品线ID
     */
    private Long proId;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return this.roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getProId() {
        return this.proId;
    }

    public void setProId(Long proId) {
        this.proId = proId;
    }


}
