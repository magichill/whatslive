package com.letv.whatslive.model;

import com.mongodb.DBRef;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gaoshan on 15-10-20.
 */

@Getter
@Setter
public class ActivityContent {

    private Long id;
    private Long actId; //活动id
    private Long programId; //program id
    private Integer status; //状态0：有效；1：无效
    private Long priority; //优先级

}
