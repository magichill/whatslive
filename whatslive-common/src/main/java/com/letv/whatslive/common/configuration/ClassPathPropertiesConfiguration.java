package com.letv.whatslive.common.configuration;

import com.letv.whatslive.common.utils.ClassLoaderUtils;

/**
 * Created by gaoshan on 15-8-28.
 */
public class ClassPathPropertiesConfiguration extends PropertiesConfiguration {
    public ClassPathPropertiesConfiguration(String propertiesFile) {
        this.properties = ClassLoaderUtils.getProperties(propertiesFile);
    }
}
