package com.letv.whatslive.common.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by gaoshan on 14-10-22.
 */

public class ObjectUtils {

    private static final String REGEXP_FORMAT_STRING = "(\\{\\d\\})";
    private static final Pattern pattern = Pattern.compile(REGEXP_FORMAT_STRING, Pattern.CASE_INSENSITIVE);
    private static final Pattern VALUE_RESOLVER_PATTERN = Pattern.compile("\\$\\{\\w+\\}", Pattern.CASE_INSENSITIVE);

    public static <E> E ifNull(E value, E defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public static Double toDouble(Object obj){
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

    /**
     * @param format hehe{0}，hello world{1}, welcome {0}
     * @param args
     * @return
     */
    public static String fmtString(String format, Object... args) {
        if (args == null) {
            return format;
        }
        String result = format;
        Matcher matcher = pattern.matcher(format);
        while (matcher.find()) {
            String token = matcher.group();
            int idx = NumberUtils.toInt(token.substring(1, token.length() - 1));
            result = StringUtils.replace(result, token, toString(args[idx]));
        }
        return result;
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

    public static String prop2String(Object obj, String[] property) {
        if (obj == null) {
            return null;
        }
        if (ArrayUtils.isEmpty(property)) {
            PropertyDescriptor[] props = new PropertyUtilsBean().getPropertyDescriptors(obj);
            if (props == null) {
                return obj.toString();
            }
            property = new String[props.length];
            for (int i = 0; i < props.length; i++) {
                property[i] = props[i].getName();
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(obj.getClass().getName());
        sb.append("：{");
        List<String> propList = new ArrayList<String>(property.length);
        for (String prop : property) {
            try {
                propList.add(prop + "=" + BeanUtils.getProperty(obj, prop));
            } catch (Exception e) {
                propList.add(prop + "=null");
            }
        }
        sb.append(StringUtils.join(propList.iterator(), ","));
        sb.append("}");
        return sb.toString();
    }

    public static Integer[] string2IntAry(String str, String separatorChars, Integer defaultValue) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        String[] strAry = StringUtils.split(str, separatorChars);
        Integer[] intAry = new Integer[strAry.length];
        for (int i = 0; i < strAry.length; i++) {
            intAry[i] = toInteger(strAry[i], defaultValue);
        }
        return intAry;
    }

    public static List<Integer> string2IntList(String str, String separatorChars, Integer defaultValue) {
        if (StringUtils.isBlank(str)) {
            return new ArrayList<Integer>(1);
        }
        String[] strAry = StringUtils.split(str, separatorChars);
        List<Integer> intList = new ArrayList<Integer>(strAry.length);
        for (int i = 0; i < strAry.length; i++) {
            intList.add(toInteger(strAry[i], defaultValue));
        }
        return intList;
    }

    public static Long[] string2LongAry(String str, String separatorChars, Long defaultValue) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        String[] strAry = StringUtils.split(str, separatorChars);
        Long[] longAry = new Long[strAry.length];
        for (int i = 0; i < strAry.length; i++) {
            longAry[i] = toLong(strAry[i], defaultValue);
        }
        return longAry;
    }

    public static List<Long> string2LongList(String str, String separatorChars, Long defaultValue) {
        if (StringUtils.isBlank(str)) {
            return new ArrayList<Long>(1);
        }
        String[] strAry = StringUtils.split(str, separatorChars);
        List<Long> longList = new ArrayList<Long>(strAry.length);
        for (int i = 0; i < strAry.length; i++) {
            longList.add(toLong(strAry[i], defaultValue));
        }
        return longList;
    }

    public static boolean isFixLengthDigits(String strNum, int length) {
        if (NumberUtils.isDigits(strNum) && strNum.length() == length) {
            return true;
        }
        return false;
    }

    /**
     * ip地址转成整数.
     *
     * @param ipStr
     * @return
     */
    public static Long ip2long(String ipStr) {
        if (StringUtils.isBlank(ipStr)) {
            return 0L;
        }
        Long[] ipAry = string2LongAry(ipStr, "[.]", 0L);
        Long ipLong = 16777216L * ipAry[0] + 65536L * ipAry[1] + 256 * ipAry[2] + ipAry[3];
        return ipLong;
    }

    /**
     * 整数转成ip地址.
     *
     * @param ipLong
     * @return
     */
    public static String long2ip(Long ipLong) {
        long mask[] = {0x000000FF, 0x0000FF00, 0x00FF0000, 0xFF000000};
        StringBuffer ipInfo = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            long num = (ipLong & mask[i]) >> (i * 8);
            if (i > 0) {
                ipInfo.insert(0, ".");
            }
            ipInfo.insert(0, Long.toString(num, 10));
        }
        return ipInfo.toString();
    }


    /**
     * 正则过滤特殊字符
     * @param str
     * @return
     * @throws java.util.regex.PatternSyntaxException
     */
    public   static   String StringFilter(String   str)   throws PatternSyntaxException {
        // 只允许字母和数字
        // String   regEx  =  "[^a-zA-Z0-9]";
        // 清除掉所有特殊字符
        String regEx="[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public static String generateSuffix() {
        Integer random = (int)(Math.random()*900)+100;
        return toString(random);
    }

    public static String getAndProcessValue(String uri,Map<String,String> map) {
        String _value = uri;
        if (StringUtils.isBlank(_value)) {
            return StringUtils.EMPTY;
        }
        StringBuffer result = new StringBuffer();
        Matcher matcher = VALUE_RESOLVER_PATTERN.matcher(_value);

        String resolveKey = null;
        String matchStr = null;

        while (matcher.find()) {
            matchStr = matcher.group();
            resolveKey = StringUtils.substringBetween(matchStr, "${", "}");
            matcher.appendReplacement(result, map.get(resolveKey));
        }
        matcher.appendTail(result);
        return result.toString();
    }

}

