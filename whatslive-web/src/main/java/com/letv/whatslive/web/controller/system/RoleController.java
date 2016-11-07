package com.letv.whatslive.web.controller.system;

import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.web.service.system.SysLogService;
import com.letv.whatslive.web.service.system.SysRoleService;
import com.letv.whatslive.model.mysql.system.SysRole;
import com.letv.whatslive.model.mysql.system.SysRoleFunctionRelation;
import com.letv.whatslive.model.mysql.system.TreeVo;
import com.letv.whatslive.web.controller.PubController;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/system/role")
public class RoleController extends PubController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysLogService sysLogService;

    private final String opType_SysRole = "/system/role/roleSave";
    private final String opType_SysRole_Authorize = "/system/role/roleAuthorize";

    /**
     * 显示角色列表页面
     *
     * @return
     */
    @RequestMapping("")
    public ModelAndView show() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("/system/role");
        return modelAndView;
    }

    /**
     * 查询角色列表
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
        String search_roleName = ObjectUtils.toString(param.get("search_roleName"));
        Map params = Maps.newHashMap();
        if(StringUtils.isNotBlank(search_roleName)){
            params.put("searchName",search_roleName);
        }
        Integer roleNum = sysRoleService.countSysRole(params);
        List<SysRole> roleList = sysRoleService.getSysRoleListByParams(params,start,limit);


        Map<String, Object> result = getSuccessMap();
        result.put("sEcho", sEcho);
        result.put("iTotalRecords", roleNum); //total
        result.put("iTotalDisplayRecords", roleNum); //totalAfterFilter
        result.put("aaData", roleList.toArray());
        setResContent2Json(response);
        return result;
    }

    /**
     * 新增角色页面
     *
     * @param webRequest
     * @return
     */
    @RequestMapping("/page/roleNew")
    public ModelAndView userNew(WebRequest webRequest) {

        Integer id = ObjectUtils.toInteger(webRequest.getParameter("id"));
        SysRole role = new SysRole();
        if(id!=null){
            role = sysRoleService.getSysRoleById(id);
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("role",role);
        modelAndView.setViewName("system/role_new");
        return modelAndView;
    }


    /**
     * 保存角色信息
     *
     * @param webRequest
     * @param response
     * @return
     */
    @RequestMapping("/roleSave")
    @ResponseBody
    public Map<String, Object> userSave(WebRequest webRequest,HttpServletRequest request, HttpServletResponse response) {

        Integer id = ObjectUtils.toInteger(webRequest.getParameter("id"));
        String roleName = webRequest.getParameter("roleName");
        if (StringUtils.isBlank(roleName)) {
            setResContent2Json(response);
            return getFailMap("角色名不能为空！");
        }
        SysRole role = new SysRole();
        role.setRoleName(roleName);

        if (id == null) {
            // 新增角色
           if(sysRoleService.existsRole(roleName)){
               setResContent2Json(response);
               return getFailMap("该角色名称已存在，请不要重复添加！");
           }
            // 保存角色
            sysRoleService.insertSysRole(role);
            sysLogService.saveSysLog(getLoginUser(request), getIpAddr(request), getLocalAddr(),opType_SysRole,null,null,toJson(role));
        } else {//用户ID不为空，则是修改角色

            SysRole oldRole = sysRoleService.getSysRoleById(id);
            if(oldRole==null){
                setResContent2Json(response);
                return getFailMap("对应ID用户不存在，无法修改请确认！");
            }
            //设置用户id
            role.setId(id);
            // 修改角色
            sysRoleService.updateSysRole(role);
            SysRole newRole = sysRoleService.getSysRoleById(id);

            sysLogService.saveSysLog(getLoginUser(request), getIpAddr(request), getLocalAddr(),opType_SysRole,id,toJson(oldRole),toJson(newRole));

        }
        // 返回结果
        setResContent2Json(response);
        return getSuccessMap();
    }

    /**
     * 获取资源树
     * @param webRequest
     * @param response
     * @return
     */
    @RequestMapping("/getTree")
    @ResponseBody
    public TreeVo getTree(WebRequest webRequest, HttpServletResponse response){

        Integer roleId = ObjectUtils.toInteger(webRequest.getParameter("id"));
        TreeVo treeVo = sysRoleService.getTreeList(roleId);
        return treeVo;

    }

    /**
     * 保存角色对应的功能资源信息
     *
     * @param webRequest
     * @param response
     * @return
     */
    @RequestMapping("/roleSave_authorize")
    @ResponseBody
    public Map<String, Object> roleSave_authorize(WebRequest webRequest,HttpServletRequest request, HttpServletResponse response) {

        Integer id = ObjectUtils.toInteger(webRequest.getParameter("roleId"));
        String funcIds = webRequest.getParameter("funcIds");
        if (StringUtils.isBlank(funcIds)) {
            setResContent2Json(response);
            return getFailMap("功能资源不能为空！");
        }
        List<SysRoleFunctionRelation> sysRoleFunctionRelations = new ArrayList<SysRoleFunctionRelation>();
        String[] funcIdArray = funcIds.split(",");
        for(int i=0;i<funcIdArray.length;i++){
            SysRoleFunctionRelation sysRoleFunctionRelation = new SysRoleFunctionRelation();
            sysRoleFunctionRelation.setRoleId(id);
            sysRoleFunctionRelation.setFuncId(Integer.valueOf(funcIdArray[i]));
            sysRoleFunctionRelations.add(sysRoleFunctionRelation);
        }

        List<SysRoleFunctionRelation> oldList = sysRoleService.getFunctionIdByRoleId(id);

        sysRoleService.insertRoleFuncRelation(id,sysRoleFunctionRelations);

        List<SysRoleFunctionRelation> newList = sysRoleService.getFunctionIdByRoleId(id);

        sysLogService.saveSysLog(getLoginUser(request), getIpAddr(request), getLocalAddr(),opType_SysRole_Authorize,id,toJson(oldList),toJson(newList));
        // 返回结果
        setResContent2Json(response);
        return getSuccessMap();
    }

}
