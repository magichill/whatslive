package com.letv.whatslive.send.utils.String;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Title:
 * Desc:
 * User: crespo
 * Company: www.gitv.cn
 * Date: 13-7-17 上午10:09
 */
public class StringUtils {
    private static final Logger logger = LoggerFactory.getLogger(StringUtils.class);
    public static final String EMPTY = "";

    private StringUtils() {
    }

    public static int toInt(String str) {
        return toInt(str, -1);
    }

    public static int toInt(String str, int defaultValue) {
        if (isBlank(str)) {
            return defaultValue;
        }

        try {
            int returnValue = Integer.parseInt(str);
            return returnValue;
        } catch (Exception ex) {
            logger.error("", ex);
            // TODO
        }
        return defaultValue;
    }

    public static long toLong(String str) {
        return toLong(str, -1);
    }

    public static long toLong(String str, long defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            long returnValue = Long.parseLong(str);
            return returnValue;
        } catch (Exception ex) {
            logger.error("", ex);
        }
        return defaultValue;
    }

    public static boolean isBlank(String _value) {
        return org.apache.commons.lang.StringUtils.isBlank(_value);
    }

    /**
     * 字符串连接实现
     *
     * @param args
     * @return
     */
    public static String concat(Object... args) {
        if (args == null || args.length == 0) {
            return EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Object arg : args) {
            stringBuilder.append(arg);
        }
        return stringBuilder.toString();
    }

    public static String concatWithSplit(String split, Object[] args) {
        if (args == null || args.length == 0) {
            return EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Object arg : args) {
            stringBuilder.append(split).append(arg);
        }

        if (stringBuilder.length() > 0 && !StringUtils.isBlank(split)) {
            return stringBuilder.toString().substring(split.length());
        }
        return stringBuilder.toString();
    }

    /**
     * 仿c语言的sprintf实现
     *
     * @param format 字符串输出格式
     * @param args
     * @return
     */
    public static String sprintf(String format, Object... args) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        writer.printf(format, args);
        writer.close();
        return stringWriter.toString();
    }

    private static final String REGEXP_FORMAT_STRING = "(\\{\\d\\})";
    private static final Pattern pattern = Pattern.compile(REGEXP_FORMAT_STRING, Pattern.CASE_INSENSITIVE);

    /**
     * @param format aaaa{0}hello world{1}, welcome {0}
     * @param args
     * @return
     */
    public static String buildString(String format, Object... args) {
        Matcher matcher = pattern.matcher(format);
        String result = format;
        if (args == null) {
            return result;
        }
        while (matcher.find()) {
            String token = matcher.group();
            int idx = Integer.parseInt(token.substring(1, token.length() - 1));
            result = result.replace(token, args[idx] == null ? "" : args[idx].toString());
        }
        return result;
    }

    /**
     * 用于截取字符串
     *
     * @param vname  标题名称
     * @param length 截取的长度
     * @return 截取后的字符串
     * @throws Exception
     */
    public static String nameSubstring(String vname, int length) {

        byte[] bytes = null;
        try {
            bytes = vname.getBytes("Unicode");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int n = 0; // 表示当前的字节数
        int i = 2; // 要截取的字节数，从第3个字节开始
        for (; i < bytes.length && n < length; i++) {
            // 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节
            if (i % 2 == 1) {
                n++; // 在UCS2第二个字节时n加1
            } else {
                // 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节
                if (bytes[i] != 0) {
                    n++;
                }
            }
        }
        // 如果i为奇数时，处理成偶数
        if (i % 2 == 1) {

            // 该UCS2字符是汉字时，去掉这个截一半的汉字
            if (bytes[i - 1] != 0) {
                i = i - 1;
                // 该UCS2字符是字母或数字，则保留该字符
            } else {
                i = i + 1;
            }
        }

        String str = "";
        try {
            str = new String(bytes, 0, i, "Unicode");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return str;
    }

    public static String returnQiyiNum(String num) {
        String ret;

        long tmp = Long.parseLong(num);
        String unit = "";
        String pattern = "##,###.###";
        DecimalFormat df = null;
        df = new DecimalFormat(pattern);
        if (tmp > 100000) {
            tmp = tmp / 10000;
            unit = "万";
        }

        String str = df.format(tmp);
        ret = str + unit;

        return ret;
    }

    /**
     * 检测ids中是否包含id
     *
     * @param id
     * @param ids
     * @return
     */
    public static boolean isContains(String id, String ids) {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(ids)) {
            return false;
        }
        String[] idsArray = ids.split(",");
        for (String idstr : idsArray) {
            if (id.trim().equals(idstr.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取字符串的字节长度
     *
     * @param s
     * @return
     */
    public static int getWordCount(String s) {
        int length = 0;
        for (int i = 0; i < s.length(); i++) {
            int ascii = Character.codePointAt(s, i);
            if (ascii >= 0 && ascii <= 255)
                length++;
            else
                length += 2;

        }
        return length;

    }

    /**
     * 专辑名称去掉特殊符号。
     *
     * @param str
     * @return
     */
    public static String removeSpecial(String str) {
        if (str == null)
            return null;
        str = str.replaceAll("\\.", "");
        str = str.replaceAll("-", "");
        str = str.replaceAll("（", "");
        str = str.replaceAll("）", "");
        str = str.replaceAll("\\)", "");
        str = str.replaceAll("\\(", "");
        str = str.replaceAll("·", "");
        str = str.replaceAll(" ", "");
        str = str.replaceAll("　", "");
        return str;
    }

    public static void main(String[] args) throws Exception {
        //	System.out.println(concat(new Object[]{"\t", "hello", "\t", 4}));

        System.out.println(isContains("123", "123,456"));
        System.out.println(isContains("123", "123"));

        System.out.println(nameSubstring("我们是123 dfsf", 7));
    }

}