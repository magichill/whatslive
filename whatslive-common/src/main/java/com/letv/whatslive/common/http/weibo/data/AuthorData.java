package com.letv.whatslive.common.http.weibo.data;

import com.alibaba.fastjson.annotation.JSONField;


public class AuthorData {

    @JSONField(name = "display_name")
    private String displayName;

    private String url;

    @JSONField(name = "object_type")
    private String objectType;


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
}
