package com.letv.whatslive.send.constants;


import com.letv.whatslive.send.utils.PropertyGetter;

/**
 * Created by wangjian7 on 2015/8/15.
 */
public class PushCosntants {
    public static int BAIDU_PUSH_IOS_DEPLOY_STATUS = PropertyGetter.getInt("baidu.push.ios.deployStatus")==0?
            1:PropertyGetter.getInt("baidu.push.ios.deployStatus");
}
