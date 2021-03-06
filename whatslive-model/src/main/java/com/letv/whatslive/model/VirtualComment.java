package com.letv.whatslive.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by wangjian7 on 2015/7/28.
 */
@Getter
@Setter
public class VirtualComment {
    private Long id;
    private String content;
    private String createUser;
    private Long createTime;
    private Integer status; //1：启用，2：停用

    private String createTimeStr; //创建时间字符串，用于页面显示，显示格式"yyyy-MM-dd HH-mm-ss"
}
