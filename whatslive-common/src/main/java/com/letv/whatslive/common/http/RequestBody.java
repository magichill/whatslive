package com.letv.whatslive.common.http;

import java.util.Map;

/**
 * Created by zoran on 14-9-15.
 */
public class RequestBody {

    private String cmd;

    private String sid;

    private Map<String,Object> data;


    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
