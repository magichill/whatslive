package com.letv.whatslive.model.dto;

import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.User;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by gaoshan on 15-7-28.
 */
@Setter
@Getter
public class FollowerDTO {

    private Long userId;
    private String nickName;
    private Integer sex;
    private String userIcon;
    private String introduce;
    private Integer role;
    private Integer status;  //关注状态 0：未关注，1：关注  2：互粉

    public FollowerDTO(User user){
        this.setUserId(ObjectUtils.toLong(user.getUserId(), 0l));
        this.setNickName(user.getNickName());
        this.setRole(ObjectUtils.toInteger(user.getRole(),0));
        this.setIntroduce(user.getIntroduce());
        this.setSex(ObjectUtils.toInteger(user.getSex(),0));
        String pics = user.getPicture();
        if(StringUtils.isNotBlank(pics)){
            String[] picArg = pics.split(",");
            pics = picArg[0];
        }else{
            pics = Constants.USER_ICON_DEFAULT;
        }
        this.setUserIcon(pics);
    }
}
