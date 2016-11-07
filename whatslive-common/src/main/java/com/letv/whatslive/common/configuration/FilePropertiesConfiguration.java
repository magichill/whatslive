package com.letv.whatslive.common.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by gaoshan on 15-8-28.
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
