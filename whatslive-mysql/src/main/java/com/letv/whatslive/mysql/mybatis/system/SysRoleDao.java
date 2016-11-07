package com.letv.whatslive.mysql.mybatis.system;

import com.letv.whatslive.mysql.mybatis.MyBatisRepository;
import com.letv.whatslive.model.mysql.system.SysRole;

import java.util.List;
import java.util.Map;


@MyBatisRepository
public interface SysRoleDao {

    SysRole getSysRoleById(Integer id);

    List<SysRole> getSysRoleList(Map<String, Object> params);

    Integer countSysRole(Map<String, Object> params);

    List<SysRole> getSysRoleListByParams(Map<String, Object> params);

    Integer existsRole(String roleName);

    void insertSysRole(SysRole sysRole);

    void updateSysRole(SysRole sysRole);

}
