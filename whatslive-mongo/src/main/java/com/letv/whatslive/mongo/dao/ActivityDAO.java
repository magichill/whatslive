package com.letv.whatslive.mongo.dao;

import com.google.common.collect.Lists;
import com.letv.whatslive.common.utils.DateUtils;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.Activity;
import com.letv.whatslive.model.convert.ActivityConvert;
import com.letv.whatslive.mongo.BaseDAO;
import com.mongodb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by gaoshan on 15-9-24.
 */
@Repository
public class ActivityDAO  extends BaseDAO {

    private final static Logger logger = LoggerFactory.getLogger(ActivityDAO.class);

    protected String collectionName = "activity";

    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }

    /**
     * 根据活动ID查询活动信息
     * @param id
     * @return 查询不到返回null
     */
    public Activity getActivityById(Long id){
        DBObject dbObject = new BasicDBObject();
        dbObject.put("_id", id);
        DBObject result = dbCollection.findOne(dbObject);
        if (result == null) {
            return null;
        }
        Activity activity = ActivityConvert.castDBObjectToActivity(result);
        return activity;
    }

    /**
     * 插入活动信息
     * @param activity
     */
    public long insertActivity(Activity activity){
        DBObject obj = ActivityConvert.castActivityToDBObject(activity);
        return this.insert(obj);
    }

    /**
     * 更新活动信息
     * @param activity
     */
    public void updateActivity(Activity activity) {
        DBObject queryCondition = new BasicDBObject();
        queryCondition.put("_id", activity.getId());
        BasicDBObject setValue = (BasicDBObject)ActivityConvert.castActivityToDBObject(activity);
        //剔除主键修改
        if (setValue.containsField("_id")) {
            setValue.removeField("_id");
        }
        if(activity.getPicture()==null || activity.getPicture().length()==0){
            setValue.removeField("picture");
        }
        DBObject values = new BasicDBObject("$set", setValue);
        this.dbCollection.update(queryCondition, values, false, false);
    }

    /**
     * 查询活动的记录数
     * @param params 查询条件参数
     * @return 满足条件的文档记录数
     */
    public Long countActivityByParams(Map params) {
        //dbObj 类似于 mysql 查询中的 where 关键字，可以添加各种搜索条件
        BasicDBObject query = getBasicDBObjectByParams(params);
        return this.dbCollection.count(query);
    }

    /**
     * 查询活动列表
     * @param orders 查询的排序条件
     * @param params 模糊查询的查询条件
     * @param start  查询条件的开始分页数
     * @param limit  查询条件的分页每页条数
     * @return
     */
    public List<Activity> getActivityListByParams(Map<String, Object> orders, Map<String, Object> params, Integer start, Integer limit) {

        List<Activity> result = Lists.newArrayList();
        if (limit == null) {
            limit = DEFAULT_NUM;
        }
        try {
            //dbObj 类似于 mysql 查询中的 where 关键字，可以添加各种搜索条件
            BasicDBObject query = getBasicDBObjectByParams(params);
            BasicDBObject order = getOrderParams(orders);
            Integer skip = start == null ? 0 : start;
            DBCursor cur = this.dbCollection.find(query).skip(skip)
                    .sort(order).limit(limit);
            while (cur.hasNext()) {
                DBObject dbObject = cur.next();
                Activity activity = ActivityConvert.castDBObjectToActivity(dbObject);
                setValueToActivity(activity);
                result.add(activity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据查询参数Map获取查询条件对象
     * @param params 查询参数Map
     * @return
     */
    private BasicDBObject getBasicDBObjectByParams(Map params){
        //dbObj 类似于 mysql 查询中的 where 关键字，可以添加各种搜索条件
        BasicDBObject query = new BasicDBObject();// 新建查询基类对象 dbo
        if (params != null && params.size() > 0) {
            if (params.get("isRecommend") != null) {
                query.put("isRecommend", ObjectUtils.toInteger(params.get("isRecommend")));
            }
        }
        return query;
    }

    /**
     * 获取排序的BasicDBObject对象
     * @param orders
     * @return
     */
    private BasicDBObject getOrderParams(Map<String, Object> orders) {
        BasicDBObject order = new BasicDBObject();
        if(null != orders &&orders.size()>0){
            for(String key : orders.keySet()){
                order.append(key, Integer.valueOf(String.valueOf(orders.get(key))));
            }
        }else{
            //默认按照创建时间排序
            order.append("createTime", -1);
        }
        return order;
    }

    /**
     * 设置活动信息中的特殊值
     * @param activity
     */
    private void setValueToActivity(Activity activity) {
        activity.setCreateTimeStr(activity.getCreateTime() != null ?
                DateUtils.long2YMDHMS(activity.getCreateTime()) : "");
    }


}
