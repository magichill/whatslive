package com.letv.whatslive.web.service.system;

import com.google.common.collect.Maps;
import com.letv.whatslive.model.mysql.system.*;
import com.letv.whatslive.mysql.mybatis.system.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@Transactional(readOnly = true)
public class SysRoleService {
    private static final Logger logger = LoggerFactory.getLogger(SysRoleService.class);

    @Autowired
    private SysRoleDao sysRoleDao;
    @Autowired
    private SysUserRoleRelationDao sysUserRoleRelationDao;
    @Autowired
    private SysFunctionDao sysFunctionDao;
    @Autowired
    private SysRoleFunctionRelationDao sysRoleFunctionRelationDao;
    @Autowired
    private SysRoleProductRelationDao sysRoleProductRelationDao;

    public SysRole getSysRoleById(Integer id) {
        if (id == null) {
            return null;
        }
        return sysRoleDao.getSysRoleById(id);
    }

    public List<SysRole> getSysRoleList() {
        Map<String, Object> params = Maps.newHashMap();
        return sysRoleDao.getSysRoleList(params);
    }

    public Integer countSysRole(Map<String, Object> params) {
        return sysRoleDao.countSysRole(params);
    }

    public List<SysRole> getSysRoleListByParams(Map<String, Object> params, Integer start, Integer limit) {
        params.put("start", start);
        params.put("limit", limit);
        return sysRoleDao.getSysRoleListByParams(params);
    }


    public Set<SysUserRoleRelation> getSysUserRoleRelationSetByUserId(Integer userId) {
        if (userId == null) {
            return null;
        }
        return sysUserRoleRelationDao.getSysUserRoleRelationSetByUserId(userId);
    }

    public boolean existsRole(String roleName) {
        return sysRoleDao.existsRole(roleName) > 0;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void insertSysRole(SysRole sysRole) {
        sysRole.setCreatedTime(new Date());
        sysRoleDao.insertSysRole(sysRole);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void updateSysRole(SysRole sysRole) {
        sysRole.setUpdatedTime(new Date());
        sysRoleDao.updateSysRole(sysRole);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void insertRoleFuncRelation(Integer roleId, List<SysRoleFunctionRelation> relation) {
        sysRoleFunctionRelationDao.delByRoleId(roleId);
        for (int i = 0; i < relation.size(); i++) {
            sysRoleFunctionRelationDao.insert(relation.get(i));
        }

    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void insertRoleProductRelation(Integer roleId, List<SysRoleProductRelation> relation) {
        sysRoleProductRelationDao.delByRoleId(roleId);
        if (relation == null)
            return;
        for (int i = 0; i < relation.size(); i++) {
            sysRoleProductRelationDao.insert(relation.get(i));
        }
    }

    public List<SysRoleFunctionRelation> getFunctionIdByRoleId(Integer roleId) {
        List<SysRoleFunctionRelation> relation = null;
        if (roleId != null)
            relation = sysRoleFunctionRelationDao.getFunctionIdByRoleId(roleId);
        return relation;
    }

    public List<SysRoleProductRelation> getProdcutIdByRoleId(Integer roleId) {
        List<SysRoleProductRelation> relation = null;
        if (roleId != null)
            relation = sysRoleProductRelationDao.getRoleProductRelationListByRoleId(roleId);
        return relation;
    }


    private List<TreeVo> getChildTree(int parentId, List<SysRoleFunctionRelation> relation) {

        List<TreeVo> rtnList = new ArrayList<TreeVo>();

        List<SysFunction> list = sysFunctionDao.getByParentId(parentId);

        for (int i = 0; i < list.size(); i++) {
            SysFunction sysFunction = list.get(i);
            TreeVo treevo = new TreeVo();
            treevo.setTitle(sysFunction.getFuncName());
            treevo.setKey(sysFunction.getId().toString());
            treevo.setFolder(sysFunction.getIsLeaf() == 0);
            if (sysFunction.getIsLeaf() == 0) {
                Integer count = sysFunctionDao.countByParentId(sysFunction.getId());
                if (count == 0)
                    continue;
                else
                    treevo.setChildren(getChildTree(sysFunction.getId(), relation));
            }
            for (int j = 0; j < relation.size(); j++) {
                SysRoleFunctionRelation r = relation.get(j);
                if (r.getFuncId() == sysFunction.getId()) {
                    treevo.setSelect(true);
                    break;
                }
            }
            rtnList.add(treevo);

        }
        return rtnList;

    }

    public TreeVo getTreeList(Integer roleId) {

        List<SysRoleFunctionRelation> relation = null;
        if (roleId != null)
            relation = sysRoleFunctionRelationDao.getFunctionIdByRoleId(roleId);

        Integer nodeId = 0;//根节点ID
        TreeVo root = new TreeVo();
        root.setKey(nodeId.toString());
        root.setFolder(true);
        root.setTitle("资源树");

        root.setChildren(getChildTree(nodeId, relation));

        return root;
    }

}
