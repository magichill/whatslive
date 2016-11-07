package com.letv.whatslive.model.convert;

import com.letv.whatslive.model.Tag;
import com.letv.whatslive.model.utils.ObjUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Created by wangjian7 on 2015/8/19.
 */
public class TagConvert {
    public static DBObject castTagToDBObject(Tag tag) {
        DBObject dbo = new BasicDBObject();
        if (tag.getId() != null) {
            dbo.put("_id", tag.getId());
        }
        dbo.put("value", tag.getValue());
        dbo.put("type", tag.getType());
        return dbo;
    }
    public static Tag castDBObjectToTag(DBObject dbObject){
        Tag tag = new Tag();
        if(dbObject != null) {
            tag.setId(ObjUtils.toLong(dbObject.get("_id")));
            tag.setType(ObjUtils.toInteger(dbObject.get("type"), 0));
            tag.setValue(ObjUtils.toString(dbObject.get("value")));
        }else{
            return null;
        }
        return tag;
    }
}
