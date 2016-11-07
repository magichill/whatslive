package com.letv.whatslive.web.service.system;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.model.mysql.system.SysFunction;
import com.letv.whatslive.mysql.mybatis.system.SysFunctionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Transactional(readOnly = true)
public class SysFunctionService {
    private static final Logger logger = LoggerFactory.getLogger(SysFunctionService.class);

    @Autowired
    private SysFunctionDao sysFunctionDao;



    public List<SysFunction> getfunctionList(Map<String,Object> map,Integer start,Integer limit){
        Map<String,Object> params = Maps.newHashMap();
        if(map!=null){
            params.putAll(map);
        }
        params.put("start",start);
        params.put("limit",limit);
        return sysFunctionDao.getFunctionList(params);
    }

    /**
     * 不分页
     * @param map
     * @param start
     * @param limit
     * @return
     */
    public List<SysFunction> getfunctionListNoPage(Map<String,Object> map,Integer start,Integer limit){
        Map<String,Object> params = Maps.newHashMap();
        if(map!=null){
            params.putAll(map);
        }
        //        params.put("start",start);
//        params.put("limit",limit);
        return sysFunctionDao.getFunctionList(params);
    }

    public Integer countFunction(Map<String,Object> map){
        return sysFunctionDao.countFunction(map);
    }


    public List<SysFunction> getFunctionListByParentId(Integer parentFuncId){
        Map<String, Object> params = Maps.newHashMap();
        params.put("parentFuncId", parentFuncId);
        return sysFunctionDao.getList(params);
    }

    public SysFunction getFuncById(Integer id){
        return sysFunctionDao.getById(id);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<SysFunction> getParentFunctionListByFuncId(Integer funcId){
        List<SysFunction> parentFuncList = Lists.newArrayList();
        SysFunction func = sysFunctionDao.getById(funcId);
        parentFuncList.add(func);

        Integer parentFuncId = func.getParentFuncId();
        //最多循环十次，防止数据错误导致死循环
        int i = 0;
        while (i < 10 && parentFuncId != null && parentFuncId > 0){
            SysFunction parentFunc = sysFunctionDao.getById(parentFuncId);
            parentFuncList.add(parentFunc);
            parentFuncId = parentFunc.getParentFuncId();
            i ++;
        }
        return parentFuncList;
    }
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void insertLm(SysFunction func) {
        //父节点默认是0
        func.setParentFuncId(0);
        //非叶子节点0
        func.setIsLeaf(0);
        func.setCreatedTime(new Date());
        sysFunctionDao.insert(func);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void insert(SysFunction func) {
        func.setCreatedTime(new Date());
        sysFunctionDao.insert(func);
    }
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void updateSysFunction(SysFunction func) {

        func.setUpdatedTime(new Date());
        sysFunctionDao.updateSysFunction(func);

    }
    public Integer maxOrder(Integer parentFuncId){
        return sysFunctionDao.maxOrder(parentFuncId)==null?0:sysFunctionDao.maxOrder(parentFuncId);
    }

}
