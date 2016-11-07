package com.letv.whatslive.robot.utils;

import org.springframework.context.ApplicationContext;

/**
 * Title:
 * Desc:
 * User: 王健
 * Company: www.letv.com
 * Date: 15-9-11 15:31
 */
public class SpringUtils {
    public static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(Class<T> clazz, String name) {
        return applicationContext.getBean(name, clazz);
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }
}
