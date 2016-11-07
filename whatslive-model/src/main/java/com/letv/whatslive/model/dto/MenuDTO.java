package com.letv.whatslive.model.dto;

import com.letv.whatslive.common.utils.ObjectUtils;
import com.mongodb.DBObject;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gaoshan on 15-11-3.
 */
@Getter
@Setter
public class MenuDTO {

    private Long menuId;
    private String menuName;
    private String command;

    public MenuDTO(){

    }
    public MenuDTO(DBObject object){
        this.setMenuId(ObjectUtils.toLong(object.get("_id")));
        this.setMenuName(ObjectUtils.toString(object.get("value")));
        this.setCommand(ObjectUtils.toString(object.get("commend")));
    }

}
