package com.letv.whatslive.website.util.configuration;

/**
 * Title:
 * Desc:
 * User: crespo
 * Company: www.gitv.cn
 * Date: 13-7-17 上午10:30
 */
public class PropertyGetter {
    private static final Configuration cfg = ConfigurationFactory.getConfiguration();

    private PropertyGetter() {
    }

    public static String getString(String key) {
        return cfg.getString(key);
    }

    public static String getString(String key, String defaultVal) {
        String cfgValue = cfg.getString(key);
        return (cfgValue == null || cfgValue.trim().length() == 0) ? defaultVal : cfgValue;
    }

    public static String[] getStringArray(String key) {
        return cfg.getStringArray(key);
    }

    public static int getInt(String key) {
        return cfg.getInt(key);
    }

    public static long getLong(String key) {
        return cfg.getLong(key);
    }

    public static int getInt(String key, int defaultVal) {
        int value = cfg.getInt(key);
        return value == -1 ? defaultVal : value;
    }

    public static double getDouble(String key, double defaultVal) {
        double value = cfg.getDouble(key);
        return value == 0 ? defaultVal : value;
    }
}
