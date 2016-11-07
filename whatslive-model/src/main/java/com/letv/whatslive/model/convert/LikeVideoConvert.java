package com.letv.whatslive.model.convert;

import com.letv.whatslive.model.LikeVideo;
import com.letv.whatslive.model.utils.ObjUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Created by gaoshan on 15-7-14.
 */
public class LikeVideoConvert {

    public static DBObject castLikeVideoToDBObject(LikeVideo likeVideo) {
        DBObject dbo = new BasicDBObject();
        if (likeVideo.getId() > 0) {
            dbo.put("_id", likeVideo.getId());
        }

        dbo.put("userId",likeVideo.getUserId());
        dbo.put("vid",likeVideo.getVid());
        dbo.put("status",likeVideo.getStatus());
        dbo.put("createTime", ObjUtils.toLong(likeVideo.getCreateTime(), System.currentTimeMillis()));
        dbo.put("updateTime", ObjUtils.toLong(likeVideo.getUpdateTime(), System.currentTimeMillis()));
        return dbo;
    }

    public static LikeVideo castDBObjectToLikeVideo(DBObject obj) {
        LikeVideo likeVideo = new LikeVideo();
        likeVideo.setUserId(ObjUtils.toLong(obj.get("userId")));
        likeVideo.setStatus(ObjUtils.toInteger(obj.get("status")));
        likeVideo.setVid(ObjUtils.toLong(obj.get("vid")));
        likeVideo.setCreateTime(ObjUtils.toLong(obj.get("createTime")));
        likeVideo.setUpdateTime(ObjUtils.toLong(obj.get("updateTime")));
        return likeVideo;
    }

}
