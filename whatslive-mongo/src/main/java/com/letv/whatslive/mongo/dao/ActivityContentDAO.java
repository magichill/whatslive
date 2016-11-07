package com.letv.whatslive.mongo.dao;

import com.google.common.collect.Lists;
import com.letv.whatslive.common.utils.ObjectUtils;
import com.letv.whatslive.model.ActivityContent;
import com.letv.whatslive.model.convert.ActivityContentConvert;
import com.letv.whatslive.model.utils.ObjUtils;
import com.letv.whatslive.mongo.BaseDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by gaoshan on 15-10-20.
 */
@Repository
public class ActivityContentDAO extends BaseDAO {

    private static final Logger logger = LoggerFactory.getLogger(ActivityContentDAO.class);

    protected String collectionName = "activityContent";

    @Override
    protected void init() {
        super.init(this.collectionName);
    }

    @Override
    protected long getAutoIncrementId() {
        return idGenerate.generateId(this.collectionName);
    }


    public List<Long> getProgramIdsByParams(Map<String, Object> params, Map<String, Object> orders, Integer start, Integer limit) {
        List<Long> result = Lists.newLinkedList();
        try {
            //dbObj 类似于 mysql 查询中的 where 关键字，可以添加各种搜索条件
            BasicDBObject query = getBasicDBObjectByParams(params);
            BasicDBObject order = getOrderParams(orders);
            Integer skip = start == null ? 0 : start;
            DBCursor cur = this.dbCollection.find(query).skip(skip)
                    .sort(order).limit(limit);
            while (cur.hasNext()) {
                DBObject dbObject = cur.next();
                result.add(ObjUtils.toLong(dbObject.get("programId")));
            }
        } catch (Exception e) {
            logger.error("get activity programIds by params error!", e);
        }
        return result;
    }

    public ActivityContent getActivityContent(Map<String, Object> params){
        ActivityContent activityContent = null;
        try {
            BasicDBObject query = getBasicDBObjectByParams(params);
            DBObject result = dbCollection.findOne(query);
            if (result != null) {
                activityContent = ActivityContentConvert.castDBObjectToActivityContent(result);
            }
        } catch (Exception e) {
            logger.error("get activityContent by params error!", e);
        }
        return activityContent;
    }

    public Long countProgramIdsByParams(Map<String, Object> params) {
        Long result = 0L;
        try {
            //dbObj 类似于 mysql 查询中的 where 关键字，可以添加各种搜索条件
            BasicDBObject query = getBasicDBObjectByParams(params);
            result = this.dbCollection.count(query);
        } catch (Exception e) {
            logger.error("get activity programIds by params error!", e);
        }
        return result;
    }

    public long insertActivityContent(ActivityContent activityContent){
        try {
            DBObject obj = ActivityContentConvert.castActivityContentToDBObject(activityContent);
            return this.insert(obj);
        } catch (Exception e) {
            logger.error("get activity programIds by params error!", e);
        }
        return 0L;
    }

    public void delActivityContent(Map<String, Object> params){
        try {
            //dbObj 类似于 mysql 查询中的 where 关键字，可以添加各种搜索条件
            BasicDBObject query = getBasicDBObjectByParams(params);
            this.delete(query);
        } catch (Exception e) {
            logger.error("delete activity programIds by params error!", e);
        }
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
            if (params.get("actId") != null) {
                query.put("actId", ObjectUtils.toLong(params.get("actId")));
            }
            if (params.get("programId") != null) {
                query.put("programId", ObjectUtils.toLong(params.get("programId")));
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
}
