package com.letv.whatslive.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by wangjian7 on 2015/8/19.
 */
@Getter
@Setter
public class Tag {
    private Long id;
    private String value;
    private int type; //0:用户标签，1：直播标签
    private String selected; //是否选中
}
