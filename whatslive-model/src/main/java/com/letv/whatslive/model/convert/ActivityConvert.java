package com.letv.whatslive.model.convert;

import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Activity;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Created by gaoshan on 15-9-24.
 */
public class ActivityConvert {

    public static DBObject castActivityToDBObject(Activity activity) {
        DBObject dbo = new BasicDBObject();
        if (activity.getId() != null) {
            dbo.put("_id", activity.getId());
        }
        dbo.put("title",activity.getTitle());
        dbo.put("status",activity.getStatus());
        dbo.put("picture",activity.getPicture());
        dbo.put("type",activity.getType());
        dbo.put("tag",activity.getTag());
        dbo.put("url",activity.getUrl());
        dbo.put("priority", ObjectUtils.toLong(activity.getPriority(), 0L));
        dbo.put("isRecommend", ObjectUtils.toInteger(activity.getIsRecommend(), 0));
        dbo.put("aDesc", ObjectUtils.toString(activity.getADesc(), ""));
        dbo.put("createTime", ObjectUtils.toLong(activity.getCreateTime(), System.currentTimeMillis()));
        return dbo;
    }

    public static Activity castDBObjectToActivity(DBObject dbObject) {
        Activity activity = new Activity();
        if(dbObject != null) {
            activity.setId(ObjectUtils.toLong(dbObject.get("_id")));
            activity.setTitle(ObjectUtils.toString(dbObject.get("title"), ""));
            activity.setTag(ObjectUtils.toString(dbObject.get("tag"), ""));
            activity.setPicture(ObjectUtils.toString(dbObject.get("picture"), ""));
            activity.setUrl(ObjectUtils.toString(dbObject.get("url"), ""));
            activity.setADesc(ObjectUtils.toString(dbObject.get("aDesc"), ""));
            activity.setStatus(ObjectUtils.toInteger(dbObject.get("status"), 0));
            activity.setPriority(ObjectUtils.toLong(dbObject.get("priority"), 0L));
            activity.setType(ObjectUtils.toInteger(dbObject.get("type"), 1));
            activity.setCreateTime(ObjectUtils.toLong(dbObject.get("createTime"), 0L));
            activity.setIsRecommend(ObjectUtils.toInteger(dbObject.get("isRecommend"), 0));
            return activity;
        }else{
            return null;
        }

    }
}
