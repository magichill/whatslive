package com.letv.whatslive.common.configuration;

import java.util.regex.Pattern;

/**
 * Created by gaoshan on 15-8-28.
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
