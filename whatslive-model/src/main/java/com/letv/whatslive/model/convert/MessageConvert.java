package com.letv.whatslive.model.convert;

import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Message;
import com.letv.whatslive.model.MessageStatus;
import com.letv.whatslive.model.utils.ObjUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;

/**
 * Created by gaoshan on 15-8-7.
 */
public class MessageConvert {

    public static DBObject castMessageToDBObject(Message message) {
        DBObject dbo = new BasicDBObject();
        if (message.getId() != null) {
            dbo.put("_id", message.getId());
        }
        dbo.put("sender",new DBRef("user",message.getSenderId()));
        dbo.put("senderId",message.getSenderId());
        dbo.put("content",message.getContent());
        dbo.put("type",message.getType());
        dbo.put("status", ObjUtils.ifNull(message.getStatus(), 1));
        dbo.put("createTime", ObjUtils.toLong(message.getCreateTime(), System.currentTimeMillis()));
        dbo.put("updateTime", ObjUtils.toLong(message.getUpdateTime(), System.currentTimeMillis()));
        return dbo;
    }

    public static DBObject castMessageStatusToDBObject(MessageStatus messageStatus) {
        DBObject dbo = new BasicDBObject();
        if (messageStatus.getId() != null) {
            dbo.put("_id", messageStatus.getId());
        }
        dbo.put("mId",messageStatus.getMid());
        dbo.put("recId",messageStatus.getRecId());
        dbo.put("status", ObjUtils.ifNull(messageStatus.getStatus(), 1));
        dbo.put("createTime", ObjUtils.toLong(messageStatus.getCreateTime(), System.currentTimeMillis()));
        dbo.put("updateTime", ObjUtils.toLong(messageStatus.getUpdateTime(), System.currentTimeMillis()));
        return dbo;
    }

    public static MessageStatus castDBObjectToMessageStatus(DBObject dbObject){
        if(dbObject != null){
            MessageStatus messageStatus = new MessageStatus();
            messageStatus.setId(ObjectUtils.toLong(dbObject.get("_id")));
            messageStatus.setMid((DBRef)dbObject.get("mId"));
            messageStatus.setStatus(ObjectUtils.toInteger(dbObject.get("status")));
            messageStatus.setRecId(ObjectUtils.toLong(dbObject.get("recId")));
            messageStatus.setCreateTime(ObjectUtils.toLong(dbObject.get("createTime")));
            messageStatus.setUpdateTime(ObjectUtils.toLong(dbObject.get("updateTime")));
            return messageStatus;
        }else{
            return null;
        }
    }
}
