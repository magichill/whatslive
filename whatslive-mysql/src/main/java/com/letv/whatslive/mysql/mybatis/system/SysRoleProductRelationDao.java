package com.letv.whatslive.mysql.mybatis.system;

import com.letv.whatslive.mysql.mybatis.MyBatisRepository;
import com.letv.whatslive.model.mysql.system.SysRoleProductRelation;

import java.util.List;
import java.util.Map;

@MyBatisRepository
public interface SysRoleProductRelationDao {

	void insert(SysRoleProductRelation roleProductRelation);
	
	void update(SysRoleProductRelation roleProductRelation);

    void delByRoleId(Integer roleId);
	
	SysRoleProductRelation getRoleProductRelationById(Integer id);

    Integer countRoleProductRelation(Map<String, Object> params);

    List<SysRoleProductRelation> getRoleProductRelationList(Map<String, Object> params);

    List<SysRoleProductRelation> getRoleProductRelationListByRoleId(Integer roleId);

}