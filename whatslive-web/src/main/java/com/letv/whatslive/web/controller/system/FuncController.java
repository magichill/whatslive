package com.letv.whatslive.web.controller.system;

import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.web.service.system.SysFunctionService;
import com.letv.whatslive.web.service.system.SysLogService;
import com.letv.whatslive.model.mysql.system.SysFunction;
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
import java.util.List;
import java.util.Map;

/**
 * Created by wangruifeng on 14-5-22.
 */
@Controller
@RequestMapping("/system/func")
public class FuncController extends PubController {

    private static final Logger logger = LoggerFactory.getLogger(FuncController.class);

    @Autowired
    private SysFunctionService sysFunctionService;

    @Autowired
    private SysLogService sysLogService;

    private final String opType_funcSave = "/system/func/funcSave";

    /**
     * 显示用户列表页面
     *
     * @return
     */
    @RequestMapping("")
    public ModelAndView show() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("/system/func");
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
        String search_funcName = ObjectUtils.toString(param.get("search_funcName"));

        Map<String,Object> params = Maps.newHashMap();
        if(StringUtils.isNotBlank(search_funcName)){
            params.put("searchName",search_funcName);
        }

//        Integer funcNum =sysFunctionService.countFunction(params);
        List<SysFunction> funcList = sysFunctionService.getfunctionListNoPage(params, start, limit);

        Map<String, Object> result = getSuccessMap();
        result.put("sEcho", sEcho);
//        result.put("iTotalRecords", funcNum); //total
//        result.put("iTotalDisplayRecords", funcNum); //totalAfterFilter
        result.put("aaData", funcList.toArray());
        setResContent2Json(response);
        return result;
    }

    /**
     * 新增用户页面
     *
     * @param webRequest
     * @return
     */
    @RequestMapping("/page/funcNew")
    public ModelAndView funcNew(WebRequest webRequest) {
        // 获取资源对象
        SysFunction func = new SysFunction();
        Integer parentFuncId = sysFunctionService.maxOrder(0);
        func.setFuncOrder(parentFuncId+1);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("func", func);
        modelAndView.addObject("opt",webRequest.getParameter("opt"));
        modelAndView.setViewName("system/func_new");
        return modelAndView;
    }


    /**
     * 修改用户页面
     *
     * @param webRequest
     * @return
     */
    @RequestMapping("/page/funcEdit")
    public ModelAndView funcEdit(WebRequest webRequest) {

        Integer id =  ObjectUtils.toInteger(webRequest.getParameter("id"));

        SysFunction func = sysFunctionService.getFuncById(id);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("func", func);
        modelAndView.addObject("opt",webRequest.getParameter("opt"));
        modelAndView.setViewName("system/func_new");
        return modelAndView;
    }

    /**
     * 修改用户页面
     *
     * @param webRequest
     * @return
     */
    @RequestMapping("/page/funcChild")
    public ModelAndView funcChild(WebRequest webRequest) {

        Integer id =  ObjectUtils.toInteger(webRequest.getParameter("id"));

        Integer parentFuncId = sysFunctionService.maxOrder(id);
        SysFunction func = new SysFunction();
        func.setParentFuncId(id);
        func.setFuncOrder(parentFuncId+1);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("func", func);
        modelAndView.addObject("opt",webRequest.getParameter("opt"));
        modelAndView.setViewName("system/func_new");
        return modelAndView;
    }

    /**
     * 保存新增栏目信息
     *
     * @param webRequest
     * @param response
     * @return
     */
    @RequestMapping("/funcSave")
    @ResponseBody
    public Map<String, Object> funcSave(SysFunction func, WebRequest webRequest, HttpServletRequest request,HttpServletResponse response) {

        if(func==null||
                func.getFuncName()==null||
                func.getIsLeaf()==null||
                func.getActionUrl()==null||
                func.getFuncOrder()==null){
            setResContent2Json(response);
            return getFailMap("必填的资源信息为空，请检查！");
        }


        //保存新增栏目信息
        sysFunctionService.insertLm(func);

        sysLogService.saveSysLog(getLoginUser(request),getIpAddr(request), getLocalAddr(),opType_funcSave,null,null,toJson(func));

        // 返回结果
        setResContent2Json(response);
        return getSuccessMap();
    }

    /**
     * 保存新增子资源信息
     *
     * @param webRequest
     * @param response
     * @return
     */
    @RequestMapping("/funcSaveChild")
    @ResponseBody
    public Map<String, Object> funcSaveChild(SysFunction func, WebRequest webRequest,HttpServletRequest request, HttpServletResponse response) {

        if(func==null||
                func.getFuncName()==null||
                func.getIsLeaf()==null||
                func.getActionUrl()==null||
                func.getFuncOrder()==null){
            setResContent2Json(response);
            return getFailMap("必填的资源信息为空，请检查！");
        }
        //保存新增资源信息
        sysFunctionService.insert(func);

        sysLogService.saveSysLog(getLoginUser(request),getIpAddr(request), getLocalAddr(),opType_funcSave,null,null,toJson(func));

        // 返回结果
        setResContent2Json(response);
        return getSuccessMap();
    }

    /**
     * 保存新增栏目信息
     *
     * @param webRequest
     * @param response
     * @return
     */
    @RequestMapping("/funcUpdate")
    @ResponseBody
    public Map<String, Object> funcUpdate(SysFunction func, WebRequest webRequest, HttpServletRequest request,HttpServletResponse response) {

        if(func==null||
                func.getFuncName()==null||
                func.getIsLeaf()==null||
                func.getActionUrl()==null||
                func.getFuncOrder()==null){
            setResContent2Json(response);
            return getFailMap("必填的资源信息为空，请检查！");
        }

        SysFunction oldObject = sysFunctionService.getFuncById(func.getId());
        //保存新增栏目信息
        sysFunctionService.updateSysFunction(func);

        SysFunction newObject = sysFunctionService.getFuncById(func.getId());

        sysLogService.saveSysLog(getLoginUser(request),getIpAddr(request), getLocalAddr(),opType_funcSave,func.getId(),toJson(oldObject),toJson(newObject));

        // 返回结果
        setResContent2Json(response);
        return getSuccessMap();
    }

}
