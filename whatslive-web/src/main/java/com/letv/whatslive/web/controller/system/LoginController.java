package com.letv.whatslive.web.controller.system;

import com.letv.whatslive.web.service.system.SysUserService;
import com.letv.whatslive.model.mysql.system.SysUser;
import com.letv.whatslive.web.constant.WebConstants;
import com.letv.whatslive.web.util.http.HttpClientUtils;
import com.letv.whatslive.web.util.json.JsonUtils;
import com.letv.whatslive.web.util.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.Map;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private SysUserService sysUserService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/loginsubmit", method = RequestMethod.POST)
    public String loginsubmit(@RequestParam(value = "loginname", required = true) String loginName,
                              @RequestParam(value = "password", required = true) String password,
                              HttpSession httpSession,
                              RedirectAttributes redirectAttributes) {
        try {
            if (StringUtils.isNotBlank(loginName) && StringUtils.isNotBlank(password)) {
                //boolean loginStatus = userService.ldapAuthenticate(loginName, password);
                SysUser sysUser = sysUserService.getEffectiveUserByLoginName(loginName);
                boolean loginStatus = false;

                //判断用户在系统中是否存在
                if (sysUser != null && StringUtils.equals(sysUser.getLoginName(), loginName)) {
                    //如果存在
                    //验证SSO用户和密码
                    if (StringUtils.isNotEmpty(sysUser.getPazzword()) && StringUtils.isNotEmpty(password)) {
                        if (loginName.equals(WebConstants.ADMIN)) {
                            if (sysUser.getPazzword().equals(MD5Util.md5(password)))
                                loginStatus = true;
                            else
                                loginStatus = false;
                        } else {
                            loginStatus = checkUser(loginName, transcode(password, WebConstants.SSO_ENCODE, WebConstants.KEY), WebConstants.KEY);
                        }
                    }

                } else {
                    //验证失败，系统中不存该用户
                    redirectAttributes.addFlashAttribute("message", "验证失败，系统中不存该用户，请联系管理员开通权限！");
                    return "redirect:/login";
                }


                if (loginStatus) {
                    httpSession.setAttribute(WebConstants.WHATSLIVE_SESSION_USERINFO_KEY, sysUser);
//                    return "redirect:/public/product_package/";
                    return "redirect:/";
                } else {
                    //验证失败
                    redirectAttributes.addFlashAttribute("message", "用户名密码错误");
                    return "redirect:/login";
                }
            } else {
                //参数为空
                redirectAttributes.addFlashAttribute("message", "用户名密码不能为空");
                return "redirect:/login";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("登录出错，loginName:" + loginName + "", e);
        }
        //参数为空
        redirectAttributes.addFlashAttribute("message", "未知错误");
        return "redirect:/login";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession httpSession) {
        try {
            if (httpSession != null) {
                httpSession.invalidate();
            }

            return "redirect:/login";
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("登出出错", e);
        }

        return "redirect:/login";
    }

    /**
     * 调用SSO加密密码
     *
     * @return
     */
    private String transcode(String v, String type, String site) {
        //参数顺序： site time type v

        String tempString = "site=" + WebConstants.KEY + "&time=" + getCurrentTime() + "&type=" + type + "&v=" + URLEncoder.encode(v);
        String sign = MD5Util.md5(tempString + WebConstants.SIGN_KEY);

        String url = WebConstants.SSO_TRANSCODE_URL + "?" + tempString + "&sign=" + sign;

        String result = HttpClientUtils.get(url);
        logger.info(result);
        Map map = JsonUtils.json2Obj(result, Map.class);
        Map respondMap = (Map) map.get("respond");
        String status = String.valueOf((Integer) respondMap.get("status"));
        String code = (String) respondMap.get("code");
        if (code.equals("0") && status.equals("1")) {//请求正常
            //获取加密后字符串
            String rtnString = (String) map.get("objects");
            return rtnString;
        } else {
            //请求SSO后台报错
            String msg = (String) respondMap.get("msg");
            logger.error("请求SSO==调用SSO加密密码后台报错:msg=" + msg);
        }
        return null;
    }

    /**
     * 调用SSO 验证用户信息
     *
     * @return
     */
    private boolean checkUser(String username, String password, String site) {
        boolean flag = false;
        //参数顺序： password site time username
        String tempString = "password=" + password + "&site=" + site + "&time=" + getCurrentTime() + "&username=" + username;
        String sign = MD5Util.md5(tempString + WebConstants.SIGN_KEY);
        String url = WebConstants.SSO_CHECKUSER_URL + "?" + tempString + "&sign=" + sign;


        String result = HttpClientUtils.get(url);
        logger.info(result);
        Map map = JsonUtils.json2Obj(result, Map.class);
        Map respondMap = (Map) map.get("respond");
        String status = String.valueOf((Integer) respondMap.get("status"));
        // String code = (String)respondMap.get("code");
        if (status.equals("1")) {//请求正常
            flag = true;
        } else {
            //请求SSO后台报错
            String msg = (String) respondMap.get("msg");
            logger.error("请求SSO==调用SSO加密密码后台报错:msg=" + msg);
        }

        return flag;


    }

    /**
     * 获取当前时间戳字符串
     *
     * @return
     */
    private String getCurrentTime() {
        //获取当前时间戳
        long time = System.currentTimeMillis();
        String timeString = String.valueOf(time);
        String subTime = timeString.substring(0, timeString.length() - 3);
        return subTime;

    }


}
