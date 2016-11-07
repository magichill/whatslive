package com.letv.whatslive.model.convert;

import com.letv.whatslive.model.FeedBack;
import com.letv.whatslive.model.utils.Constant;
import com.letv.whatslive.model.utils.ObjUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Created by gaoshan on 15-8-31.
 */
public class FeedBackConvert {

    public static DBObject castFeedBackToDBObject(FeedBack feedBack) {
        DBObject dbo = new BasicDBObject();
        if (feedBack.getId() != null) {
            dbo.put("_id", feedBack.getId());
        }
        dbo.put("imei", ObjUtils.ifNull(feedBack.getImei(), ""));
        dbo.put("ip",ObjUtils.ifNull(feedBack.getIp(),""));
        dbo.put("userId", ObjUtils.toLong(feedBack.getUserId()));
        dbo.put("programId",ObjUtils.toString(feedBack.getProgramId()));
        dbo.put("phone",ObjUtils.toString(feedBack.getPhone(),""));
        dbo.put("lastGetPlayUrlTime", ObjUtils.toLong(feedBack.getLastGetPlayUrlTime()));
        dbo.put("lastPlayTime", ObjUtils.toLong(feedBack.getLastPlayTime()));
        dbo.put("lastTotalPlayTime", ObjUtils.toLong(feedBack.getLastTotalPlayTime(),0l));
        dbo.put("feedBack", ObjUtils.ifNull(feedBack.getFeedback(),""));
        return dbo;
    }
}
