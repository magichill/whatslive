package com.letv.whatslive.model.dto;

import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.MessageStatus;
import com.letv.whatslive.model.convert.UserConvert;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gaoshan on 15-8-11.
 */
@Getter
@Setter
public class MessageDTO {

    private Long mid;
    private String content;
    private UserDTO user;

    public MessageDTO(MessageStatus messageStatus){
        DBRef message = messageStatus.getMid();
        this.setMid(messageStatus.getId());
        if(message.fetch() != null) {
            this.setContent(ObjectUtils.toString(message.fetch().get("content")));
            DBRef userRef = (DBRef) message.fetch().get("sender");
            if(userRef != null) {
                if(userRef.fetch() != null) {
                    this.setUser(new UserDTO(UserConvert.castDBObjectToUser(userRef.fetch())));
                }
            }
        }

    }
}
