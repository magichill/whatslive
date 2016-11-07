package com.letv.whatslive.model.convert;

import com.letv.whatslive.model.Keyword;
import com.letv.whatslive.model.utils.ObjUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Created by wangjian7 on 2015/7/22.
 */
public class KeywordConvert {
    public static DBObject castKeywordToDBObject(Keyword keyword){
        DBObject dbo = new BasicDBObject();
        if (keyword.getId() != null) {
            dbo.put("_id", keyword.getId());
        }
        dbo.put("key", keyword.getKey());
        dbo.put("createUser", keyword.getCreateUser());
        dbo.put("createTime", ObjUtils.toLong(keyword.getCreateTime(), System.currentTimeMillis()));
        dbo.put("status", ObjUtils.ifNull(keyword.getStatus(), 1));
        return dbo;
    }

    public static Keyword castDBObjectToKeyword(DBObject dbObject){
        Keyword keyword = new Keyword();
        if(dbObject != null) {
            keyword.setId(ObjUtils.toLong(dbObject.get("_id")));
            keyword.setKey(ObjUtils.toString(dbObject.get("key")));
            keyword.setCreateTime(ObjUtils.toLong(dbObject.get("createTime")));
            keyword.setCreateUser(ObjUtils.toString(dbObject.get("createUser")));
            keyword.setStatus(ObjUtils.toInteger(dbObject.get("status")));
        }

        return keyword;
    }
}
