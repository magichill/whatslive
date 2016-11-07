package com.letv.whatslive.model;

import com.mongodb.DBRef;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gaoshan on 15-7-23.
 */

/**
 * 预约model
 */
@Getter
@Setter
public class Subscribe {

    private Long id;
    private Long userId;
    private DBRef program;
    private Long programId;
    private int status; //1:用户已预约  0:取消预约
    private Long createTime;
    private Long updateTime;

}
