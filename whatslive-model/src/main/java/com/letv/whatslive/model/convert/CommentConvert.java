package com.letv.whatslive.model.convert;

import com.letv.whatslive.model.Comment;
import com.letv.whatslive.model.utils.ObjUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Created by gaoshan on 15-7-10.
 */
public class CommentConvert {

    public static DBObject castCommentToDBObject(Comment comment) {
        DBObject dbo = new BasicDBObject();
        if (comment.getCId() > 0) {
            dbo.put("_id", comment.getCId());
        } else {
            return null;
        }
        dbo.put("content", ObjUtils.ifNull(comment.getContent(), ""));
        dbo.put("pId",ObjUtils.ifNull(comment.getPId(),0L));
        dbo.put("postId", ObjUtils.ifNull(comment.getPostId(), 0L));
        dbo.put("status", 1);
        dbo.put("createTime", ObjUtils.toLong(comment.getCreateTime(), System.currentTimeMillis()));
        dbo.put("updateTime", ObjUtils.toLong(comment.getUpdateTime(), System.currentTimeMillis()));

        return dbo;
    }

    public static Comment castDBObjectToComment(DBObject dbObject){
        Comment comment = new Comment();
        comment.setCId(ObjUtils.toLong(dbObject.get("cId")));
        comment.setContent(ObjUtils.toString(dbObject.get("content")));
        comment.setPId(ObjUtils.toLong(dbObject.get("pId")));
        comment.setPostId(ObjUtils.toLong(dbObject.get("postId")));
        return comment;
    }
}
