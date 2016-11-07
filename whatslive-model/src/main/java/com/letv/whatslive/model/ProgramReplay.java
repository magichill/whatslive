package com.letv.whatslive.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 直播回放
 * Created by wangjian7 on 2015/10/20.
 */
@Getter
@Setter
public class ProgramReplay {
    private Long id; //_id
    private Long pid; //直播id
    private int status; //状态，0：表示未生成回放，1：表示生成回放中，2：表示回放文件已经生成并上传CDN，3：表示生成回放成功，4:表示生成回放失败，5:表示没有回放日志，不生成回放。
    private String logURL; // 直播回放文件的url
    private Long createTime; // 创建时间
    private Long updateTime; // 修改时间
    private int retryNum; //重试次数

}
