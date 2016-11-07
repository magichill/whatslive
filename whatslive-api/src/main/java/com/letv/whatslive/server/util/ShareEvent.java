package com.letv.whatslive.server.util;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by gaoshan on 15-9-1.
 */
@Getter
@Setter
public class ShareEvent implements Serializable {

    private String accessToken;
    private String shareText;

    public ShareEvent(){

    }


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public String toLogString(){
        StringBuffer sb = new StringBuffer();
        sb.append("ShareEvent{");
        sb.append("accessToken=").append(accessToken);
        sb.append(", shareText=").append(shareText).append("}");
        return sb.toString();
    }
}
