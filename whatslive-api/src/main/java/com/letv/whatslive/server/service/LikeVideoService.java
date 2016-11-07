package com.letv.whatslive.server.service;

import com.letv.whatslive.model.LikeVideo;
import com.letv.whatslive.model.convert.LikeVideoConvert;
import com.letv.whatslive.mongo.dao.LikeVideoDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by gaoshan on 15-7-14.
 */
@Service
public class LikeVideoService {

    private final static Logger logger = LoggerFactory.getLogger(LikeVideoService.class);

    @Resource
    private LikeVideoDAO likeVideoDAO;

    public void insert(LikeVideo likeVideo){
        if(likeVideo != null && likeVideo.getId()>0){
            DBObject query = new BasicDBObject();
            query.put("_id",likeVideo.getId());
            DBObject update = new BasicDBObject();
            update.put("status",likeVideo.getStatus());
            likeVideoDAO.update(query,new BasicDBObject("$set",update));
        }else{
            DBObject obj = LikeVideoConvert.castLikeVideoToDBObject(likeVideo);
            likeVideoDAO.insert(obj);
        }

    }

    public LikeVideo getLikeVideo(long userId,long vid){
        DBObject query = new BasicDBObject();
        query.put("userId",userId);
        query.put("vid",vid);
        DBObject obj = likeVideoDAO.find(query);
        if(obj == null){
            return null;
        }else{
            return LikeVideoConvert.castDBObjectToLikeVideo(obj);
        }
    }
}
