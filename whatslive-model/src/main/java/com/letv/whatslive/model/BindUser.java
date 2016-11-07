package com.letv.whatslive.model;

import com.mongodb.DBRef;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gaoshan on 15-7-24.
 */

@Getter
@Setter
public class BindUser {

    private Long id;
    private DBRef bindUser;
    private Integer bindType;  //1:facebook 2:twitter 4:weibo 5:微信
    private String bindThirdId; //绑定的第三方Id
    private String bindToken;
    private String bindTokenSecret; //twitter登陆保存字段
    private Long createTime;

}
