package com.letv.whatslive.model.convert;

import com.letv.whatslive.model.VirtualComment;
import com.letv.whatslive.model.utils.ObjUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Created by wangjian7 on 2015/7/22.
 */
public class VirtualCommentConvert {
    public static DBObject castVirtualCommentToDBObject(VirtualComment comment){
        DBObject dbo = new BasicDBObject();
        if (comment.getId() != null) {
            dbo.put("_id", comment.getId());
        }
        dbo.put("content", comment.getContent());
        dbo.put("createUser", comment.getCreateUser());
        dbo.put("createTime", ObjUtils.toLong(comment.getCreateTime(), System.currentTimeMillis()));
        dbo.put("status", ObjUtils.ifNull(comment.getStatus(), 1));
        return dbo;
    }

    public static VirtualComment castDBObjectToVirtualComment(DBObject dbObject){
        VirtualComment comment = new VirtualComment();
        if(dbObject != null) {
            comment.setId(ObjUtils.toLong(dbObject.get("_id")));
            comment.setContent(ObjUtils.toString(dbObject.get("content")));
            comment.setCreateTime(ObjUtils.toLong(dbObject.get("createTime")));
            comment.setCreateUser(ObjUtils.toString(dbObject.get("createUser")));
            comment.setStatus(ObjUtils.toInteger(dbObject.get("status")));
        }

        return comment;
    }
}
