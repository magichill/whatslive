package com.letv.whatslive.send.utils.util;


import org.springframework.context.ApplicationContext;

/**
 * Title:
 * Desc:
 * User: crespo
 * Company: www.gitv.cn
 * Date: 13-7-17 上午10:37
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
