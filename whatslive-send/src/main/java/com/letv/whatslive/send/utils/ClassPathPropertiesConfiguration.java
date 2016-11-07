package com.letv.whatslive.send.utils;


import com.letv.whatslive.send.utils.util.ClassLoaderUtils;

/**
 * Title:
 * Desc:
 * User: crespo
 * Company: www.gitv.cn
 * Date: 13-7-17 上午10:06
 */
public class ClassPathPropertiesConfiguration extends PropertiesConfiguration {
    public ClassPathPropertiesConfiguration(String propertiesFile) {
        this.properties = ClassLoaderUtils.getProperties(propertiesFile);
    }
}
