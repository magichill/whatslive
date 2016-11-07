package com.letv.whatslive.mysql.mybatis.system;

import com.letv.whatslive.model.mysql.system.SysFunction;
import com.letv.whatslive.mysql.mybatis.MyBatisRepository;

import java.util.List;
import java.util.Map;


@MyBatisRepository
public interface SysFunctionDao {

    SysFunction getById(Integer id);

    List<SysFunction> getList(Map<String, Object> parameters);

    List<SysFunction> getFunctionList(Map<String, Object> params);

    Integer countFunction(Map<String, Object> params);

    void insert(SysFunction func);

    void updateSysFunction(SysFunction func);

    Integer maxOrder(Integer parentFuncId);

    Integer countByParentId(Integer parentFuncId);

    List<SysFunction> getByParentId(Integer parentFuncId);


}
