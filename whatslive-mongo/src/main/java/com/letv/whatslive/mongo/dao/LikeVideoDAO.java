package com.letv.whatslive.mongo.dao;

import com.letv.whatslive.model.LikeVideo;
import com.letv.whatslive.model.convert.LikeVideoConvert;
import com.letv.whatslive.mongo.BaseDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.stereotype.Repository;

/**
 * Created by gaoshan on 15-7-14.
 */
@Repository
public class LikeVideoDAO extends BaseDAO {

    protected String collectionName = "likeVideo";

    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }

    public void saveOrUpdate(LikeVideo likeVideo){
        if(likeVideo != null && likeVideo.getId() > 0){
            DBObject query = new BasicDBObject();
            query.put("_id",likeVideo.getId());
            DBObject update = this.find(query);
            update.put("status",likeVideo.getStatus());
            update.put("updateTime",System.currentTimeMillis());
            this.update(query,update);
        } else{
            this.insert(LikeVideoConvert.castLikeVideoToDBObject(likeVideo));
        }
    }
}
