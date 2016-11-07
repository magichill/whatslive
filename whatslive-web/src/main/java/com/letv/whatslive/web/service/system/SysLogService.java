package com.letv.whatslive.web.service.system;

import com.google.common.collect.Maps;
import com.letv.whatslive.model.mysql.system.SysLog;
import com.letv.whatslive.mysql.mybatis.system.SysLogDao;
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
public class SysLogService {
    private static final Logger logger = LoggerFactory.getLogger(SysLogService.class);

    @Autowired
    private SysLogDao sysLogDao;

    /**
     * 保存操作日志
     *opType:业务类型
     *opObjectid:操作对象ID
     *opBefore:修改前内容
     *opAfter:修改后内容
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void saveSysLog(Integer userId,String clentIp,String sysLocalIP,String opType,
                           Integer opObjectid,String opBefore,String opAfter){

        SysLog sysLog = new SysLog();

        sysLog.setOpUserid(userId);
        sysLog.setClientIp(clentIp);
        sysLog.setSystemId(sysLocalIP);
        sysLog.setOpType(opType);
        sysLog.setOpObjectid(opObjectid);
        sysLog.setOpBefore(opBefore);
        sysLog.setOpAfter(opAfter);
        sysLog.setOpTime(new Date());
        sysLogDao.insert(sysLog);

    }

    /**
     * 保存操作日志
     * @param userId
     * @param clentIp
     * @param sysLocalIP
     * @param opType
     * @param opObjectid
     * @param opBefore
     * @param opAfter
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void saveSysLog2(Integer userId,String clentIp,String sysLocalIP,String opType,Integer opObjectid,String opBefore,String opAfter){
        Map params = Maps.newHashMap();

        params.put("opUserid",userId);
        params.put("clientIp",clentIp);
        params.put("systemId",sysLocalIP);
        params.put("opTime",new Date());
        params.put("opType",opType);
        params.put("opBefore",opBefore);
        params.put("opAfter",opAfter);
        params.put("opObjectid",opObjectid);

        sysLogDao.insertSysLog(params);

    }

    public Integer countSysLog(Map params) {

        return sysLogDao.countSysLog(params);
    }

    public List<SysLog> getSysLogList(Map map, Integer start, Integer limit) {

//        Map map = Maps.newHashMap();
        map.put("start", start);
        map.put("limit", limit);
        return sysLogDao.getSysLogList(map);

    }

    public SysLog getSysLogById(Integer id){
        return sysLogDao.getSysLogById(id);
    }


}
