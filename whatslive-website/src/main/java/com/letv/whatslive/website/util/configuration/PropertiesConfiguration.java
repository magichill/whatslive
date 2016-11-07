package com.letv.whatslive.website.util.configuration;


import com.letv.whatslive.website.util.String.StringUtils;

import java.util.Properties;
import java.util.regex.Matcher;

/**
 * Title:
 * Desc:
 * User: crespo
 * Company: www.gitv.cn
 * Date: 13-7-17 上午10:08
 */
public abstract class PropertiesConfiguration implements Configuration {
    static final Properties EMPTY_PROPERTIES = new Properties();

    protected Properties properties;

    /*
     * (non-Javadoc)
     *
     * @see
     * com.qiyi.vrs.vis.commons.configuration.Configuration#getString(java.lang
     * .String)
     */
    public String getString(String key) {
        String _value = getAndProcessValue(key);
        return _value;
    }

    private String getAndProcessValue(String key) {
        String _value = properties.getProperty(key);
        if (StringUtils.isBlank(_value)) {
            return StringUtils.EMPTY;
        }
        StringBuffer result = new StringBuffer();
        Matcher matcher = Configuration.VALUE_RESOLVER_PATTERN.matcher(_value);

        String resolveKey = null;
        String matchStr = null;

        while (matcher.find()) {
            matchStr = matcher.group();
            resolveKey = org.apache.commons.lang3.StringUtils.substringBetween(matchStr, "${", "}");
            matcher.appendReplacement(result, properties.getProperty(resolveKey));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.qiyi.vrs.vis.commons.configuration.Configuration#getInt(java.lang
     * .String)
     */
    public int getInt(String key) {
        String value = getAndProcessValue(key);
        return StringUtils.toInt(value, -1);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.qiyi.vrs.vis.commons.configuration.Configuration#getStringArray(java
     * .lang.String)
     */
    public String[] getStringArray(String key) {
        String value = getAndProcessValue(key);
        if (value == null) {
            return null;
        }
        return value.split(",");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.qiyi.vrs.vis.commons.configuration.Configuration#getLong(java.lang
     * .String)
     */
    public long getLong(String key) {
        String value = getAndProcessValue(key);
        return StringUtils.toLong(value, -1L);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.qiyi.vrs.vis.commons.configuration.Configuration#getFloat(java.lang
     * .String)
     */
    public float getFloat(String key) {
        String _value = getAndProcessValue(key);
        if (StringUtils.isBlank(_value)) {
            return -1F;
        }
        try {
            return Float.parseFloat(_value);
        } catch (Exception ex) {
            // ignore this ex
        }
        return -1F;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.qiyi.vrs.vis.commons.configuration.Configuration#getDouble(java.lang
     * .String)
     */
    public double getDouble(String key) {
        String _value = getAndProcessValue(key);
        if (StringUtils.isBlank(_value)) {
            return -1D;
        }
        try {
            return Double.parseDouble(_value);
        } catch (Exception ex) {
            // ignore this ex
        }
        return -1D;
    }

    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }
}
