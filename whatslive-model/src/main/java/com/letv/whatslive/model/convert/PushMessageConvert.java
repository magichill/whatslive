package com.letv.whatslive.model.convert;

import com.letv.whatslive.model.PushMessage;
import com.letv.whatslive.model.utils.ObjUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Created by wangjian7 on 2015/7/28.
 */
public class PushMessageConvert {
    public static DBObject castPushMessageToDBObject(PushMessage pushMessage){
        DBObject dbo = new BasicDBObject();
        if (pushMessage.getId() != null) {
            dbo.put("_id", pushMessage.getId());
        }
        if(pushMessage.getSendTime() != null){
            dbo.put("sendTime", ObjUtils.toLong(pushMessage.getSendTime()));
        }
        dbo.put("content", pushMessage.getContent());
        dbo.put("sendType", ObjUtils.toInteger(pushMessage.getSendType(), -1));
        dbo.put("createUser", pushMessage.getCreateUser());
        dbo.put("createTime", ObjUtils.toLong(pushMessage.getCreateTime(), System.currentTimeMillis()));

        dbo.put("status", ObjUtils.ifNull(pushMessage.getStatus(), 0));
        return dbo;
    }

    public static PushMessage castDBObjectToPushMessage(DBObject dbObject){
        PushMessage pushMessage = new PushMessage();
        if(dbObject != null) {
            pushMessage.setId(ObjUtils.toLong(dbObject.get("_id")));
            pushMessage.setContent(ObjUtils.toString(dbObject.get("content")));
            pushMessage.setSendType(ObjUtils.toInteger(dbObject.get("sendType")));
            pushMessage.setCreateTime(ObjUtils.toLong(dbObject.get("createTime")));
            pushMessage.setSendTime(ObjUtils.toLong(dbObject.get("sendTime"), null));
            pushMessage.setCreateUser(ObjUtils.toString(dbObject.get("createUser")));
            pushMessage.setStatus(ObjUtils.toInteger(dbObject.get("status")));
        }
        return pushMessage;
    }
}
