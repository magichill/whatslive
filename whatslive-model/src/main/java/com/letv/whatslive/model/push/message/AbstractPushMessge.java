package com.letv.whatslive.model.push.message;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * Created by wangjian7 on 2015/8/14.
 */
public abstract class AbstractPushMessge implements Serializable {
    public abstract String toJsonString();
}
