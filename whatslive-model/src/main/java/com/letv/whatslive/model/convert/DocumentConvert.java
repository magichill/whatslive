package com.letv.whatslive.model.convert;

import com.letv.whatslive.model.Document;
import com.letv.whatslive.model.utils.ObjUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Created by wangjian7 on 2015/7/27.
 */
public class DocumentConvert {
    public static DBObject castDocumentToDBObject(Document document){
        DBObject dbo = new BasicDBObject();
        if (document.getId() != null) {
            dbo.put("_id", document.getId());
        }
        dbo.put("version", document.getVersion());
        dbo.put("comment", document.getComment());
        dbo.put("createUser", document.getCreateUser());
        dbo.put("createTime", ObjUtils.toLong(document.getCreateTime(), System.currentTimeMillis()));
        dbo.put("status", ObjUtils.ifNull(document.getStatus(), 0));
        return dbo;
    }

    public static Document castDBObjectToDocument(DBObject dbObject){
        Document document = new Document();
        if(dbObject != null) {
            document.setId(ObjUtils.toLong(dbObject.get("_id")));
            document.setVersion(ObjUtils.toString(dbObject.get("version")));
            document.setComment(ObjUtils.toString(dbObject.get("comment")));
            document.setCreateTime(ObjUtils.toLong(dbObject.get("createTime")));
            document.setCreateUser(ObjUtils.toString(dbObject.get("createUser")));
            document.setStatus(ObjUtils.toInteger(dbObject.get("status")));
        }

        return document;
    }
}
