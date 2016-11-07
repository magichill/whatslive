package com.letv.whatslive.model.utils;

import com.letv.whatslive.model.Program;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ObjUtils {

    public static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Integer ifNull(Integer value, Integer defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public static Long ifNull(Long value, Long defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public static Double ifNull(Double value, Double defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public static String ifNull(String value, String defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public static List ifNull(List value, List defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public static Integer toInteger(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        try {
            return new Integer(obj.toString().trim());
        } catch (Exception e) {
        }
        return null;
    }

    public static Integer toInteger(Object obj, Integer defaultValue) {
        Integer value = toInteger(obj);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public static Long toLong(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Long) {
            return (Long) obj;
        }
        try {
            return new Long(obj.toString().trim());
        } catch (Exception e) {
        }
        return null;
    }

    public static Long toLong(Object obj, Long defaultValue) {
        Long value = toLong(obj);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public static Double toDouble(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Double) {
            return (Double) obj;
        }
        try {
            return new Double(obj.toString().trim());
        } catch (Exception e) {
        }
        return null;
    }

    public static Double toDouble(Object obj, Double defaultValue) {
        Double value = toDouble(obj);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public static BigDecimal toBigDecimal(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof BigDecimal) {
            return (BigDecimal) obj;
        }
        try {
            return new BigDecimal(obj.toString().trim());
        } catch (Exception e) {
        }
        return null;
    }

    public static BigDecimal toBigDecimal(Object obj, BigDecimal defaultValue) {
        BigDecimal value = toBigDecimal(obj);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public static Boolean toBoolean(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        try {
            return new Boolean(obj.toString().trim());
        } catch (Exception e) {
        }
        return null;
    }

    public static String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        return obj.toString();
    }

    public static String toString(Object obj, String defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        return obj.toString();
    }

    public static String toStringTrim(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return ((String) obj).trim();
        }
        return obj.toString().trim();
    }

    public static String toStringTrim(Object obj, String defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        if (obj instanceof String) {
            return ((String) obj).trim();
        }
        return obj.toString().trim();
    }

    public static String dateFormat(Long time) {
        if (time == null) {
            return SDF_DATE.format(new Date());
        }
        return SDF_DATE.format(new Date(time));
    }
}
