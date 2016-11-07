package com.letv.whatslive.model.dto;

import com.letv.whatslive.common.utils.Constants;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.User;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by gaoshan on 15-7-3.
 */
@Getter
@Setter
public class UserDTO {

    private Long userId;
    private String nickName;
    private int sex;
    private String email;
    private String userIcon;
    private String introduce;
    private int role;

    public UserDTO(User user){
        this.setUserId(ObjectUtils.toLong(user.getUserId(),0l));
        this.setNickName(user.getNickName());
        this.setRole(ObjectUtils.toInteger(user.getRole(),0));
        this.setSex(ObjectUtils.toInteger(user.getSex(),0));
        this.setEmail(user.getEmail());
        this.setIntroduce(ObjectUtils.toString(user.getIntroduce(),""));
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
