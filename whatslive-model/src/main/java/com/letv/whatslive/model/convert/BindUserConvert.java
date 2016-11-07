package com.letv.whatslive.model.convert;

import com.letv.whatslive.model.BindUser;
import com.letv.whatslive.model.utils.ObjUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;

/**
 * Created by gaoshan on 15-7-24.
 */
public class BindUserConvert {

    public static DBObject castUserToDBObject(BindUser user) {
        DBObject dbo = new BasicDBObject();
        if (user.getId() != null) {
            dbo.put("_id", user.getId());
        }
        dbo.put("bindType",ObjUtils.ifNull(user.getBindType(),0));
        dbo.put("bindThirdId", ObjUtils.ifNull(user.getBindThirdId(),""));
        dbo.put("createTime", ObjUtils.toLong(user.getCreateTime(), System.currentTimeMillis()));
        dbo.put("bindToken", ObjUtils.toString(user.getBindToken()));
        dbo.put("bindTokenSecret", ObjUtils.toString(user.getBindTokenSecret()));
        return dbo;
    }

    public static BindUser castDBObjectToUser(DBObject dbObject){
        BindUser user = new BindUser();
        if(dbObject.get("_id") != null) {
            user.setId(ObjUtils.toLong(dbObject.get("_id")));
            user.setBindThirdId(ObjUtils.toString(dbObject.get("bindThirdId")));
            user.setBindTokenSecret(ObjUtils.toString(dbObject.get("bindTokenSecret")));
            user.setBindToken(ObjUtils.toString(dbObject.get("bindToken")));
            user.setBindUser((DBRef)dbObject.get("bindUser"));
            user.setBindType(ObjUtils.toInteger(dbObject.get("bindType")));
            user.setCreateTime(ObjUtils.toLong(dbObject.get("createTime")));
        }
        return user;
    }
}
