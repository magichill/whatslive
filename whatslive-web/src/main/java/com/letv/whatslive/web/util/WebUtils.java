package com.letv.whatslive.web.util;

import com.letv.whatslive.model.constant.ProgramConstants;
import com.letv.whatslive.model.mysql.system.SysFunction;
import com.letv.whatslive.model.mysql.system.SysUser;
import com.letv.whatslive.web.service.system.SysFunctionService;
import com.letv.whatslive.web.service.system.SysUserService;
import com.letv.whatslive.web.constant.WebConstants;
import com.google.common.collect.Lists;
import com.letv.whatslive.web.service.video.VideoService;
import com.letv.whatslive.web.util.util.SpringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Title: 处理web相关功能的工具类
 * Desc: 处理web相关功能的工具类
 * User: crespo
 * Company: www.gitv.cn
 * Date: 13-11-21 下午5:36
 */
public class WebUtils {

    private static final Logger logger = LoggerFactory.getLogger(WebUtils.class);

    private static SysFunctionService sysFunctionService;
    private static SysUserService sysUserService;
    private static VideoService videoService;

    static{
        if (sysFunctionService == null) {
            sysFunctionService = SpringUtils.getBean(SysFunctionService.class, "sysFunctionService");
        }
        if (sysUserService == null) {
            sysUserService = SpringUtils.getBean(SysUserService.class, "sysUserService");
        }
        if (videoService == null){
            videoService = SpringUtils.getBean(VideoService.class, "videoService");
        }
    }

    /**
     * 重定向至登录页面
     * @param request
     * @param response
     * @throws Exception
     */
    public static void redirect2LoginPage(HttpServletRequest request, HttpServletResponse response) throws Exception{
        response.sendRedirect(request.getContextPath() + WebConstants.LOGIN_PAGE_URI);
    }

    /**
     * 获取登录用户对象
     * @param request
     * @return
     */
    public static SysUser getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(session != null){
            Object userObj = session.getAttribute(WebConstants.WHATSLIVE_SESSION_USERINFO_KEY);
            if(userObj != null){
                return (SysUser) userObj;
            }
        }
        return null;
    }

    /**
     * 获取登录用户对象id
     *
     * @param request
     * @return
     */
    public static Integer getLoginUserId(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session != null) {
            Object userObj = session.getAttribute(WebConstants.WHATSLIVE_SESSION_USERINFO_KEY);
            if (userObj != null) {
                return ((SysUser) userObj).getId();
            }
        }
        return -1;
    }

    /**
     * 获取登录用户的中文用户名，为null时返回空字符串
     * @param request
     * @return
     */
    public static String getLoginUserNameNotNull(HttpServletRequest request){
        SysUser sysUser = getLoginUser(request);
        if(sysUser != null && StringUtils.isNotEmpty(sysUser.getUserName())){
            return sysUser.getUserName();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取拥有权限的顶级功能（子系统）
     * @param request
     * @return
     */
    public static List<SysFunction> getTopFunctionListByLoginUser(HttpServletRequest request){
        return getFunctionListByLoginUserAndParentFuncId(request, 0);
    }

    /**
     * 获取拥有权限的顶级功能（子系统）
     * @param request
     * @return
     */
    public static List<SysFunction> getFunctionListByLoginUserAndParentFuncId(HttpServletRequest request, Integer parentFuncId){
        SysUser sysUser = getLoginUser(request);
        if(sysUser == null){
            logger.error("未登录，非法操作");
            return Lists.newArrayList();
        }
        List<SysFunction> fList = sysFunctionService.getFunctionListByParentId(parentFuncId);
        List<SysFunction> funcList = Lists.newArrayList();
        if(fList != null){
            for(SysFunction f : fList){
                if(sysUserService.hasFuncRight(sysUser.getId(), f.getId())){
                    //对于可视化审核页面，如果有被举报的直播或者录播视频，需要提示红点
                    if(WebConstants.noticeFunctionName.equals(f.getFuncName())){
                        if(videoService.countReportProgramByPTypes(ProgramConstants.pType_live+","+ProgramConstants.pType_end)>0){
                            f.setFuncName(f.getFuncName()+WebConstants.noticeImgHTML);
                        }

                    }
                    funcList.add(f);
                }
            }
        }
        return funcList;
    }

    public static String addQueryParam(String queryUrl, String key, String value){
        if(queryUrl == null){
            logger.error("queryUrl为空");
            return null;
        }
        if(queryUrl.indexOf("?") >= 0){
            return queryUrl + "&" + key + "=" + value;
        }else{
            return queryUrl + "?" + key + "=" + value;
        }
    }

    public static boolean isFuncActive(SysFunction func, int activeFId){
        if(func == null || activeFId <= 0){
            logger.error("参数错误，有参数为空");
            return false;
        }
        if(activeFId != 0){
            if(func.getIsLeaf() != null && func.getIsLeaf() == 1){
                //如果叶子节点，直接判断id是否相同
                return func.getId() == activeFId;
            }else{
                //非叶子节点，递归activeFId的所有父节点，查看是否在父节点中
                List<SysFunction> parentFuncList = getParentFunctionListByFuncId(activeFId);
                for(SysFunction f : parentFuncList){
                    if(f.getId().equals(func.getId())){
                        return true;
                    }
                }
            }

        }
        return false;
    }

    public static List<SysFunction> getParentFunctionListByFuncId(Integer funcId){
        return sysFunctionService.getParentFunctionListByFuncId(funcId);
    }

    public static SysFunction getTopFunctionBySubFuncId(Integer subFuncId){
        SysFunction subFunc = sysFunctionService.getFuncById(subFuncId);
        if(subFunc.getParentFuncId() != null && subFunc.getParentFuncId() == 0){
            return subFunc;
        }else{
            List<SysFunction> parentFuncList = getParentFunctionListByFuncId(subFuncId);
            for(SysFunction f : parentFuncList){
                if(f.getParentFuncId() != null && f.getParentFuncId() == 0){
                    return f;
                }
            }
        }
        return null;
    }

    public static SysFunction getFirstLeafFunctionByLoginUserAndTopFunction(HttpServletRequest request, Integer topFuncId){
        List<SysFunction> f1List = getFunctionListByLoginUserAndParentFuncId(request, topFuncId);
        if(f1List != null && f1List.size() > 0){
            SysFunction firstF1 = f1List.get(0);
            if(firstF1.getIsLeaf() != null && firstF1.getIsLeaf() == 1){
                return firstF1;
            }else{
                List<SysFunction> firstF2List = WebUtils.getFunctionListByLoginUserAndParentFuncId(request, firstF1.getId());
                for(SysFunction f2 : firstF2List){
                    if(f2.getIsLeaf() != null && f2.getIsLeaf() == 1){
                        return f2;
                    }
                }
            }
        }
        return null;
    }


}
