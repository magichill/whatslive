package com.letv.whatslive.mysql.mybatis.system;

import com.letv.whatslive.mysql.mybatis.MyBatisRepository;
import com.letv.whatslive.model.mysql.system.SysLog;

import java.util.List;
import java.util.Map;

@MyBatisRepository
public interface SysLogDao {

    //该方法允许直接在service层调用
    void insertSysLog(Map params);

	void insert(SysLog log);
	
	void update(SysLog log);

    SysLog getSysLogById(Integer id);

    Integer countSysLog(Map<String, Object> params);

    List<SysLog> getSysLogList(Map<String, Object> params);
	
}