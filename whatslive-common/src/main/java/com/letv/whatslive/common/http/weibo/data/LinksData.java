package com.letv.whatslive.common.http.weibo.data;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author pangjie1@letv.com
 * @date 14-12-18 下午2:34
 * @Description
 */
public class LinksData {

    private String url;

    private String scheme;

    @JSONField(name = "display_name")
    private String displayName;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
