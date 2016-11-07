package com.letv.whatslive.common.http.weibo.data;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author pangjie1@letv.com
 * @date 14-12-18 下午2:32
 * @Description
 */
public class StreamData {

    private String url;

    @JSONField(name = "hd_url")
    private String hdUrl;

    private String format;

    private int duration;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHdUrl() {
        return hdUrl;
    }

    public void setHdUrl(String hdUrl) {
        this.hdUrl = hdUrl;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
