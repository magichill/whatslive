package com.letv.whatslive.mysql.mybatis.system;

import com.letv.whatslive.mysql.mybatis.MyBatisRepository;
import com.letv.whatslive.model.mysql.system.SysUserRoleRelation;

import java.util.Set;


@MyBatisRepository
public interface SysUserRoleRelationDao {

    void insert(SysUserRoleRelation sysUserRoleRelation);

    void deleteByUserId(Integer userId);

    Set<SysUserRoleRelation> getSysUserRoleRelationSetByUserId(Integer id);

}
