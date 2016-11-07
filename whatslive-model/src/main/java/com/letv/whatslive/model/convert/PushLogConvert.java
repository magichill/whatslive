package com.letv.whatslive.model.convert;

import com.letv.whatslive.model.PushLog;
import com.letv.whatslive.model.utils.ObjUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Created by gaoshan on 15-7-13.
 */
public class PushLogConvert {


    public static DBObject castPushLogToDBObject(PushLog pushLog) {
        DBObject dbo = new BasicDBObject();
        if (pushLog.getId() != null) {
            dbo.put("_id", pushLog.getId());
        }
        if (pushLog.getBussikey() != null) {
            dbo.put("bussikey", pushLog.getBussikey());
        }
        if (pushLog.getMessage() != null) {
            dbo.put("message", pushLog.getMessage());
        }
        if (pushLog.getTagName() != null) {
            dbo.put("tagName", pushLog.getTagName());
        }
        if (pushLog.getSendTime() != null) {
            dbo.put("sendTime", pushLog.getSendTime());
        }
        if (pushLog.getBussiType() != null) {
            dbo.put("bussiType", pushLog.getBussiType());
        }
        dbo.put("type", pushLog.getType());
        dbo.put("status", ObjUtils.toInteger(pushLog.getStatus()));
        if(pushLog.getStartTime() != null){
            dbo.put("startTime", pushLog.getStartTime());
        }
        dbo.put("startTime", ObjUtils.toLong(pushLog.getStartTime()));
        if(pushLog.getPushStartTime() != null){
            dbo.put("pushStartTime", pushLog.getPushStartTime());
        }
        if(pushLog.getPushEndTime() != null){
            dbo.put("pushEndTime", pushLog.getPushEndTime());
        }
        dbo.put("createTime", pushLog.getCreateTime());
        return dbo;
    }

    public static PushLog castDBObjectToPushLog(DBObject dbObject){
        PushLog pushLog = new PushLog();
        if(dbObject != null) {
            pushLog.setId(ObjUtils.toLong(dbObject.get("_id")));
            pushLog.setBussikey(ObjUtils.toString(dbObject.get("bussikey")));
            pushLog.setMessage(ObjUtils.toString(dbObject.get("message")));
            pushLog.setTagName(ObjUtils.toString(dbObject.get("tagName")));
            pushLog.setSendTime(ObjUtils.toLong(dbObject.get("sendTime")));
            pushLog.setBussiType(ObjUtils.toInteger(dbObject.get("bussiType")));
            pushLog.setType(ObjUtils.toInteger(dbObject.get("type")));
            pushLog.setStatus(ObjUtils.toInteger(dbObject.get("status")));
            pushLog.setStartTime(ObjUtils.toLong(dbObject.get("startTime")));
            pushLog.setPushStartTime(ObjUtils.toLong(dbObject.get("pushStartTime")));
            pushLog.setPushEndTime(ObjUtils.toLong(dbObject.get("pushEndTime")));
            pushLog.setCreateTime(ObjUtils.toLong(dbObject.get("createTime")));
        }else{
            return null;
        }
        return pushLog;
    }
}
