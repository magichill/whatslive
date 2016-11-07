package com.letv.whatslive.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by gaoshan on 15-8-7.
 */
@Getter
@Setter
public class TokenVo {

    private String appkey;
    private String outkey;

    public TokenVo(String appkey, String outkey) {
        this.appkey = appkey;
        this.outkey = outkey;
    }
}
