package com.letv.whatslive.web.util.util;

import java.util.UUID;

/**
 * Title:
 * Desc:
 * User: crespo
 * Company: www.gitv.cn
 * Date: 13-7-28 上午11:50
 */
public class UUIDGenerator {
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-", "");
    }
}
