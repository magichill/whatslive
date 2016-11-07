package com.letv.whatslive.mongo.dao;

import com.letv.whatslive.model.Comment;
import com.letv.whatslive.model.convert.CommentConvert;
import com.letv.whatslive.mongo.BaseDAO;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.springframework.stereotype.Repository;

/**
 * Created by gaoshan on 15-7-10.
 */
@Repository
public class CommentDAO extends BaseDAO {


    protected String collectionName = "comment";

    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }

    public int insertComment(Comment comment){
        DBObject dbObject = CommentConvert.castCommentToDBObject(comment);
        WriteResult result = this.dbCollection.save(dbObject);
        return result.getN();
    }
}
