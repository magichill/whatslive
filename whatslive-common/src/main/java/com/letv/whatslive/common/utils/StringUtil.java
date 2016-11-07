package com.letv.whatslive.common.utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zoran on 14-11-10.
 */
public class StringUtil {

    public static String regExtractor(String content, String reg) {
        String result = "";
        Matcher matcher;
        Pattern pattern;
        pattern = Pattern.compile(reg);
        matcher = pattern.matcher(content);
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount() ; i++) {
                if (result.equals("")) {
                    result = matcher.group(i);
                } else {
                    if (!matcher.group(i).isEmpty()) {
                        result = result + "," + matcher.group(i);
                    } else {
                        result = result + ",null";
                    }
                }
            }
        }
        return result;
    }


    public static String filterUTF8MB4(String text) throws UnsupportedEncodingException {
        byte[] bytes = text.getBytes("utf-8");
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        int i = 0;
        while (i < bytes.length) {
            short b = bytes[i];
            if (b > 0) {
                buffer.put(bytes[i++]);
                continue;
            }
            b += 256;
            if ((b ^ 0xC0) >> 4 == 0) {
                buffer.put(bytes, i, 2);
                i += 2;
            }
            else if ((b ^ 0xE0) >> 4 == 0) {
                buffer.put(bytes, i, 3);
                i += 3;
            }
            else if ((b ^ 0xF0) >> 4 == 0) {
                i += 4;
            }
        }
        buffer.flip();
        return new String(buffer.array(), "utf-8");
    }


}
