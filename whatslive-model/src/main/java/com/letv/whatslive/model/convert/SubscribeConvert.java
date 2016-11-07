package com.letv.whatslive.model.convert;

import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Subscribe;
import com.letv.whatslive.model.utils.ObjUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;

/**
 * Created by gaoshan on 15-7-23.
 */
public class SubscribeConvert {

    public static DBObject castSubscribeToDBObject(Subscribe subscribe) {
        DBObject dbo = new BasicDBObject();
        if (subscribe.getId() != null) {
            dbo.put("_id", subscribe.getId());
        }
        dbo.put("userId", ObjUtils.toLong(subscribe.getUserId()));
        dbo.put("status",ObjUtils.toInteger(subscribe.getStatus()));
        dbo.put("programId",ObjUtils.toInteger(subscribe.getProgramId()));
        dbo.put("createTime", ObjUtils.toLong(subscribe.getCreateTime(), System.currentTimeMillis()));
        dbo.put("updateTime", ObjUtils.toLong(subscribe.getUpdateTime(), System.currentTimeMillis()));
        return dbo;
    }

    public static Subscribe castDBObjectToSubscribe(DBObject dbObject){
        Subscribe subscribe = new Subscribe();
        subscribe.setId(ObjUtils.toLong(dbObject.get("_id")));
        subscribe.setProgram((DBRef) dbObject.get("program"));
        subscribe.setUserId(ObjUtils.toLong(dbObject.get("userId")));
        subscribe.setProgramId(ObjectUtils.toLong(dbObject.get("programId")));
        subscribe.setStatus(ObjUtils.toInteger(dbObject.get("status")));
        subscribe.setCreateTime(ObjUtils.toLong(dbObject.get("createTime")));
        subscribe.setUpdateTime(ObjUtils.toLong(dbObject.get("updateTime")));
        return subscribe;
    }
}
