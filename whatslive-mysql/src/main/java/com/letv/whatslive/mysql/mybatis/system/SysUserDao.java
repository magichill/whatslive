package com.letv.whatslive.mysql.mybatis.system;

import com.letv.whatslive.mysql.mybatis.MyBatisRepository;
import com.letv.whatslive.model.mysql.system.SysUser;

import java.util.List;
import java.util.Map;


@MyBatisRepository
public interface SysUserDao {

    void insert(SysUser user);

    void update(SysUser user);

    SysUser get(Map<String, Object> parameters);

    int countByLoginNameAndPassword(Map<String, Object> parameters);

    int countUserFuncRelation(Map<String, Object> parameters);

    Integer checkSysUser(String loginName);

    Integer countSysUser(Map<String, Object> params);

    List<SysUser> getSysUserList(Map<String, Object> params);

    SysUser getSysUserById(Integer id);

    Integer getMaxId();


}
