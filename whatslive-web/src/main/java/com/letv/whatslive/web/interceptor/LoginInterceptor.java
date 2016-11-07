package com.letv.whatslive.web.interceptor;

import com.letv.whatslive.web.constant.WebConstants;
import com.letv.whatslive.web.util.WebUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Title:
 * Desc:
 * User: crespo
 * Company: www.gitv.cn
 * Date: 13-11-21 下午5:06
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isStaticResource(request) || isNotFilter(request)) {
            logger.debug("在不拦截的列表中：" + request.getServletPath());

            return super.preHandle(request, response, handler);
        }

        HttpSession session = request.getSession();

        if (isLogin(session)) {
            return super.preHandle(request, response, handler);
        } else {
            WebUtils.redirect2LoginPage(request, response);
            return false;
        }
    }

    public static boolean isStaticResource(HttpServletRequest req) {
        String requestPath = req.getServletPath();
        if (StringUtils.lowerCase(requestPath).indexOf(StringUtils.lowerCase("/static/")) > -1) {
            return true;
        }
        return false;
    }

    public static boolean isNotFilter(HttpServletRequest req) {
        String requestPath = req.getServletPath();
        String[] filterExcludes = WebConstants.SERVER_FILTER_EXCLUDES;
        for (String filterExclude : filterExcludes) {
            if (StringUtils.lowerCase(requestPath).indexOf(StringUtils.lowerCase(filterExclude)) > -1) {
                return true;
            }
        }
        return false;
    }

    public static boolean isLogin(HttpSession session) {
        if (session != null && session.getAttribute(WebConstants.WHATSLIVE_SESSION_USERINFO_KEY) != null) {
            return true;
        } else {
            return false;
        }

    }
}
