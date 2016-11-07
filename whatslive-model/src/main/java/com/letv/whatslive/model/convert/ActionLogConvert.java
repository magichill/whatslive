package com.letv.whatslive.model.convert;

import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.ActionLog;
import com.letv.whatslive.model.redis.chat.protocol.ChatEvent;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Map;

/**
 * Created by gaoshan on 15-9-24.
 */
public class ActionLogConvert {

    public static DBObject castActionLogToDBObject(ActionLog actionLog) {
        DBObject dbo = new BasicDBObject();
        if (actionLog.getId() != null) {
            dbo.put("_id", actionLog.getId());
        }
        dbo.put("uid",actionLog.getUid());
        dbo.put("roomId",actionLog.getRoomId());
        dbo.put("action",actionLog.getAction());
        dbo.put("type",actionLog.getType());
        dbo.put("content",actionLog.getContent());
        dbo.put("voice", actionLog.getVoice());
        dbo.put("date", actionLog.getDate());
        return dbo;
    }

    public static ActionLog castDBObjectToActionLog(DBObject dbObject) {
        ActionLog actionLog = new ActionLog();
        if(dbObject != null) {
            actionLog.setId(ObjectUtils.toLong(dbObject.get("_id")));
            actionLog.setUid(ObjectUtils.toString(dbObject.get("uid"), ""));
            actionLog.setRoomId(ObjectUtils.toString(dbObject.get("roomId"), ""));
            actionLog.setAction(ObjectUtils.toInteger(dbObject.get("action")));
            actionLog.setType(ObjectUtils.toInteger(dbObject.get("type")));
            actionLog.setContent(ObjectUtils.toString(dbObject.get("content"), ""));
            actionLog.setVoice(ObjectUtils.toString(dbObject.get("voice"), ""));
            actionLog.setDate(ObjectUtils.toLong(dbObject.get("date"), 0L));
            return actionLog;
        }else{
            return null;
        }
    }

    public static Map<String, Object> castDBObjectToMap(DBObject dbObject, long programStartTime) {
        Map<String, Object> actionLogMap = Maps.newHashMap();
        if(dbObject != null) {
            actionLogMap.put("uid",ObjectUtils.toString(dbObject.get("uid"), ""));
            actionLogMap.put("action", ObjectUtils.toInteger(dbObject.get("action")));
            actionLogMap.put("type", ObjectUtils.toInteger(dbObject.get("type")));
            actionLogMap.put("content", ObjectUtils.toString(dbObject.get("content"), ""));
            actionLogMap.put("voice", ObjectUtils.toString(dbObject.get("voice"), ""));
            actionLogMap.put("date", ObjectUtils.toLong(dbObject.get("date"), 0L) - programStartTime);
            return actionLogMap;
        }else{
            return null;
        }
    }

    public static ActionLog castChatEventToActionLog(ChatEvent chatEvent) {
        ActionLog actionLog = new ActionLog();
        actionLog.setUid(chatEvent.getUid());
        actionLog.setRoomId(chatEvent.getRoomId());
        actionLog.setAction(chatEvent.getAction());
        actionLog.setType(chatEvent.getType());
        actionLog.setContent(chatEvent.getContent());
        actionLog.setVoice(chatEvent.getVoice());
        actionLog.setDate(System.currentTimeMillis());
//        actionLog.setDate(chatEvent.getDate());
        return actionLog;
    }
}
