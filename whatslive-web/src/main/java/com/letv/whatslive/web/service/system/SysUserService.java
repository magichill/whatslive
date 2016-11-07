package com.letv.whatslive.web.service.system;

import com.google.common.collect.Maps;
import com.letv.whatslive.model.mysql.system.SysFunction;
import com.letv.whatslive.model.mysql.system.SysUser;
import com.letv.whatslive.model.mysql.system.SysUserRoleRelation;
import com.letv.whatslive.mysql.mybatis.system.SysUserDao;
import com.letv.whatslive.mysql.mybatis.system.SysUserRoleRelationDao;
import org.apache.commons.lang3.StringUtils;
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
public class SysUserService {

    private static final Logger logger = LoggerFactory.getLogger(SysUserService.class);

    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private SysUserRoleRelationDao sysUserRoleRelationDao;
    @Autowired
    private SysFunctionService sysFunctionService;

    public SysUser getEffectiveUserByLoginName(String loginName) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("isEffective", 1);
        params.put("loginName", loginName);
        return sysUserDao.get(params);
    }

    /**
     * 判断节点是否有权限，支持各类型节点（递归实现） *注意：service中递归需要关闭事务*
     *
     * @param userId
     * @param funcId
     * @return
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public boolean hasFuncRight(Integer userId, Integer funcId) {
        SysFunction currentFunc = sysFunctionService.getFuncById(funcId);
        if (currentFunc == null) {
            return false;
        }
        if (currentFunc.getIsLeaf() != null && currentFunc.getIsLeaf() == 1) {
            if (this.hasUserFuncRelation(userId, currentFunc.getId())) {
                return true;
            } else {
                return false;
            }
        }

        List<SysFunction> subFuncList = sysFunctionService.getFunctionListByParentId(funcId);
        if (subFuncList == null) {
            return false;
        }
        for (SysFunction subFunc : subFuncList) {
            if (this.hasFuncRight(userId, subFunc.getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasUserFuncRelation(Integer userId, Integer funcId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("funcId", funcId);
        int relCount = sysUserDao.countUserFuncRelation(params);
        if (relCount > 0) {
            return true;
        }
        return false;
    }

    public Integer getMaxId(){
        return sysUserDao.getMaxId();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void insertSysUser(SysUser sysUser, List<SysUserRoleRelation> userRoleList) {
        if (sysUser == null) {
            logger.warn("要新增的用户为空！");
        }
        // 新增用户
        sysUser.setCreatedTime(new Date());
        sysUserDao.insert(sysUser);
        // 新增角色
        if (userRoleList == null || userRoleList.isEmpty()) {
            return;
        }
        for (SysUserRoleRelation userRole : userRoleList) {
            userRole.setUserId(sysUser.getId());
            sysUserRoleRelationDao.insert(userRole);
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void updateSysUser(SysUser sysUser, List<SysUserRoleRelation> userRoleList) {
        if (sysUser == null || sysUser.getId() == null) {
            logger.warn("要修改的用户为空！");
        }
        // 修改用户
        sysUser.setUpdatedTime(new Date());
        sysUserDao.update(sysUser);
        if (userRoleList == null || userRoleList.isEmpty()) {
            return;
        }
        // 删除角色
        sysUserRoleRelationDao.deleteByUserId(sysUser.getId());
        // 新增角色
        for (SysUserRoleRelation userRole : userRoleList) {
            userRole.setUserId(sysUser.getId());
            sysUserRoleRelationDao.insert(userRole);
        }
    }

    public boolean exsitSysUser(String loginName) {
        if (StringUtils.isBlank(loginName)) {
            return false;
        }
        return sysUserDao.checkSysUser(loginName) > 0;
    }

    public Integer countSysUser(String searchName) {
        Map<String, Object> params = Maps.newHashMap();
        if (StringUtils.isNotBlank(searchName)) {
            params.put("searchName", searchName);
        }
        return sysUserDao.countSysUser(params);
    }

    public List<SysUser> getSysUserList(String searchName, Integer start, Integer limit) {
        Map<String, Object> params = Maps.newHashMap();
        if (StringUtils.isNotBlank(searchName)) {
            params.put("searchName", searchName);
        }
        params.put("start", start);
        params.put("limit", limit);
        return sysUserDao.getSysUserList(params);
    }


    public SysUser getSysUserById(Integer id){
        if(id==null){
            return null;
        }
        return sysUserDao.getSysUserById(id);
    }


}
