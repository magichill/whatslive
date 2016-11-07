package com.letv.whatslive.common.http.weibo.data;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author pangjie1@letv.com
 * @date 14-12-18 下午2:37
 * @Description
 */
public class TagData {

    @JSONField(name = "display_name")
    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
