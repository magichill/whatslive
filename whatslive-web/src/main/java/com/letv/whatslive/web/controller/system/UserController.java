package com.letv.whatslive.web.controller.system;

import com.google.common.collect.Lists;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.web.constant.ModelConstants;
import com.letv.whatslive.web.service.system.SysLogService;
import com.letv.whatslive.web.service.system.SysRoleService;
import com.letv.whatslive.web.service.system.SysUserService;
import com.letv.whatslive.model.mysql.system.SysRole;
import com.letv.whatslive.model.mysql.system.SysUser;
import com.letv.whatslive.model.mysql.system.SysUserRoleRelation;
import com.letv.whatslive.web.constant.WebConstants;
import com.letv.whatslive.web.controller.PubController;
import com.letv.whatslive.web.util.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/system/user")
public class UserController extends PubController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysLogService sysLogService;

    private final String opType_SysUser = "/system/user/userSave";
    private final String opType_SysUserRoleRelation = "/system/user/userSaveRoleRelation";

    /**
     * 显示用户列表页面
     *
     * @return
     */
    @RequestMapping("")
    public ModelAndView show() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("/system/user");
        return modelAndView;
    }

    /**
     * 查询用户列表
     *
     * @param valueMap
     * @param response
     * @return
     */
    @RequestMapping("/list.json")
    @ResponseBody
    public Map<String, Object> list(@RequestBody MultiValueMap valueMap, HttpServletResponse response) {

        Map<String, Object> param = valueMap.toSingleValueMap();
        Integer start = ObjectUtils.toInteger(param.get("iDisplayStart"));
        Integer limit = ObjectUtils.toInteger(param.get("iDisplayLength"));
        String sEcho = ObjectUtils.toString(param.get("sEcho"));
        String search_userName = ObjectUtils.toString(param.get("search_userName"));

        Integer userNum = sysUserService.countSysUser(search_userName);
        List<SysUser> userList = sysUserService.getSysUserList(search_userName, start, limit);

        Map<String, Object> result = getSuccessMap();
        result.put("sEcho", sEcho);
        result.put("iTotalRecords", userNum); //total
        result.put("iTotalDisplayRecords", userNum); //totalAfterFilter
        result.put("aaData", userList.toArray());
        setResContent2Json(response);
        return result;
    }

    /**
     * 新增用户页面
     *
     * @param webRequest
     * @return
     */
    @RequestMapping("/page/userNew")
    public ModelAndView userNew(WebRequest webRequest) {

        // 获取用户信息
        SysUser user = new SysUser();
        // 获取相似产品包信息，供选择
        List<SysRole> roleList = sysRoleService.getSysRoleList();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", user);
        modelAndView.addObject("roleList", roleList);
        modelAndView.setViewName("system/user_new");
        return modelAndView;
    }


    /**
     * 修改用户页面
     *
     * @param webRequest
     * @return
     */
    @RequestMapping("/page/userEdit")
    public ModelAndView userEdit(WebRequest webRequest) {

        Integer id = ObjectUtils.toInteger(webRequest.getParameter("id"));

        SysUser user = sysUserService.getSysUserById(id);

        user.setSysUserRoleRelationList(sysRoleService.getSysUserRoleRelationSetByUserId(user.getId()));

        List<SysRole> roleList = sysRoleService.getSysRoleList();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", user);
        modelAndView.addObject("roleList", roleList);
        modelAndView.setViewName("system/user_edit");
        return modelAndView;
    }

    /**
     * 保存用户信息
     *
     * @param webRequest
     * @param response
     * @return
     */
    @RequestMapping("/userSave")
    @ResponseBody
    public Map<String, Object> userSave(WebRequest webRequest, HttpServletRequest request, HttpServletResponse response) {

        Integer id = ObjectUtils.toInteger(webRequest.getParameter("id"));
        String loginName = webRequest.getParameter("loginName");
        String userName = webRequest.getParameter("userName");
        String pazzword = webRequest.getParameter("pazzword");
        String roles = webRequest.getParameter("roles");
        Integer[] roleIdAry = ObjectUtils.string2IntAry(roles, ",", null);
        if (StringUtils.isBlank(loginName)) {
            setResContent2Json(response);
            return getFailMap("登录名不能为空！");
        }
        if (roleIdAry == null || roleIdAry.length == 0) {
            setResContent2Json(response);
            return getFailMap("请选择至少一个角色！");
        }
        SysUser user = new SysUser();
        user.setId(id);
        user.setLoginName(loginName);
        if (StringUtils.isNotBlank(pazzword))
            user.setPazzword(MD5Util.md5(pazzword));
        else
            user.setPazzword(MD5Util.md5(WebConstants.PAZZWORD));
        user.setUserName(userName);
        user.setIsEffective(ModelConstants.EFFECTIVE_YES);
        List<SysUserRoleRelation> userRoleList = Lists.newArrayList();
        for (Integer roleId : roleIdAry) {
            SysUserRoleRelation userRole = new SysUserRoleRelation();
            userRole.setRoleId(roleId);
            userRoleList.add(userRole);
        }
        if (id == null) {
            // 新增用户
            if (sysUserService.exsitSysUser(loginName)) {
                setResContent2Json(response);
                return getFailMap("用户已存在，请不要重复添加！");
            }
            // 保存用户和角色信息
            sysUserService.insertSysUser(user, userRoleList);
            Integer newId = sysUserService.getMaxId();
            SysUser newUser = sysUserService.getSysUserById(newId);

            sysLogService.saveSysLog(getLoginUser(request),getIpAddr(request),getLocalAddr(),opType_SysUser,
                    newId, toJson(null), toJson(newUser));
            sysLogService.saveSysLog(getLoginUser(request),getIpAddr(request),getLocalAddr(),opType_SysUserRoleRelation,
                    newId,toJson(null),toJson(userRoleList));
        } else {
            // 修改用户
            SysUser oldUser = sysUserService.getSysUserById(user.getId());
            Set<SysUserRoleRelation> oldRelation= sysRoleService.getSysUserRoleRelationSetByUserId(user.getId());
            sysUserService.updateSysUser(user, userRoleList);
            Set<SysUserRoleRelation> newRelation= sysRoleService.getSysUserRoleRelationSetByUserId(user.getId());
            SysUser newUser = sysUserService.getSysUserById(user.getId());

            sysLogService.saveSysLog(getLoginUser(request), getIpAddr(request), getLocalAddr(), opType_SysUser,
                    oldUser.getId(), toJson(oldUser), toJson(newUser));
            sysLogService.saveSysLog(getLoginUser(request), getIpAddr(request), getLocalAddr(), opType_SysUserRoleRelation,
                    user.getId(), toJson(oldRelation), toJson(newRelation));
        }
        // 返回结果
        setResContent2Json(response);
        return getSuccessMap();
    }

}
