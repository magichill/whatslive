package com.letv.whatslive.inner.utils;

import java.util.regex.Pattern;

/**
 * Title: configuration
 * Desc:
 * Company: www.gitv.cn
 * Date: 13-7-17 上午10:00
 */
public interface Configuration {
    Pattern VALUE_RESOLVER_PATTERN = Pattern.compile("\\$\\{\\w+\\}", Pattern.CASE_INSENSITIVE);

    String getString(String key);

    int getInt(String key);

    String[] getStringArray(String key);

    long getLong(String key);

    float getFloat(String key);

    double getDouble(String key);

    boolean containsKey(String key);
}
