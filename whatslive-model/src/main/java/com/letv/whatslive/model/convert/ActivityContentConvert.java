package com.letv.whatslive.model.convert;

import com.letv.whatslive.model.ActivityContent;
import com.letv.whatslive.model.utils.ObjUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;

/**
 * Created by gaoshan on 15-10-20.
 */
public class ActivityContentConvert {

    public static DBObject castActivityContentToDBObject(ActivityContent activityContent) {
        DBObject dbo = new BasicDBObject();
        if (activityContent.getId() != null) {
            dbo.put("_id", activityContent.getId());
        }
        dbo.put("actId",activityContent.getActId());
        dbo.put("programId",activityContent.getProgramId());
        DBRef program = new DBRef("program",activityContent.getProgramId());
        dbo.put("program",program);
        dbo.put("priority",activityContent.getPriority());
        dbo.put("status",activityContent.getStatus());
        return dbo;
    }
    public static ActivityContent castDBObjectToActivityContent(DBObject dbObject){
        ActivityContent activityContent = new ActivityContent();
        if(dbObject != null) {
            activityContent.setId(ObjUtils.toLong(dbObject.get("_id")));
            activityContent.setActId(ObjUtils.toLong(dbObject.get("actId")));
            activityContent.setProgramId(ObjUtils.toLong(dbObject.get("programId")));
            activityContent.setPriority(ObjUtils.toLong(dbObject.get("priority")));
            activityContent.setStatus(ObjUtils.toInteger(dbObject.get("status")));
        }else{
            return null;
        }
        return activityContent;
    }
}
