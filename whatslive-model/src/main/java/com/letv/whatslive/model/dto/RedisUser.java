package com.letv.whatslive.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by gaoshan on 15-7-31.
 */
@Setter
@Getter
public class RedisUser {

    private String uid;
    private String nickName;
    private String picture;
    private long date;
    private Integer status;

}
