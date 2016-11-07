package com.letv.whatslive.web.listener;

import com.letv.whatslive.web.util.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;

/**
 * .
 * User: zhumh
 * Date: 13-11-14 下午5:16
 */
public class SpringContextLoaderListener extends ContextLoaderListener {

    private static final Logger logger = LoggerFactory.getLogger(SpringContextLoaderListener.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            super.contextInitialized(event);
            ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext());
            SpringUtils.setApplicationContext(ctx);
        } catch (Exception ex) {
            logger.error("设置spring context环境异常！", ex);
        }
    }

}
