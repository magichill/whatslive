package com.letv.whatslive.mysql.mybatis.system;

import com.letv.whatslive.mysql.mybatis.MyBatisRepository;
import com.letv.whatslive.model.mysql.system.SysRoleFunctionRelation;

import java.util.List;


@MyBatisRepository
public interface SysRoleFunctionRelationDao {

    List<SysRoleFunctionRelation> getFunctionIdByRoleId(Integer roleId);

    void delByRoleId(Integer roleId);

    void insert(SysRoleFunctionRelation sysRoleFunctionRelation);
}
