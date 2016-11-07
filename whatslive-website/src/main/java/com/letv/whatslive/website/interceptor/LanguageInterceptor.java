package com.letv.whatslive.website.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * Created by haojiayao on 2015/8/20.
 */
public class LanguageInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(LanguageInterceptor.class);

    /**
     * 国际化语言对应
     *
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {

//        // 浏览器设定首选语言
//        String acceptLanguage = "en";
//
//        if (request.getHeader("accept-language") != null) {
//
//            acceptLanguage = request.getHeader("accept-language").split(",")[0];
//        }
//
//        // 首选语言是中文以外时，默认使用英语
//        if (acceptLanguage.indexOf("zh") < 0) {
//
//            request.getSession().setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, Locale.ENGLISH);
//            request.getSession().setAttribute("language", "en");
//        } else {

            request.getSession().setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, Locale.CHINESE);
            request.getSession().setAttribute("language", "zh");
//        }

        return true;
    }
}
