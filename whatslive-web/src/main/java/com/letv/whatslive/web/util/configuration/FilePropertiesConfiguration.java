package com.letv.whatslive.web.util.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Title:
 * Desc:
 * User: crespo
 * Company: www.gitv.cn
 * Date: 13-7-17 上午10:07
 */
public class FilePropertiesConfiguration extends PropertiesConfiguration {
    private static final Logger logger= LoggerFactory.getLogger(FilePropertiesConfiguration.class);

    public FilePropertiesConfiguration(String propertiesFile) {
        Properties props=new Properties();
        try {
            InputStream is = new FileInputStream(propertiesFile);
            props.load(is);
            this.properties=props;
        } catch (Exception ex) {
            logger.error("",ex);
            this.properties=EMPTY_PROPERTIES;
        }
    }
}
