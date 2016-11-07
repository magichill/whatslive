package com.letv.whatslive.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by wangjian7 on 2015/9/21.
 */
@Getter
@Setter
public class RobotRule {
    private int userWatchTime = 20; //虚拟用户进入定时任务周期
    private int userLikeTime = 60; //虚拟用户点赞定时任务周期
    private int userComeInMaxNum = 10; //虚拟用户一次进入最大数量
    private int userComeInMinNum = 5; //虚拟用户一次进入最少数量
    private int userOutMaxNum = 3; // 虚拟用户一次退出最大数量
    private int userOutMinNum = 1;  //虚拟用户一次退出最小数量
    private int userLikeMaxNum = 20; // 一次点赞定时任务最大虚拟用户数量
    private int userLikeMinNum = 10;  //一次点赞定时任务最小虚拟用户数量
    private int userLikeNum = 200;  //一次点赞总的数量
    private int userComeInLimitMin = 40; //限制一个聊天室最多虚拟用户数的最小数量
    private int userComeInLimitMax = 90; //限制一个聊天室最多虚拟用户数的最大数量
    private int userOutLimit = 10; //限制一个聊天室当超过多少虚拟用户才开始退出

    private int userCommentTime = 5*60; //虚拟用户评论定时任务周期
    private int userCommentNum = 15;  //一次评论执行的评论数

}
