package com.letv.whatslive.send.service;

/**
 * Created by wangjian7 on 2015/8/24.
 */
public class BaiduBaseService {
    public Long getSendTIme(long sendTime){
        long time = sendTime/1000;//转换成秒
        if(time>System.currentTimeMillis()/1000+60&&time<System.currentTimeMillis()/1000+31536000){
            return time;
        }else{
            return null;
        }
    }
}
