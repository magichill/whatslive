package com.letv.whatslive.server.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Activity;
import com.letv.whatslive.model.convert.ActivityConvert;
import com.letv.whatslive.mongo.dao.ActivityContentDAO;
import com.letv.whatslive.mongo.dao.ActivityDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by gaoshan on 15-9-24.
 */
@Service
public class ActivityService {

    @Resource
    private ActivityDAO activityDAO;

    @Resource
    private ActivityContentDAO activityContentDAO;

    public void insertActivity(Activity activity){
        DBObject dbo = ActivityConvert.castActivityToDBObject(activity);
        activityDAO.insert(dbo);
    }

    public List<Activity> getActivityList(){
        DBObject query = new BasicDBObject();
        query.put("status",1);
        query.put("isRecommend",1);
        DBObject order = new BasicDBObject();
        order.put("_id",-1);
        List<DBObject> list = activityDAO.findAll(query,order);
        List<Activity> result = Lists.newArrayList();
        for(DBObject dbo : list){
            result.add(ActivityConvert.castDBObjectToActivity(dbo));
        }
        return result;
    }

    public List<Activity> getActivityList(Integer start,Integer limit,Long timestamp){
        Map<String,Object> params = Maps.newHashMap();
        params.put("status",1);
        if(timestamp != null && timestamp > 0) {
            params.put("createTime", new BasicDBObject("$lt", timestamp));
        }
        DBObject order = new BasicDBObject();
        order.put("priority",-1);
        order.put("_id",-1);
        List<DBObject> list = activityDAO.selectAll(start,limit,params,order);
        List<Activity> result = Lists.newArrayList();
        Map<String,Object> contentParams = Maps.newHashMap();
        for(DBObject obj : list){
            contentParams.put("actId", ObjectUtils.toLong(obj.get("_id")));
            Long watchNum = activityContentDAO.countList(contentParams);
            Activity activity = ActivityConvert.castDBObjectToActivity(obj);
            activity.setWatchNum(watchNum);
            result.add(activity);
        }
        return result;
    }

    public Activity getActivityById(Long id){
        return activityDAO.getActivityById(id);
    }
}
